package productionProject.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import productionProject.domain.Limit;
import productionProject.domain.User;
import productionProject.migration.AbstractMigration;

public class SetPendingLimitService extends AbstractMigration {

    public LimitService limitService;

    public SetPendingLimitService(
        final Connection billfoldConnection,
        final Connection titanLimitConnection
    ) throws SQLException {
        limitService = new LimitService(billfoldConnection, titanLimitConnection);
    }

    /**
     * search and update limit with pending new amount for certain player
     *
     * @param user User entity
     * @param limitType String limitType
     * @param boUserId Long boUserId
     */
    public void setPendingLimit(
        final User user,
        final String limitType,
        final Long boUserId
    ) throws SQLException, ParseException {
        List<Limit> playerLimits = limitService.retrievePendingLimitsFromDb(limitType, user.getTitanPlayerId());

        if (playerLimits.isEmpty()) {
            return;
        }

        for (Limit limitToUpdate : playerLimits) {
            if (limitToUpdate.getNewAmount() != null) {
                // set pending limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                limitService.updateLimitWithNewAmount(user, limitToUpdate, limitToUpdate.getNewAmount(), boUserId);
            } else {
                // clear limit with records in LIMIT/LIMIT_CHANGE_HISTORY/LIMIT_HISTORY/LIMIT_UPDATE_AFFECTED_USERS
                limitService.removeLimitData(user, limitToUpdate, boUserId);
            }
        }
    }

}
