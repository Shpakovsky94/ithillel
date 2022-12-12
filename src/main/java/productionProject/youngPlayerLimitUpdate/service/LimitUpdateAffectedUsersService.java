package productionProject.youngPlayerLimitUpdate.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import productionProject.migration.AbstractMigration;
import productionProject.youngPlayerLimitUpdate.domain.User;

@Slf4j
public class LimitUpdateAffectedUsersService extends AbstractMigration {
    private final PreparedStatement sInsertIntoLimitUpdateAffectedUsers;


    public LimitUpdateAffectedUsersService(
        final Connection billfoldConnection
    ) throws SQLException {

        sInsertIntoLimitUpdateAffectedUsers = billfoldConnection
            .prepareStatement("INSERT INTO LIMIT_UPDATE_AFFECTED_USERS "
                                  + "(SITE_ID, USER_ID, TITAN_PLAYER_ID, FIRST_NAME, LAST_NAME, EMAIL, STATUS, BIRTH_DATE, COUNTRY,"
                                  + " LIMIT_TYPE, LIMIT_PERIOD, AMOUNT_BEFORE, AMOUNT_AFTER, PHASE)"
                                  + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        statements.add(sInsertIntoLimitUpdateAffectedUsers);
    }


    public void insert(
        final User user,
        final String limitType,
        final String periodType,
        final BigDecimal amountBefore,
        final BigDecimal amountAfter,
        final String phase
    ) throws SQLException {
        sInsertIntoLimitUpdateAffectedUsers.setInt(1, user.getSiteId());
        sInsertIntoLimitUpdateAffectedUsers.setLong(2, user.getUserId());
        sInsertIntoLimitUpdateAffectedUsers.setLong(3, user.getTitanPlayerId());
        sInsertIntoLimitUpdateAffectedUsers.setString(4, user.getFirstName());
        sInsertIntoLimitUpdateAffectedUsers.setString(5, user.getLastName());
        sInsertIntoLimitUpdateAffectedUsers.setString(6, user.getEmail());
        sInsertIntoLimitUpdateAffectedUsers.setString(7, user.getStatus());
        sInsertIntoLimitUpdateAffectedUsers.setString(8, user.getBirthDate());
        sInsertIntoLimitUpdateAffectedUsers.setString(9, user.getCountry());
        sInsertIntoLimitUpdateAffectedUsers.setString(10, limitType);
        sInsertIntoLimitUpdateAffectedUsers.setString(11, periodType);
        sInsertIntoLimitUpdateAffectedUsers.setBigDecimal(12, amountBefore);
        sInsertIntoLimitUpdateAffectedUsers.setBigDecimal(13, amountAfter);
        sInsertIntoLimitUpdateAffectedUsers.setString(14, phase);
        sInsertIntoLimitUpdateAffectedUsers.executeUpdate();
    }


}
