package productionProject.migration;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import productionProject.Main;
import productionProject.Util;

@Slf4j
public abstract class AbstractMigration {
    protected List<PreparedStatement> statements = new ArrayList<>();
    protected PreparedStatement       sInsertTitanLimit;
    protected PreparedStatement       sUpdateTitanLimit;
    protected PreparedStatement       sInsertTitanLimitChangeHistory;
    protected PreparedStatement       sFindExistingLimitInTitan;
    protected PreparedStatement       sSelectUsagesAmountInTitan;


    public AbstractMigration() {

    }

    public AbstractMigration(
        final Connection billfoldConnection,
        final Connection titanLimitConnection
    ) throws SQLException {

        sInsertTitanLimit = titanLimitConnection.prepareStatement(
            "INSERT INTO `LIMIT` (PLAYER_ID, LIMIT_TYPE, PERIOD_TYPE, SET_TIME, CURRENT_PERIOD_START_TIME, CURRENT_PERIOD_END_TIME, RENEW, AMOUNT, USED_AMOUNT, VERSION, EFFECTIVE_TIME) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS);
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

        sSelectUsagesAmountInTitan = titanLimitConnection.prepareStatement("SELECT IFNULL(SUM(LUH.AMOUNT), 0) AS SUM_AMOUNT "
                                                                               + "FROM LIMIT_USAGE LU "
                                                                               + "       JOIN LIMIT_USAGE_HISTORY LUH ON LUH.LIMIT_USAGE_ID = LU.ID AND LUH.LIMIT_ID = ? AND DIRECTION = ? "
                                                                               + "WHERE LU.PLAYER_ID = ? "
                                                                               + "  AND LUH.CREATE_TIME BETWEEN ? AND ? "
                                                                               + "  AND (SELECT COUNT(*) FROM LIMIT_USAGE_HISTORY WHERE LIMIT_USAGE_HISTORY.LIMIT_USAGE_ID = LU.ID AND LIMIT_USAGE_HISTORY.LIMIT_ID = ? AND LIMIT_USAGE_HISTORY.DIRECTION = ?) = 0");
        sSelectUsagesAmountInTitan.setString(2, "increase");
        sSelectUsagesAmountInTitan.setString(7, "decrease");

        statements.add(sInsertTitanLimit);
        statements.add(sUpdateTitanLimit);
        statements.add(sInsertTitanLimitChangeHistory);
        statements.add(sFindExistingLimitInTitan);
        statements.add(sSelectUsagesAmountInTitan);
    }

    Long getInsertedId(final PreparedStatement statement) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        } else {
            throw new RuntimeException("Empty id after insert");
        }
    }

    public void closeStatements() throws SQLException {
        for (PreparedStatement statement : statements) {
            statement.close();
        }
    }

    protected void saveLimitAndLimitChangeHistory(
        Long userId,
        Long playerId,
        String periodType,
        Integer siteId,
        BigDecimal limitAmount,
        boolean limitRenew,
        Timestamp setTime,
        BigDecimal newLimitAmount,
        Timestamp newLimitEffectiveTime
    ) throws SQLException {
        sInsertTitanLimit.setString(3, periodType);

        Long limitId = saveTitanLimit(playerId, userId, periodType, limitAmount, limitRenew, setTime, newLimitAmount, newLimitEffectiveTime);

        log.debug("   Save Limit change history - userId: {}, playerId: {}, periodType {}", userId, playerId, periodType);
        sInsertTitanLimitChangeHistory.setTimestamp(1, setTime);
        sInsertTitanLimitChangeHistory.setLong(2, limitId);
        sInsertTitanLimitChangeHistory.setLong(3, playerId);
        sInsertTitanLimitChangeHistory.setLong(4, siteId);
        sInsertTitanLimitChangeHistory.setLong(5, userId);
        sInsertTitanLimitChangeHistory.setBigDecimal(8, limitAmount);
        sInsertTitanLimitChangeHistory.executeUpdate();
    }

    protected Long saveTitanLimit(
        Long playerId,
        Long userId,
        String periodType,
        BigDecimal limitAmount,
        boolean limitRenew,
        Timestamp setTime,
        BigDecimal newLimitAmount,
        Timestamp newLimitEffectiveTime
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

        DatePair limitPeriod = Util.calculateCurrentPeriod(periodType, setTime, limitRenew, Main.LIMIT_END_TIME_MODE, 2);

        log.debug("  Save or update Limit  - userId: {}, playerId: {}, periodType {}, limitAmount: {}, startTime: {}, endTime: {}", userId, playerId, periodType, limitAmount,
                  limitPeriod.getStartTime(), limitPeriod.getEndTime());
        if (existingAmount != null && existingAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 3. if found, compare the limit amount and use the smaller value
            if (existingAmount.compareTo(limitAmount) > 0) {
                if (Dates.isDateInPeriod(new Date(), limitPeriod.getStartTime(), limitPeriod.getEndTime())) {
                    // existingAmount is greater that limitAmount, update to use new
                    sUpdateTitanLimit.setTimestamp(1, setTime);
                    sUpdateTitanLimit.setTimestamp(2, new Timestamp(limitPeriod.getStartTime().getTime()));
                    sUpdateTitanLimit.setTimestamp(3, new Timestamp(limitPeriod.getEndTime().getTime()));
                    sUpdateTitanLimit.setBoolean(4, limitRenew);
                    sUpdateTitanLimit.setBigDecimal(5, limitAmount);
                    sUpdateTitanLimit.setBigDecimal(6, calculateUsedAmount(playerId, limitId, limitPeriod));
                    sUpdateTitanLimit.setTimestamp(7, newLimitEffectiveTime);
                    sUpdateTitanLimit.setBigDecimal(8, newLimitAmount);
                    sUpdateTitanLimit.setLong(9, limitId);
                    sUpdateTitanLimit.executeUpdate();
                }
            }
        } else {
            // 2. if not found, insert
            sInsertTitanLimit.setLong(1, playerId);
            sInsertTitanLimit.setTimestamp(4, setTime);
            sInsertTitanLimit.setTimestamp(5, new Timestamp(limitPeriod.getStartTime().getTime()));
            sInsertTitanLimit.setTimestamp(6, new Timestamp(limitPeriod.getEndTime().getTime()));
            sInsertTitanLimit.setBoolean(7, limitRenew);
            sInsertTitanLimit.setBigDecimal(8, limitAmount);
            sInsertTitanLimit.setBigDecimal(9, BigDecimal.ZERO);
            sInsertTitanLimit.setTimestamp(11, newLimitEffectiveTime);
            sInsertTitanLimit.executeUpdate();

            limitId = getInsertedId(sInsertTitanLimit);
        }

        return limitId;
    }

    protected BigDecimal calculateUsedAmount(
        final Long playerId,
        final Long limitId,
        final DatePair limitPeriod
    ) throws SQLException {
        log.debug("Calculating usedAmount for player {}, limit id {}, start time: {}, end time: {}", playerId, limitId, new Timestamp(limitPeriod.getStartTime().getTime()),
                  new Timestamp(limitPeriod.getEndTime().getTime()));
        BigDecimal amount = BigDecimal.ZERO;
        sSelectUsagesAmountInTitan.setLong(1, limitId);
        sSelectUsagesAmountInTitan.setLong(3, playerId);
        sSelectUsagesAmountInTitan.setTimestamp(4, new Timestamp(limitPeriod.getStartTime().getTime()));
        sSelectUsagesAmountInTitan.setTimestamp(5, new Timestamp(limitPeriod.getEndTime().getTime()));
        sSelectUsagesAmountInTitan.setLong(6, limitId);
        ResultSet existedUsedAmountRS = sSelectUsagesAmountInTitan.executeQuery();

        if (existedUsedAmountRS.next()) {
            log.debug("found usage");
            amount = existedUsedAmountRS.getBigDecimal("SUM_AMOUNT");
        }

        log.debug("Calculated amount: {}", amount);

        return amount;
    }

    protected int countResultSetSize(final ResultSet rs) throws SQLException {
        int size = 0;
        if (rs != null) {
            rs.last();    // moves cursor to the last row
            size = rs.getRow(); // get row id
            rs.first();   // moves cursor back to the start
        }
        return size;
    }

    protected boolean isValidString(final String s) {
        return s != null && !s.isEmpty();
    }

    protected String composeFirstMiddleLastNameString(final List<String> strList) {
        StringBuilder result = new StringBuilder();
        for (String s : strList) {
            result.append(s == null ? "" : s.trim().toLowerCase());
        }
        return result.toString();
    }

    //  if country is in PLAYER_LINKING_COUNTRIES config return true
    protected boolean isCountryInPlayerLinkingCountriesList(final String country) {
        return isValidString(country) && (Main.PLAYER_LINKING_COUNTRIES.contains("ALL") || Main.PLAYER_LINKING_COUNTRIES.contains(country));
    }
}
