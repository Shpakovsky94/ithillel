package productionProject.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import lombok.extern.slf4j.Slf4j;
import productionProject.domain.Limit;
import productionProject.enums.Activity;
import productionProject.enums.LimitChangeAction;
import productionProject.enums.LimitType;
import productionProject.enums.PeriodType;
import productionProject.migration.AbstractMigration;

@Slf4j
public class LimitHistoryService extends AbstractMigration {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final PreparedStatement sInsertLimitHistory;

    public LimitHistoryService(
        final Connection billfoldConnection
    ) throws SQLException {

        sInsertLimitHistory = billfoldConnection
            .prepareStatement("INSERT INTO LIMIT_HISTORY "
                                  + "(FK_USER_ID, SITE_ID, LIMIT_TYPE, LIMIT_PERIOD, VALUE_BEFORE, VALUE_AFTER, "
                                  + "AUTO_RENEW, EXPIRY_TIME, FK_BO_USER_ID, ACTIVITY_CODE)"
                                  + " VALUES (?,?,?,?,?,?,?,?,?,?)");
        statements.add(sInsertLimitHistory);
    }

    public void insert(
        final Limit playerLimit,
        final LimitChangeAction action,
        final BigDecimal newAmount,
        final Long boUserId
    ) throws SQLException {
        sInsertLimitHistory.setLong(1, playerLimit.getUserId());
        sInsertLimitHistory.setInt(2, playerLimit.getSiteId());
        sInsertLimitHistory.setInt(3, LimitType.valueOf(playerLimit.getLimitType()).getValue());
        sInsertLimitHistory.setInt(4, PeriodType.valueOf(playerLimit.getPeriodType()).getValue());
        sInsertLimitHistory.setBigDecimal(5, playerLimit.getAmount());
        sInsertLimitHistory.setBigDecimal(6, newAmount);

        if (newAmount != null) {
            sInsertLimitHistory.setBoolean(7, true);
        } else {
            sInsertLimitHistory.setNull(7, Types.BOOLEAN);
        }
        Timestamp expireTime = null;
        try {
            expireTime = new Timestamp(formatter.parse(playerLimit.getCurrentPeriodEndTime()).getTime());
        } catch (Exception ignore) {
        }

        if (action == LimitChangeAction.clearLimit || expireTime == null) {
            sInsertLimitHistory.setNull(8, Types.TIMESTAMP);
        } else {
            sInsertLimitHistory.setTimestamp(8, expireTime);
        }
        sInsertLimitHistory.setLong(9, boUserId);

        Integer activityCode = null;
        switch (playerLimit.getLimitType()) {
            case "loss":
                if (newAmount != null) {
                    activityCode = Activity.ModifyLossLimit.getValue();
                } else {
                    activityCode = Activity.RemoveGamingLimit.getValue();
                }
                break;
            case "session":
                if (newAmount != null) {
                    activityCode = action == LimitChangeAction.createLimit ? Activity.CreateSessionTimeLimit.getValue() : Activity.ModifySessionTimeLimit.getValue();
                } else {
                    activityCode = Activity.RemoveSessionTimeLimit.getValue();
                }
                break;
            default:
                log.debug("Unexpected value: " + playerLimit.getLimitType());
        }
        if (activityCode == null) {
            sInsertLimitHistory.setNull(10, Types.INTEGER);
        } else {
            sInsertLimitHistory.setInt(10, activityCode);
        }
        sInsertLimitHistory.executeUpdate();
    }


}
