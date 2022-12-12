package productionProject.migration;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import productionProject.Main;

@Slf4j
public class DepositLimitUsageMigration extends AbstractMigration {

    private Logger loggerPaymentLimitUsageUpdateSql = LoggerFactory.getLogger("paymentLimitUsageUpdateSql");

    private PreparedStatement sSelectBillfoldPayment;
    private PreparedStatement sInsertLimitUsage;
    private PreparedStatement sInsertLimitUsageHistory;
    private PreparedStatement sSelectTitanLimitsForPlayer;
    private PreparedStatement sUpdateLimitUsedAmount;
    private PreparedStatement sUpdateBillfoldLimitUsageId;

    public DepositLimitUsageMigration(
        final Connection billfoldConnection,
        final Connection titanPlayerConnection,
        final Connection titanLimitConnection
    ) throws SQLException {
        // Select billfold.PAYMENT
        sSelectBillfoldPayment = billfoldConnection.prepareStatement(
            "SELECT PK_PAYMENT_ID, PAYMENT.SITE_ID, FK_USER_ID, CREATED_TIME, OK_TIME, (IFNULL(AMOUNT, 0) + IFNULL(FEE, 0)) as USAGE_AMOUNT, PAYMENT_IP, PAYMENT_DEVICE_TYPE, PAYMENT_USER_AGENT, PAYMENT_COUNTRY"
                + " FROM " + Main.BILLFOLD_TABLE_PREFIX + Main.BILLFOLD_TABLE_PAYMENT_NAME_WITHOUT_PREFIX + " AS PAYMENT"
                + " JOIN `" + Main.BILLFOLD_TABLE_PREFIX + "USER`"
                + " ON PK_USER_ID = FK_USER_ID"
                + " WHERE TITAN_PLAYER_ID = ?"
                + " AND DIRECTION = ?"
                + " AND PAYMENT.STATUS = ?"
                + " AND OK_TIME BETWEEN ? AND ?"
                + " AND LIMIT_USAGE_ID IS NULL"
                + " ORDER BY OK_TIME ASC");
        sSelectBillfoldPayment.setInt(2, 0);
        sSelectBillfoldPayment.setInt(3, 1);

        // Update LIMIT_USAGE_ID in PAYMENT
        sUpdateBillfoldLimitUsageId = billfoldConnection.prepareStatement("UPDATE " + Main.BILLFOLD_TABLE_PREFIX + "PAYMENT SET LIMIT_USAGE_ID = ? WHERE PK_PAYMENT_ID = ?");

        // Select titan_limit.LIMIT for player
        sSelectTitanLimitsForPlayer = titanLimitConnection
            .prepareStatement("SELECT ID, PERIOD_TYPE, AMOUNT, USED_AMOUNT, CURRENT_PERIOD_START_TIME, CURRENT_PERIOD_END_TIME FROM `LIMIT` WHERE LIMIT_TYPE = ? AND PLAYER_ID = ? ");
        sSelectTitanLimitsForPlayer.setString(1, "deposit");

        // Insert titan_limit.LIMIT_USAGE
        sInsertLimitUsage = titanLimitConnection.prepareStatement("INSERT INTO LIMIT_USAGE (CREATED_TIME, PLAYER_ID, SITE_ID, USER_ID, VERSION) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        sInsertLimitUsage.setInt(5, 0);

        // Insert titan_limit.LIMIT_USAGE_HISTORY
        sInsertLimitUsageHistory = titanLimitConnection.prepareStatement(
            "INSERT INTO LIMIT_USAGE_HISTORY (CREATE_TIME, LIMIT_USAGE_ID, LIMIT_ID, DIRECTION, AMOUNT, USED_AMOUNT_BEFORE, USED_AMOUNT_AFTER, LIMIT_REACHED, CLIENT_IP, CLIENT_COUNTRY, CLIENT_USER_AGENT, CLIENT_DEVICE_TYPE, VERSION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
        sInsertLimitUsageHistory.setString(4, "increase");
        sInsertLimitUsageHistory.setInt(13, 0);

        // Update titan_limit.USED_AMOUNT
        sUpdateLimitUsedAmount = titanLimitConnection.prepareStatement("UPDATE `LIMIT` SET USED_AMOUNT = ? WHERE ID = ?");

        statements.add(sSelectBillfoldPayment);
        statements.add(sInsertLimitUsage);
        statements.add(sInsertLimitUsageHistory);
        statements.add(sSelectTitanLimitsForPlayer);
        statements.add(sUpdateLimitUsedAmount);
    }

    public void migrateUsagesForPlayer(final Long playerId) throws SQLException {
        log.debug("Migrating limit usages for: player id {}", playerId);
        // fetch migrated limits from Titan
        Limits limits = new Limits(playerId);
        if (limits.dailyLimitId == null && limits.weeklyLimitId == null && limits.monthlyLimitId == null) {
            return;
        }

        log.debug("  Select billfold." + Main.BILLFOLD_TABLE_PREFIX + "PAYMENT where OK_TIME between {} and {}", limits.minStartTime, limits.maxEndTime);
        // Select deposit payments from billfold.PAYMENT
        sSelectBillfoldPayment.setLong(1, playerId);
        sSelectBillfoldPayment.setTimestamp(4, limits.minStartTime);
        sSelectBillfoldPayment.setTimestamp(5, limits.maxEndTime);
        ResultSet billfoldPaymentRS = sSelectBillfoldPayment.executeQuery();

        while (billfoldPaymentRS.next()) {
            long       paymentId        = billfoldPaymentRS.getLong("PK_PAYMENT_ID");
            int        siteId           = billfoldPaymentRS.getInt("SITE_ID");
            Timestamp  createdTime      = billfoldPaymentRS.getTimestamp("CREATED_TIME");
            Timestamp  okTime           = billfoldPaymentRS.getTimestamp("OK_TIME");
            BigDecimal usageAmount      = billfoldPaymentRS.getBigDecimal("USAGE_AMOUNT");
            String     clientIp         = billfoldPaymentRS.getString("PAYMENT_IP");
            String     clientCountry    = billfoldPaymentRS.getString("PAYMENT_COUNTRY");
            String     clientUserAgent  = billfoldPaymentRS.getString("PAYMENT_USER_AGENT");
            String     clientDeviceType = billfoldPaymentRS.getString("PAYMENT_DEVICE_TYPE");
            long       userId           = billfoldPaymentRS.getLong("FK_USER_ID");

            log.debug("Insert LIMIT_USAGE userId {}, siteId {}, ", userId, siteId);
            sInsertLimitUsage.setTimestamp(1, okTime);
            sInsertLimitUsage.setLong(2, playerId);
            sInsertLimitUsage.setInt(3, siteId);
            sInsertLimitUsage.setLong(4, userId);
            sInsertLimitUsage.executeUpdate();
            Long limitUsageId = getInsertedId(sInsertLimitUsage);

            log.debug("Update PAYMENT for paymentId {} set LIMIT_USAGE_ID = {}", paymentId, limitUsageId);
            sUpdateBillfoldLimitUsageId.setLong(1, limitUsageId);
            sUpdateBillfoldLimitUsageId.setLong(2, paymentId);
            sUpdateBillfoldLimitUsageId.executeUpdate();

            loggerPaymentLimitUsageUpdateSql.debug("UPDATE PAYMENT SET LIMIT_USAGE_ID = {} WHERE PK_PAYMENT_ID = {};", limitUsageId, paymentId);

            log.debug("Insert LIMIT_USAGE_HISTORY for limitUsageId {}, usageAmount {}", limitUsageId, usageAmount);
            sInsertLimitUsageHistory.setTimestamp(1, createdTime);
            sInsertLimitUsageHistory.setLong(2, limitUsageId);
            sInsertLimitUsageHistory.setBigDecimal(5, usageAmount);
            sInsertLimitUsageHistory.setBoolean(8, false);
            sInsertLimitUsageHistory.setString(9, clientIp);
            sInsertLimitUsageHistory.setString(10, clientCountry);
            sInsertLimitUsageHistory.setString(11, clientUserAgent);
            sInsertLimitUsageHistory.setString(12, clientDeviceType);

            // If okTime of deposit is in period of the limit then add USAGE_HISTORY and update usedAmount in LIMIT
            // daily
            if (limits.dailyLimitId != null && limits.dailyStartTime.before(okTime) && limits.dailyEndTime.after(okTime)) {
                if (limits.dailyUsedAmount == null) {
                    limits.dailyUsedAmount = BigDecimal.ZERO;
                }
                BigDecimal newLimitAmount = limits.dailyUsedAmount.add(usageAmount);
                sInsertLimitUsageHistory.setLong(3, limits.dailyLimitId);
                sInsertLimitUsageHistory.setBigDecimal(6, limits.dailyUsedAmount);
                sInsertLimitUsageHistory.setBigDecimal(7, newLimitAmount);
                sInsertLimitUsageHistory.executeUpdate();
                limits.dailyUsedAmount = newLimitAmount;
                limits.dailyLimitUsedAmountChanged = true;
            }
            // weekly
            if (limits.weeklyLimitId != null && limits.weeklyStartTime.before(okTime) && limits.weeklyEndTime.after(okTime)) {
                if (limits.weeklyUsedAmount == null) {
                    limits.weeklyUsedAmount = BigDecimal.ZERO;
                }
                BigDecimal newLimitAmount = limits.weeklyUsedAmount.add(usageAmount);
                sInsertLimitUsageHistory.setLong(3, limits.weeklyLimitId);
                sInsertLimitUsageHistory.setBigDecimal(6, limits.weeklyUsedAmount);
                sInsertLimitUsageHistory.setBigDecimal(7, newLimitAmount);
                sInsertLimitUsageHistory.executeUpdate();
                limits.weeklyUsedAmount = newLimitAmount;
                limits.weeklyLimitUsedAmountChanged = true;
            }
            // monthly
            if (limits.monthlyLimitId != null && limits.monthlyStartTime.before(okTime) && limits.monthlyEndTime.after(okTime)) {
                if (limits.monthlyUsedAmount == null) {
                    limits.monthlyUsedAmount = BigDecimal.ZERO;
                }
                BigDecimal newLimitAmount = limits.monthlyUsedAmount.add(usageAmount);
                sInsertLimitUsageHistory.setLong(3, limits.monthlyLimitId);
                sInsertLimitUsageHistory.setBigDecimal(6, limits.monthlyUsedAmount);
                sInsertLimitUsageHistory.setBigDecimal(7, newLimitAmount);
                sInsertLimitUsageHistory.executeUpdate();
                limits.monthlyUsedAmount = newLimitAmount;
                limits.monthlyLimitUsedAmountChanged = true;
            }
        }

        // Update usedAmount in existed titan_limit.LIMIT
        limits.saveUsedAmounts();
    }

    // Class for working with deposit daily and weekly and monthly limits for one player
    private class Limits {
        private Long dailyLimitId;
        private Long weeklyLimitId;
        private Long monthlyLimitId;

        private BigDecimal dailyUsedAmount;
        private BigDecimal weeklyUsedAmount;
        private BigDecimal monthlyUsedAmount;

        private Timestamp dailyStartTime;
        private Timestamp weeklyStartTime;
        private Timestamp monthlyStartTime;

        private Timestamp dailyEndTime;
        private Timestamp weeklyEndTime;
        private Timestamp monthlyEndTime;

        //minimum of startTimes (daily, weekly and monthly)
        private Timestamp minStartTime;
        //maximum of endTimes (daily, weekly and monthly)
        private Timestamp maxEndTime;

        // Is usedAmount in limit changed
        private Boolean dailyLimitUsedAmountChanged   = false;
        private Boolean weeklyLimitUsedAmountChanged  = false;
        private Boolean monthlyLimitUsedAmountChanged = false;

        // Select existed limits from titan_limit DB
        Limits(final Long playerId) throws SQLException {
            sSelectTitanLimitsForPlayer.setLong(2, playerId);
            ResultSet limitsRS = sSelectTitanLimitsForPlayer.executeQuery();
            int       count    = 0;
            while (limitsRS.next()) {
                count++;
                String periodType = limitsRS.getString("PERIOD_TYPE");
                switch (periodType) {
                    case "daily":
                        dailyLimitId = limitsRS.getLong("ID");
                        dailyUsedAmount = limitsRS.getBigDecimal("USED_AMOUNT");
                        dailyStartTime = limitsRS.getTimestamp("CURRENT_PERIOD_START_TIME");
                        dailyEndTime = limitsRS.getTimestamp("CURRENT_PERIOD_END_TIME");
                        break;
                    case "weekly":
                        weeklyLimitId = limitsRS.getLong("ID");
                        weeklyUsedAmount = limitsRS.getBigDecimal("USED_AMOUNT");
                        weeklyStartTime = limitsRS.getTimestamp("CURRENT_PERIOD_START_TIME");
                        weeklyEndTime = limitsRS.getTimestamp("CURRENT_PERIOD_END_TIME");
                        break;
                    case "monthly":
                        monthlyLimitId = limitsRS.getLong("ID");
                        monthlyUsedAmount = limitsRS.getBigDecimal("USED_AMOUNT");
                        monthlyStartTime = limitsRS.getTimestamp("CURRENT_PERIOD_START_TIME");
                        monthlyEndTime = limitsRS.getTimestamp("CURRENT_PERIOD_END_TIME");
                        break;
                }
                calculateMinAndMaxTime();
            }
            log.debug(" Found {} limits for player id {}", count, playerId);
        }

        // Calculate period which will be use in SELECT FROM billfold.PAYMENTS
        void calculateMinAndMaxTime() {
            if (dailyStartTime != null) {
                minStartTime = dailyStartTime;
            }
            if (weeklyStartTime != null) {
                minStartTime = minStartTime == null ? weeklyStartTime : new Timestamp(Math.min(minStartTime.getTime(), weeklyStartTime.getTime()));
            }
            if (monthlyStartTime != null) {
                minStartTime = minStartTime == null ? monthlyStartTime : new Timestamp(Math.min(minStartTime.getTime(), monthlyStartTime.getTime()));
            }
            if (dailyEndTime != null) {
                maxEndTime = dailyEndTime;
            }
            if (weeklyEndTime != null) {
                maxEndTime = maxEndTime == null ? weeklyEndTime : new Timestamp(Math.max(maxEndTime.getTime(), weeklyEndTime.getTime()));
            }
            if (monthlyEndTime != null) {
                maxEndTime = maxEndTime == null ? monthlyEndTime : new Timestamp(Math.max(maxEndTime.getTime(), monthlyEndTime.getTime()));
            }
        }

        // Save changes of usedAmount
        void saveUsedAmounts() throws SQLException {
            if (dailyLimitId != null && dailyLimitUsedAmountChanged) {
                sUpdateLimitUsedAmount.setBigDecimal(1, dailyUsedAmount);
                sUpdateLimitUsedAmount.setLong(2, dailyLimitId);
                sUpdateLimitUsedAmount.executeUpdate();
                log.debug("Daily limit {} updated usedAmount to {}", dailyLimitId, dailyUsedAmount);
            }
            if (weeklyLimitId != null && weeklyLimitUsedAmountChanged) {
                sUpdateLimitUsedAmount.setBigDecimal(1, weeklyUsedAmount);
                sUpdateLimitUsedAmount.setLong(2, weeklyLimitId);
                sUpdateLimitUsedAmount.executeUpdate();
                log.debug("Weekly limit {} updated usedAmount to {}", weeklyLimitId, weeklyUsedAmount);
            }
            if (monthlyLimitId != null && monthlyLimitUsedAmountChanged) {
                sUpdateLimitUsedAmount.setBigDecimal(1, monthlyUsedAmount);
                sUpdateLimitUsedAmount.setLong(2, monthlyLimitId);
                sUpdateLimitUsedAmount.executeUpdate();
                log.debug("Monthly limit {} updated usedAmount to {}", monthlyLimitId, monthlyUsedAmount);
            }
        }
    }

}
