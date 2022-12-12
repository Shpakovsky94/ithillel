package productionProject.youngPlayerLimitUpdate.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import productionProject.Main;
import productionProject.migration.AbstractMigration;
import productionProject.youngPlayerLimitUpdate.domain.Limit;
import productionProject.youngPlayerLimitUpdate.domain.User;
import productionProject.youngPlayerLimitUpdate.enums.LimitChangeAction;

@Slf4j
public class LimitService extends AbstractMigration {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LimitChangeHistoryService       limitChangeHistoryService;
    public LimitHistoryService             limitHistoryService;
    public LimitUpdateAffectedUsersService limitUpdateAffectedUsersService;

    public final PreparedStatement sSelectTitanLimit;
    public final PreparedStatement sSelectTitanLimitWithEffectiveTime;
    public final PreparedStatement sInsertTitanLimit;
    public final PreparedStatement sUpdateAmountTitanLimit;
    public final PreparedStatement sUpdateAmountInClearedLimit;
    public final PreparedStatement sClearTitanLimit;

    public LimitService(
        final Connection billfoldConnection,
        final Connection titanLimitConnection
    ) throws SQLException {
        limitChangeHistoryService = new LimitChangeHistoryService(titanLimitConnection);
        limitHistoryService = new LimitHistoryService(billfoldConnection);
        limitUpdateAffectedUsersService = new LimitUpdateAffectedUsersService(billfoldConnection);

        // Prepare SQL statements
        sSelectTitanLimit = titanLimitConnection.prepareStatement(
            "SELECT * FROM `LIMIT` WHERE LIMIT_TYPE = ? AND PLAYER_ID = ?");

        sSelectTitanLimitWithEffectiveTime = titanLimitConnection.prepareStatement(
            "SELECT * FROM `LIMIT` WHERE LIMIT_TYPE = ? AND PLAYER_ID = ? AND EFFECTIVE_TIME IS NOT NULL");

        sInsertTitanLimit = titanLimitConnection.prepareStatement(
            "INSERT INTO "
                + "`LIMIT` (PLAYER_ID, LIMIT_TYPE, PERIOD_TYPE, SET_TIME, CURRENT_PERIOD_START_TIME, "
                + "CURRENT_PERIOD_END_TIME, RENEW, AMOUNT, USED_AMOUNT, EFFECTIVE_TIME, VERSION, SITE_ID, USER_ID)"
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        sUpdateAmountTitanLimit = titanLimitConnection.prepareStatement(
            "UPDATE `LIMIT` SET AMOUNT = ?, EFFECTIVE_TIME = NULL, NEW_AMOUNT = NULL, VERSION = ? WHERE ID = ?");

        sUpdateAmountInClearedLimit = titanLimitConnection.prepareStatement(
            "UPDATE `LIMIT` SET SET_TIME = ?, CURRENT_PERIOD_START_TIME = ?, CURRENT_PERIOD_END_TIME = ?, RENEW = ?, AMOUNT = ?, EFFECTIVE_TIME = ?, NEW_AMOUNT = ?, VERSION = ? WHERE ID = ?");

        sClearTitanLimit = titanLimitConnection.prepareStatement(
            "UPDATE `LIMIT` SET AMOUNT = NULL, RENEW = NULl, CURRENT_PERIOD_START_TIME = NULL, CURRENT_PERIOD_END_TIME = NULL, EFFECTIVE_TIME = NULL, NEW_AMOUNT = NULL, USED_AMOUNT = NULL, SET_TIME = NULL, VERSION = ? WHERE ID = ?");

        statements.add(sSelectTitanLimit);
        statements.add(sSelectTitanLimitWithEffectiveTime);
        statements.add(sInsertTitanLimit);
        statements.add(sUpdateAmountTitanLimit);
        statements.add(sUpdateAmountInClearedLimit);
        statements.add(sClearTitanLimit);
    }

    public void createLimit(
        final User user,
        final Limit newLimit,
        final BigDecimal amountToSet,
        final Long boUserId
    ) throws SQLException, ParseException {

        // create new limit to the titan_limit.LIMIT
        Long insertedLimitPk = insertIntoTitanLimit(newLimit, amountToSet);
        log.debug("Created new {} {} LIMIT limitId: {}: amount: {}", newLimit.getLimitType(), newLimit.getPeriodType(), insertedLimitPk, amountToSet);

        //set id from inserted limit
        newLimit.setId(insertedLimitPk);

        // create record in titan_limit.LIMIT_CHANGE_HISTORY
        limitChangeHistoryService.insert(newLimit, LimitChangeAction.createLimit, amountToSet, boUserId);

        // create record in billfold.LIMIT_HISTORY
        limitHistoryService.insert(newLimit, LimitChangeAction.createLimit, amountToSet, boUserId);

        // create record in billfold.LIMIT_UPDATE_AFFECTED_USERS
        limitUpdateAffectedUsersService.insert(user, newLimit.getLimitType(), newLimit.getPeriodType(), null, amountToSet, Main.PHASE);
    }

    public void updateLimitWithNewAmount(
        final User user,
        final Limit oldLimit,
        final BigDecimal newAmount,
        final Long boUserId
    ) throws SQLException, ParseException {

        // update limit amount in titan_limit.LIMIT

        if (oldLimit.getAmount() != null) {
            updateAmount(oldLimit, newAmount);
            log.debug("Updated amount for LIMIT limitId: {}: old amount: {}, new amount: {}", oldLimit.getId(), oldLimit.getAmount(), newAmount);
        } else {
            // if limit's amount is null, it means limit was cleaned and we need update it with new data
            buildClearedLimitToUpdate(oldLimit);

            updateAmountInClearedLimit(oldLimit, newAmount);
            log.debug("Updated cleared LIMIT limitId: {}: new amount: {}", oldLimit.getId(), newAmount);
        }

        // create record in titan_limit.LIMIT_CHANGE_HISTORY
        limitChangeHistoryService.insert(oldLimit, LimitChangeAction.modifyLimit, newAmount, boUserId);

        // create record in billfold.LIMIT_HISTORY
        limitHistoryService.insert(oldLimit, LimitChangeAction.modifyLimit, newAmount, boUserId);

        // create record in billfold.LIMIT_UPDATE_AFFECTED_USERS
        limitUpdateAffectedUsersService.insert(user, oldLimit.getLimitType(), oldLimit.getPeriodType(), oldLimit.getAmount(), newAmount, Main.PHASE);
    }

    public void removeLimitData(
        final User user,
        final Limit limit,
        final Long boUserId
    ) throws SQLException {
        // clear limit in titan_limit.LIMIT
        clearTitanLimit(limit);
        log.debug("LIMIT limitId: {} was cleared", limit.getId());

        // create record in titan_limit.LIMIT_CHANGE_HISTORY
        limitChangeHistoryService.insert(limit, LimitChangeAction.clearLimit, null, boUserId);

        // create record in billfold.LIMIT_HISTORY
        limitHistoryService.insert(limit, LimitChangeAction.clearLimit, null, boUserId);

        // create record in billfold.LIMIT_UPDATE_AFFECTED_USERS
        limitUpdateAffectedUsersService.insert(user, limit.getLimitType(), limit.getPeriodType(), limit.getAmount(), null, Main.PHASE);
    }

    private void updateAmount(
        final Limit oldLimit,
        final BigDecimal newAmount
    ) throws SQLException {

        sUpdateAmountTitanLimit.setBigDecimal(1, newAmount);

        long version;
        try {
            version = Long.parseLong(oldLimit.getVersion()) + 1;
        } catch (Exception e) {
            version = 0L;
        }
        sUpdateAmountTitanLimit.setLong(2, version);
        sUpdateAmountTitanLimit.setLong(3, oldLimit.getId());
        sUpdateAmountTitanLimit.executeUpdate();
    }

    private void clearTitanLimit(
        final Limit oldLimit
    ) throws SQLException {

        long version;
        try {
            version = Long.parseLong(oldLimit.getVersion()) + 1;
        } catch (Exception e) {
            version = 0L;
        }
        sClearTitanLimit.setLong(1, version);
        sClearTitanLimit.setLong(2, oldLimit.getId());
        sClearTitanLimit.executeUpdate();
    }

    private void updateAmountInClearedLimit(
        final Limit clearedLimit,
        final BigDecimal newAmount
    ) throws SQLException, ParseException {

        Timestamp currentPeriodStartTime = new Timestamp(formatter.parse(clearedLimit.getCurrentPeriodStartTime()).getTime());
        sUpdateAmountInClearedLimit.setTimestamp(1, currentPeriodStartTime);
        sUpdateAmountInClearedLimit.setTimestamp(2, currentPeriodStartTime);

        Timestamp currentPeriodEndTime = new Timestamp(formatter.parse(clearedLimit.getCurrentPeriodEndTime()).getTime());
        sUpdateAmountInClearedLimit.setTimestamp(3, currentPeriodEndTime);

        sUpdateAmountInClearedLimit.setBoolean(4, true);
        sUpdateAmountInClearedLimit.setBigDecimal(5, newAmount);
        sUpdateAmountInClearedLimit.setNull(6, Types.TIMESTAMP);
        sUpdateAmountInClearedLimit.setNull(7, Types.DECIMAL);

        long version;
        try {
            version = Long.parseLong(clearedLimit.getVersion()) + 1;
        } catch (Exception e) {
            version = 0L;
        }
        sUpdateAmountInClearedLimit.setLong(8, version);
        sUpdateAmountInClearedLimit.setLong(9, clearedLimit.getId());
        sUpdateAmountInClearedLimit.executeUpdate();
    }


    private Long insertIntoTitanLimit(
        final Limit newLimit,
        final BigDecimal amount
    ) throws SQLException, ParseException {
        Long insertedLimitPk = null;

        sInsertTitanLimit.setLong(1, newLimit.getPlayerId());
        sInsertTitanLimit.setString(2, newLimit.getLimitType());
        sInsertTitanLimit.setString(3, newLimit.getPeriodType());

        Timestamp currentPeriodStartTime = new Timestamp(formatter.parse(newLimit.getCurrentPeriodStartTime()).getTime());
        sInsertTitanLimit.setTimestamp(4, currentPeriodStartTime);
        sInsertTitanLimit.setTimestamp(5, currentPeriodStartTime);

        Timestamp currentPeriodEndTime = new Timestamp(formatter.parse(newLimit.getCurrentPeriodEndTime()).getTime());
        sInsertTitanLimit.setTimestamp(6, currentPeriodEndTime);

        sInsertTitanLimit.setBoolean(7, true);
        sInsertTitanLimit.setBigDecimal(8, amount);
        sInsertTitanLimit.setNull(9, Types.DECIMAL);
        sInsertTitanLimit.setNull(10, Types.TIMESTAMP);
        sInsertTitanLimit.setInt(11, 0);
        sInsertTitanLimit.setInt(12, newLimit.getSiteId());
        sInsertTitanLimit.setLong(13, newLimit.getUserId());
        sInsertTitanLimit.executeUpdate();

        ResultSet rs = sInsertTitanLimit.getGeneratedKeys();
        if (rs.next()) {
            insertedLimitPk = rs.getLong(1);
        }
        return insertedLimitPk;
    }

    public Limit buildLimitToInsert(
        final User user,
        final String limitType,
        final String periodType
    ) {
        Date currentPeriodStartTime = DateService.getCurrentDatetime();
        Date currentPeriodEndTime   = DateService.getCurrentPeriodEndTime(periodType);

        Limit limitToInsert = new Limit();
        limitToInsert.setPlayerId(user.getTitanPlayerId());
        limitToInsert.setLimitType(limitType);
        limitToInsert.setPeriodType(periodType);
        limitToInsert.setSetTime(formatter.format(currentPeriodStartTime));
        limitToInsert.setCurrentPeriodStartTime(formatter.format(currentPeriodStartTime));
        limitToInsert.setCurrentPeriodEndTime(formatter.format(currentPeriodEndTime));
        limitToInsert.setSiteId(user.getSiteId());
        limitToInsert.setUserId(user.getUserId());
        return limitToInsert;
    }

    private void buildClearedLimitToUpdate(
        Limit limit
    ) {
        Date currentPeriodStartTime = DateService.getCurrentDatetime();
        Date currentPeriodEndTime   = DateService.getCurrentPeriodEndTime(limit.getPeriodType());

        limit.setSetTime(formatter.format(currentPeriodStartTime));
        limit.setCurrentPeriodStartTime(formatter.format(currentPeriodStartTime));
        limit.setCurrentPeriodEndTime(formatter.format(currentPeriodEndTime));
        limit.setRenew(true);
    }

    public Map<String, Limit> retrieveLimitsFromDbByLimitTypeAndPlayerId(
        final String limitType,
        final Long playerId
    ) throws SQLException {
        Map<String, Limit> limitList = new LinkedHashMap<>();

        if (limitType != null && !limitType.equals("") && playerId != null) {
            sSelectTitanLimit.setString(1, limitType);
            sSelectTitanLimit.setLong(2, playerId);
            ResultSet limitRs = sSelectTitanLimit.executeQuery();

            while (limitRs.next()) {
                Limit limitToPut = Limit.buildLimitFromRs(limitRs);
                limitList.put(limitToPut.getPeriodType(), limitToPut);
            }
            limitRs.close();
        }
        return limitList;
    }

    public List<Limit> retrievePendingLimitsFromDb(
        final String limitType,
        final Long playerId
    ) throws SQLException {
        List<Limit> limitList = new ArrayList<>();

        if (limitType != null && !limitType.equals("") && playerId != null) {
            sSelectTitanLimitWithEffectiveTime.setString(1, limitType);
            sSelectTitanLimitWithEffectiveTime.setLong(2, playerId);
            ResultSet limitRs = sSelectTitanLimitWithEffectiveTime.executeQuery();

            while (limitRs.next()) {
                Limit limitToPut = Limit.buildLimitFromRs(limitRs);
                limitList.add(limitToPut);
            }
            limitRs.close();
        }
        return limitList;
    }
}
