package productionProject.youngPlayerLimitUpdate.updateLossLimit;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import productionProject.migration.AbstractMigration;
import productionProject.youngPlayerLimitUpdate.domain.Limit;
import productionProject.youngPlayerLimitUpdate.domain.User;
import productionProject.youngPlayerLimitUpdate.enums.PeriodType;
import productionProject.youngPlayerLimitUpdate.service.LimitService;

@Slf4j
public class UpdateLimitService extends AbstractMigration {

    public LimitService limitService;

    public UpdateLimitService(
        final Connection billfoldConnection,
        final Connection titanLimitConnection
    ) throws SQLException {
        limitService = new LimitService(billfoldConnection, titanLimitConnection);
    }

    /**
     * search and update limit for certain player
     *
     * @param user User entity
     */
    public void updateLimit(
        final User user,
        final String limitType,
        final String periodType,
        final BigDecimal newAmount,
        final Long boUserId
    ) throws SQLException, ParseException {
        Map<String, Limit> playerLimits = limitService.retrieveLimitsFromDbByLimitTypeAndPlayerId(limitType, user.getTitanPlayerId());

        boolean hasDailyLimit   = false;
        boolean hasWeeklyLimit  = false;
        boolean hasMonthlyLimit = false;

        Limit dailyLimit   = null;
        Limit weeklyLimit  = null;
        Limit monthlyLimit = null;

        BigDecimal dailyAmount   = null;
        BigDecimal weeklyAmount  = null;
        BigDecimal monthlyAmount = null;

        if (playerLimits.containsKey(PeriodType.daily.toString())) {
            hasDailyLimit = true;
            dailyLimit = playerLimits.get(PeriodType.daily.toString());
            if (dailyLimit.getAmount() != null) {
                dailyAmount = dailyLimit.getAmount();
            }
        }
        if (playerLimits.containsKey(PeriodType.weekly.toString())) {
            hasWeeklyLimit = true;
            weeklyLimit = playerLimits.get(PeriodType.weekly.toString());
            if (weeklyLimit.getAmount() != null) {
                weeklyAmount = weeklyLimit.getAmount();
            }
        }
        if (playerLimits.containsKey(PeriodType.monthly.toString())) {
            hasMonthlyLimit = true;
            monthlyLimit = playerLimits.get(PeriodType.monthly.toString());
            if (monthlyLimit.getAmount() != null) {
                monthlyAmount = monthlyLimit.getAmount();
            }
        }

        BigDecimal amountToSet   = newAmount;
        Limit      limitToInsert = limitService.buildLimitToInsert(user, limitType, periodType);

        if (periodType.equals(PeriodType.monthly.toString())) {

            if (!hasMonthlyLimit) {
                // create new monthly limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                limitService.createLimit(user, limitToInsert, amountToSet, boUserId);
            } else if (monthlyAmount == null || monthlyAmount.compareTo(amountToSet) > 0) {
                // update monthly limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                limitService.updateLimitWithNewAmount(user, monthlyLimit, amountToSet, boUserId);
            }

            if (hasWeeklyLimit) {
                if (weeklyAmount != null && amountToSet.compareTo(weeklyAmount) < 0) {
                    // update weekly limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                    limitService.updateLimitWithNewAmount(user, weeklyLimit, amountToSet, boUserId);
                }
            }

            if (hasDailyLimit) {
                if (dailyAmount != null && amountToSet.compareTo(dailyAmount) < 0) {
                    // update daily limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                    limitService.updateLimitWithNewAmount(user, dailyLimit, amountToSet, boUserId);
                }
            }

        } else if (periodType.equals(PeriodType.weekly.toString())) {

            // check is new weekly limit amount < then existed monthly limit amount
            if (hasMonthlyLimit) {
                if (monthlyAmount != null && amountToSet.compareTo(monthlyAmount) > 0) {
                    amountToSet = monthlyAmount;
                }
            }
            if (!hasWeeklyLimit) {
                // create new weekly limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                limitService.createLimit(user, limitToInsert, amountToSet, boUserId);
            } else if (weeklyAmount == null || weeklyAmount.compareTo(amountToSet) > 0) {
                // update weekly limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                limitService.updateLimitWithNewAmount(user, weeklyLimit, amountToSet, boUserId);
            }

            if (hasDailyLimit) {
                if (dailyAmount != null && amountToSet.compareTo(dailyAmount) < 0) {
                    // update daily limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                    limitService.updateLimitWithNewAmount(user, dailyLimit, amountToSet, boUserId);
                }
            }

        } else if (periodType.equals(PeriodType.daily.toString())) {

            // check is new daily limit amount < then existed weekly limit amount
            if (hasWeeklyLimit) {
                if (weeklyAmount != null && amountToSet.compareTo(weeklyAmount) > 0) {
                    amountToSet = weeklyAmount;
                }
                // check is new daily limit amount < then existed monthly limit amount
            } else if (hasMonthlyLimit) {
                if (monthlyAmount != null && amountToSet.compareTo(monthlyAmount) > 0) {
                    amountToSet = monthlyAmount;
                }
            }

            if (!hasDailyLimit) {
                // create new daily limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                limitService.createLimit(user, limitToInsert, amountToSet, boUserId);
            } else if (dailyAmount == null || dailyAmount.compareTo(amountToSet) > 0) {
                // update daily limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                limitService.updateLimitWithNewAmount(user, dailyLimit, amountToSet, boUserId);
            }
        }
    }
}
