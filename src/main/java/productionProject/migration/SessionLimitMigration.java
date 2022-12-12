package productionProject.migration;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import lombok.extern.slf4j.Slf4j;
import productionProject.Main;
import productionProject.Util;

@Slf4j
public class SessionLimitMigration extends AbstractMigration {

    private PreparedStatement sSelectBillfoldLimit;

    private PreparedStatement sInsertTitanLimit;
    private PreparedStatement sUpdateTitanLimit;
    private PreparedStatement sInsertTitanLimitChangeHistory;
    private PreparedStatement sFindExistingLimitInTitan;

    public SessionLimitMigration(
        final Connection billfoldConnection,
        final Connection titanLimitConnection
    ) throws SQLException {
        // Prepare SQL statements

        // billfold limit statements
        sSelectBillfoldLimit = billfoldConnection.prepareStatement(
            "SELECT TIME_LIMIT_AMOUNT, TIME_LIMIT_PERIOD, TIME_LIMIT_START_TIME FROM " + Main.BILLFOLD_TABLE_PREFIX + "GAMING_LIMIT WHERE FK_USER_ID = ?");

        // titan_limit statements
        sInsertTitanLimit = titanLimitConnection.prepareStatement(
            "INSERT INTO `LIMIT` (PLAYER_ID, LIMIT_TYPE, PERIOD_TYPE, SET_TIME, CURRENT_PERIOD_START_TIME, CURRENT_PERIOD_END_TIME, RENEW, AMOUNT, USED_AMOUNT, VERSION, EFFECTIVE_TIME) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS);
        sInsertTitanLimit.setString(2, Main.SESSION_LIMIT_TYPE);
        sInsertTitanLimit.setInt(10, 0);
        sInsertTitanLimit.setNull(11, Types.TIMESTAMP);

        sUpdateTitanLimit = titanLimitConnection
            .prepareStatement("UPDATE `LIMIT` SET SET_TIME=?, CURRENT_PERIOD_START_TIME=?, CURRENT_PERIOD_END_TIME=?, RENEW=?, AMOUNT=?, USED_AMOUNT=? WHERE ID=?");

        sInsertTitanLimitChangeHistory = titanLimitConnection
            .prepareStatement("INSERT INTO LIMIT_CHANGE_HISTORY (CREATED_TIME, LIMIT_ID, PLAYER_ID, SITE_ID, USER_ID, ACTION, AMOUNT_BEFORE, AMOUNT_AFTER, VERSION) VALUES (?,?,?,?,?,?,?,?,?)");
        sInsertTitanLimitChangeHistory.setString(6, "createLimit");
        sInsertTitanLimitChangeHistory.setBigDecimal(7, BigDecimal.ZERO);
        sInsertTitanLimitChangeHistory.setInt(9, 0);

        sFindExistingLimitInTitan = titanLimitConnection.prepareStatement("SELECT ID, AMOUNT FROM `LIMIT` WHERE PLAYER_ID=? AND LIMIT_TYPE=? AND PERIOD_TYPE=?");
        sFindExistingLimitInTitan.setString(2, Main.SESSION_LIMIT_TYPE);

        statements.add(sSelectBillfoldLimit);
        statements.add(sInsertTitanLimit);
        statements.add(sInsertTitanLimitChangeHistory);
        statements.add(sUpdateTitanLimit);
        statements.add(sFindExistingLimitInTitan);
    }

    public void migrate(
        final Long userId,
        final Long playerId,
        final Integer siteId
    ) throws SQLException {
        log.debug("Migrating session limit for: user id: {}, site id: {}, player id: {}", userId, siteId, playerId);

        sSelectBillfoldLimit.setLong(1, userId);
        ResultSet billfoldLimitRS = sSelectBillfoldLimit.executeQuery();

        while (billfoldLimitRS.next()) {
            BigDecimal timeLimitAmount    = billfoldLimitRS.getBigDecimal("TIME_LIMIT_AMOUNT");
            int        timeLimitPeriod    = billfoldLimitRS.getInt("TIME_LIMIT_PERIOD");
            Timestamp  timeLimitTimeStart = billfoldLimitRS.getTimestamp("TIME_LIMIT_START_TIME");

            timeLimitAmount = timeLimitAmount.multiply(new BigDecimal(60));

            String periodType = timeLimitPeriod == 0 ? "daily" : timeLimitPeriod == 1 ? "weekly" : "monthly";

            saveLimitAndLimitChangeHistory(userId, playerId, periodType, siteId, timeLimitAmount, timeLimitTimeStart);
        }

    }

    private void saveLimitAndLimitChangeHistory(
        Long userId,
        Long playerId,
        String periodType,
        Integer siteId,
        BigDecimal limitAmount,
        Timestamp setTime
    ) throws SQLException {
        sInsertTitanLimit.setString(3, periodType);

        Long limitId = saveTitanLimit(playerId, userId, periodType, limitAmount, setTime);

        sInsertTitanLimitChangeHistory.setTimestamp(1, setTime);
        sInsertTitanLimitChangeHistory.setLong(2, limitId);
        sInsertTitanLimitChangeHistory.setLong(3, playerId);
        sInsertTitanLimitChangeHistory.setLong(4, siteId);
        sInsertTitanLimitChangeHistory.setLong(5, userId);
        sInsertTitanLimitChangeHistory.setBigDecimal(8, limitAmount);
        sInsertTitanLimitChangeHistory.executeUpdate();
    }

    private Long saveTitanLimit(
        Long playerId,
        Long userId,
        String periodType,
        BigDecimal limitAmount,
        Timestamp setTime
    ) throws SQLException {
        // 1. get existing limit by playerId and the PERIOD_TYPE from titan
        // 2. if not found, insert
        // 3. if found, compare the limit amount and use the smaller value

        // 1. get existing limit by playerId and the PERIOD_TYPE from titan
        Long       limitId        = null;
        BigDecimal existingAmount = null;
        sFindExistingLimitInTitan.setLong(1, playerId);
        sFindExistingLimitInTitan.setString(3, periodType);
        ResultSet existingLimitRs = sFindExistingLimitInTitan.executeQuery();
        if (existingLimitRs != null && existingLimitRs.next()) {
            limitId = existingLimitRs.getLong("ID");
            existingAmount = existingLimitRs.getBigDecimal("AMOUNT");
        }

        DatePair limitPeriod = Util.calculateCurrentPeriod(periodType, setTime, true, Main.LIMIT_END_TIME_MODE, 2);

        if (existingAmount != null && existingAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 3. if found, compare the limit amount and use the smaller value
            if (existingAmount.compareTo(limitAmount) > 0) {
                // existingAmount is greater that limitAmount, update to use new
                sUpdateTitanLimit.setTimestamp(1, setTime);
                sUpdateTitanLimit.setTimestamp(2, new Timestamp(limitPeriod.getStartTime().getTime()));
                sUpdateTitanLimit.setTimestamp(3, new Timestamp(limitPeriod.getEndTime().getTime()));
                sUpdateTitanLimit.setBoolean(4, true);
                sUpdateTitanLimit.setBigDecimal(5, limitAmount);
                sUpdateTitanLimit.setBigDecimal(6, BigDecimal.ZERO);
                sUpdateTitanLimit.setLong(7, limitId);
                sUpdateTitanLimit.executeUpdate();
            } else {
                // existingAmount is equal or smaller than limitAmount, keep the existing limit
            }
        } else {
            // 2. if not found, insert
            sInsertTitanLimit.setLong(1, playerId);
            sInsertTitanLimit.setTimestamp(4, setTime);
            sInsertTitanLimit.setTimestamp(5, new Timestamp(limitPeriod.getStartTime().getTime()));
            sInsertTitanLimit.setTimestamp(6, new Timestamp(limitPeriod.getEndTime().getTime()));
            sInsertTitanLimit.setBoolean(7, true);
            sInsertTitanLimit.setBigDecimal(8, limitAmount);
            sInsertTitanLimit.setBigDecimal(9, BigDecimal.ZERO);
            sInsertTitanLimit.executeUpdate();

            limitId = getInsertedId(sInsertTitanLimit);
        }

        return limitId;
    }

}
