package productionProject.youngPlayerLimitUpdate.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import productionProject.migration.AbstractMigration;
import productionProject.youngPlayerLimitUpdate.domain.User;

@Slf4j
public class LimitUpdateFailedUsersService extends AbstractMigration {

    private final PreparedStatement sInsertLimitUpdateFailedUsersBillfold;


    public LimitUpdateFailedUsersService(
        final Connection billfoldConnection
    ) throws SQLException {
        sInsertLimitUpdateFailedUsersBillfold = billfoldConnection
            .prepareStatement("INSERT INTO LIMIT_UPDATE_FAILED_USERS "
                                  + "(SITE_ID, USER_ID, TITAN_PLAYER_ID, PHASE, FAIL_REASON)"
                                  + " VALUES (?,?,?,?,?)");

        statements.add(sInsertLimitUpdateFailedUsersBillfold);
    }


    public void insert(
        final User user,
        final String phase,
        final String reason
    ) throws SQLException {

        sInsertLimitUpdateFailedUsersBillfold.setInt(1, user.getSiteId());
        sInsertLimitUpdateFailedUsersBillfold.setLong(2, user.getUserId());
        sInsertLimitUpdateFailedUsersBillfold.setLong(3, user.getTitanPlayerId());
        sInsertLimitUpdateFailedUsersBillfold.setString(4, phase);
        sInsertLimitUpdateFailedUsersBillfold.setString(5, reason);
        sInsertLimitUpdateFailedUsersBillfold.executeUpdate();
    }
}
