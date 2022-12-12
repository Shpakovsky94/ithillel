package productionProject.task;

import java.math.BigDecimal;
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
import productionProject.domain.User;
import productionProject.service.LimitUpdateFailedUsersService;
import productionProject.service.UpdateLimitService;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class UpdateLimitTask extends AbstractMigratingTask {

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

            UpdateLimitService            updateLimitService            = new UpdateLimitService(billfoldConnection, titanLimitConnection);
            LimitUpdateFailedUsersService limitUpdateFailedUsersService = new LimitUpdateFailedUsersService(billfoldConnection);

            String     limitType  = Main.UPDATE_LIMIT_LIMIT_TYPE;
            String     periodType = Main.UPDATE_LIMIT_PERIOD_TYPE;
            BigDecimal newAmount  = Main.UPDATE_LIMIT_NEW_AMOUNT;
            Long       boUserId   = Main.UPDATE_LIMIT_BO_USER;

            while (usersYounger24Rs.next()) {
                setAutoCommitFalse(billfoldConnection, titanLimitConnection);
                User userFromRs = User.buildUserFromRs(usersYounger24Rs);

                try {
                    updateLimitService.updateLimit(userFromRs, limitType, periodType, newAmount, boUserId);

                    commit(billfoldConnection, titanLimitConnection);
                } catch (Exception e) {
                    log.error("Updating {} limits failed for playerId: {}", limitType, userFromRs.getTitanPlayerId(), e);
                    rollback(billfoldConnection, titanLimitConnection);

                    limitUpdateFailedUsersService.insert(userFromRs, Main.PHASE, e.getMessage());
                    commit(billfoldConnection);
                }
            }
            log.debug("Finished updating {} limits in ms: {}", limitType, System.currentTimeMillis() - startTs);
            updateLimitService.closeStatements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
