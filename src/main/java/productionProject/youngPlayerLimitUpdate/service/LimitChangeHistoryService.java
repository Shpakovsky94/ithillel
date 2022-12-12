package productionProject.youngPlayerLimitUpdate.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import lombok.extern.slf4j.Slf4j;
import productionProject.migration.AbstractMigration;
import productionProject.youngPlayerLimitUpdate.domain.Limit;
import productionProject.youngPlayerLimitUpdate.enums.LimitChangeAction;

@Slf4j
public class LimitChangeHistoryService extends AbstractMigration {

    private final PreparedStatement sInsertTitanLimitChangeHistory;

    public LimitChangeHistoryService(
        final Connection titanLimitConnection
    ) throws SQLException {

        sInsertTitanLimitChangeHistory = titanLimitConnection
            .prepareStatement("INSERT INTO LIMIT_CHANGE_HISTORY "
                                  + "(CREATED_TIME, LIMIT_ID, PLAYER_ID, SITE_ID, USER_ID, ACTION, AMOUNT_BEFORE, AMOUNT_AFTER, BO_USER_ID, VERSION)"
                                  + " VALUES (?,?,?,?,?,?,?,?,?,?)");

        statements.add(sInsertTitanLimitChangeHistory);
    }


    public void insert(
        final Limit playerLimit,
        final LimitChangeAction action,
        final BigDecimal newAmount,
        final Long boUserId
    ) throws SQLException {

        Timestamp now = new Timestamp(System.currentTimeMillis());
        sInsertTitanLimitChangeHistory.setTimestamp(1, now);
        sInsertTitanLimitChangeHistory.setLong(2, playerLimit.getId());
        sInsertTitanLimitChangeHistory.setLong(3, playerLimit.getPlayerId());
        sInsertTitanLimitChangeHistory.setInt(4, playerLimit.getSiteId());
        sInsertTitanLimitChangeHistory.setLong(5, playerLimit.getUserId());
        sInsertTitanLimitChangeHistory.setString(6, action.toString());
        sInsertTitanLimitChangeHistory.setBigDecimal(7, playerLimit.getAmount());
        sInsertTitanLimitChangeHistory.setBigDecimal(8, newAmount);
        sInsertTitanLimitChangeHistory.setLong(9, boUserId);
        sInsertTitanLimitChangeHistory.setInt(10, 0);
        sInsertTitanLimitChangeHistory.executeUpdate();
    }

}
