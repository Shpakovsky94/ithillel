package productionProject.migration;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import lombok.extern.slf4j.Slf4j;
import productionProject.Main;

@Slf4j
public class DepositLimitMigration extends AbstractMigration {

    private PreparedStatement sSelectBillfoldLimit;

    public DepositLimitMigration(
        final Connection billfoldConnection,
        final Connection titanLimitConnection
    ) throws SQLException {
        super(billfoldConnection, titanLimitConnection);
        // Prepare SQL statements

        // billfold limit statements
        sSelectBillfoldLimit = billfoldConnection.prepareStatement(
            "SELECT DAILY_DEPOSIT_LIMIT_AMOUNT, DAILY_DEPOSIT_LIMIT_AUTO_RENEW, DAILY_DEPOSIT_LIMIT_START_TIME, NEW_DAILY_DEPOSIT_LIMIT_AMOUNT, NEW_DAILY_DEPOSIT_LIMIT_EFFECTIVE_TIME, "
                + "WEEKLY_DEPOSIT_LIMIT_AMOUNT, WEEKLY_DEPOSIT_LIMIT_AUTO_RENEW, WEEKLY_DEPOSIT_LIMIT_START_TIME, NEW_WEEKLY_DEPOSIT_LIMIT_AMOUNT, NEW_WEEKLY_DEPOSIT_LIMIT_EFFECTIVE_TIME, "
                + "MONTHLY_DEPOSIT_LIMIT_AMOUNT, MONTHLY_DEPOSIT_LIMIT_AUTO_RENEW, MONTHLY_DEPOSIT_LIMIT_START_TIME, NEW_MONTHLY_DEPOSIT_LIMIT_AMOUNT, NEW_MONTHLY_DEPOSIT_LIMIT_EFFECTIVE_TIME "
                + "FROM " + Main.BILLFOLD_TABLE_PREFIX + "GAMING_LIMIT WHERE FK_USER_ID = ?");

        // titan_limit statements
        sInsertTitanLimit.setString(2, Main.DEPOSIT_LIMIT_TYPE);
        sFindExistingLimitInTitan.setString(2, Main.DEPOSIT_LIMIT_TYPE);

        statements.add(sSelectBillfoldLimit);
    }

    public void migrate(
        final Long userId,
        final Long playerId,
        final Integer siteId
    ) throws SQLException {
        log.debug("Migrating limit for: user id: {}, site id: {}, player id: {}", userId, siteId, playerId);

        sSelectBillfoldLimit.setLong(1, userId);
        ResultSet billfoldLimitRS = sSelectBillfoldLimit.executeQuery();

        while (billfoldLimitRS.next()) {
            BigDecimal dailyLimitAmount           = billfoldLimitRS.getBigDecimal("DAILY_DEPOSIT_LIMIT_AMOUNT");
            boolean    dailyLimitRenew            = billfoldLimitRS.getBoolean("DAILY_DEPOSIT_LIMIT_AUTO_RENEW");
            Timestamp  dailyDepositStartTime      = billfoldLimitRS.getTimestamp("DAILY_DEPOSIT_LIMIT_START_TIME");
            BigDecimal newDailyLimitAmount        = billfoldLimitRS.getBigDecimal("NEW_DAILY_DEPOSIT_LIMIT_AMOUNT");
            Timestamp  newDailyLimitEffectiveTime = billfoldLimitRS.getTimestamp("NEW_DAILY_DEPOSIT_LIMIT_EFFECTIVE_TIME");

            BigDecimal weeklyLimitAmount           = billfoldLimitRS.getBigDecimal("WEEKLY_DEPOSIT_LIMIT_AMOUNT");
            boolean    weeklyLimitRenew            = billfoldLimitRS.getBoolean("WEEKLY_DEPOSIT_LIMIT_AUTO_RENEW");
            Timestamp  weeklyDepositStartTime      = billfoldLimitRS.getTimestamp("WEEKLY_DEPOSIT_LIMIT_START_TIME");
            BigDecimal newWeeklyLimitAmount        = billfoldLimitRS.getBigDecimal("NEW_WEEKLY_DEPOSIT_LIMIT_AMOUNT");
            Timestamp  newWeeklyLimitEffectiveTime = billfoldLimitRS.getTimestamp("NEW_WEEKLY_DEPOSIT_LIMIT_EFFECTIVE_TIME");

            BigDecimal monthlyLimitAmount           = billfoldLimitRS.getBigDecimal("MONTHLY_DEPOSIT_LIMIT_AMOUNT");
            boolean    monthlyLimitRenew            = billfoldLimitRS.getBoolean("MONTHLY_DEPOSIT_LIMIT_AUTO_RENEW");
            Timestamp  monthlyDepositStartTime      = billfoldLimitRS.getTimestamp("MONTHLY_DEPOSIT_LIMIT_START_TIME");
            BigDecimal newMonthlyLimitAmount        = billfoldLimitRS.getBigDecimal("NEW_MONTHLY_DEPOSIT_LIMIT_AMOUNT");
            Timestamp  newMonthlyLimitEffectiveTime = billfoldLimitRS.getTimestamp("NEW_MONTHLY_DEPOSIT_LIMIT_EFFECTIVE_TIME");

            if ((dailyLimitAmount != null || newDailyLimitAmount != null) && dailyDepositStartTime != null) {
                saveLimitAndLimitChangeHistory(userId, playerId, "daily", siteId, dailyLimitAmount, dailyLimitRenew, dailyDepositStartTime, newDailyLimitAmount, newDailyLimitEffectiveTime);
            }
            if ((weeklyLimitAmount != null || newWeeklyLimitAmount != null) && weeklyDepositStartTime != null) {
                saveLimitAndLimitChangeHistory(userId, playerId, "weekly", siteId, weeklyLimitAmount, weeklyLimitRenew, weeklyDepositStartTime, newWeeklyLimitAmount, newWeeklyLimitEffectiveTime);
            }
            if ((monthlyLimitAmount != null || newMonthlyLimitAmount != null) && monthlyDepositStartTime != null) {
                saveLimitAndLimitChangeHistory(userId, playerId, "monthly", siteId, monthlyLimitAmount, monthlyLimitRenew, monthlyDepositStartTime, newMonthlyLimitAmount,
                                               newMonthlyLimitEffectiveTime);
            }
        }

    }

}
