package productionProject.youngPlayerLimitUpdate.setPendingLimits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import productionProject.Main;
import productionProject.datasource.Datasource;
import productionProject.task.AbstractMigratingTask;
import productionProject.youngPlayerLimitUpdate.domain.User;
import productionProject.youngPlayerLimitUpdate.service.LimitUpdateFailedUsersService;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class SetPendingLimitTask extends AbstractMigratingTask {

    private Datasource billfoldDatasource;
    private Datasource titanLimitDatasource;

    @Override
    public void run() {

        try (
            Connection billfoldConnection = billfoldDatasource.getConnection();
            Connection titanLimitConnection = titanLimitDatasource.getConnection();
        ) {
            long startTs = System.currentTimeMillis();
            setAutoCommitFalse(billfoldConnection, titanLimitConnection);

            String qSelectUsersYounger24 =
                "SELECT u.PK_USER_ID, u.SITE_ID, u.TITAN_PLAYER_ID, u.FIRST_NAME, u.LAST_NAME, u.EMAIL, (CASE "
                    + "WHEN u.STATUS = 0 THEN 'inactive' "
                    + "WHEN u.STATUS = 1 THEN 'active' "
                    + "WHEN u.STATUS = 2 THEN 'frozen' "
                    + "WHEN u.STATUS = 3 THEN 'closed' "
                    + "WHEN u.STATUS = 4 THEN 'suspended' "
                    + "WHEN u.STATUS = 5 THEN 'suspended_death' "
                    + "END) AS STATUS,"
                    + " u.BIRTH_DATE, u.COUNTRY "
                    + "FROM USER u "
                    + "WHERE TIMESTAMPDIFF(year, u.BIRTH_DATE, NOW()) < 24;";

            PreparedStatement sSelectUsersYounger24 = billfoldConnection.prepareStatement(qSelectUsersYounger24);
            ResultSet         usersYounger24Rs      = sSelectUsersYounger24.executeQuery();

            SetPendingLimitService        setPendingLimitService        = new SetPendingLimitService(billfoldConnection, titanLimitConnection);
            LimitUpdateFailedUsersService limitUpdateFailedUsersService = new LimitUpdateFailedUsersService(billfoldConnection);

            String limitType = Main.UPDATE_LIMIT_LIMIT_TYPE;
            Long   boUserId  = Main.UPDATE_LIMIT_BO_USER;

            while (usersYounger24Rs.next()) {
                setAutoCommitFalse(billfoldConnection, titanLimitConnection);
                User userFromRs = User.buildUserFromRs(usersYounger24Rs);

                try {
                    setPendingLimitService.setPendingLimit(userFromRs, limitType, boUserId);

                    commit(billfoldConnection, titanLimitConnection);
                } catch (Exception e) {
                    log.error("Setting pending {} limits failed for playerId: {}", limitType, userFromRs.getTitanPlayerId(), e);
                    rollback(billfoldConnection, titanLimitConnection);

                    limitUpdateFailedUsersService.insert(userFromRs, Main.PHASE, e.getMessage());
                    commit(billfoldConnection);
                }
            }
            log.debug("Finished setting pending {} limits in ms: {}", limitType, System.currentTimeMillis() - startTs);
            setPendingLimitService.closeStatements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
