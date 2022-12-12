package productionProject.task;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import productionProject.Main;
import productionProject.datasource.Datasource;
import productionProject.migration.LossLimitMigration;

@Slf4j
public class LossLimitMigratingTask extends AbstractMigratingTask {

    public LossLimitMigratingTask(
        long taskNumber,
        long fetchSize,
        Datasource billfoldDatasource,
        Datasource titanLimitDatasource
    ) {
        super(taskNumber, fetchSize, billfoldDatasource, null, titanLimitDatasource);
    }

    @Override
    public void run() {
        long offset = taskNumber * fetchSize;
        try (
            Connection billfoldConnection = billfoldDatasource.getConnection();
            Connection titanLimitConnection = titanLimitDatasource.getConnection();
        ) {
            String query = "SELECT U.PK_USER_ID, U.SITE_ID, U.TITAN_PLAYER_ID FROM " + Main.BILLFOLD_TABLE_PREFIX + "GAMING_LIMIT GL JOIN " + Main.BILLFOLD_TABLE_PREFIX
                + "USER U on GL.FK_USER_ID = U.PK_USER_ID WHERE U.TITAN_PLAYER_ID IS NOT NULL AND LOSS_LIMIT_AMOUNT IS NOT NULL AND LOSS_LIMIT_PERIOD IS NOT NULL AND LOSS_LIMIT_START_TIME IS NOT NULL AND (LOSS_LIMIT_AUTO_RENEW = true OR ( (LOSS_LIMIT_AUTO_RENEW IS NULL OR LOSS_LIMIT_AUTO_RENEW = false) AND LOSS_LIMIT_END_TIME > NOW())) ORDER BY U.PK_USER_ID LIMIT ?, ?";
            PreparedStatement billfoldStatement = billfoldConnection.prepareStatement(query);
            billfoldStatement.setLong(1, offset);
            billfoldStatement.setLong(2, fetchSize);

            ResultSet resultSet = billfoldStatement.executeQuery();

            LossLimitMigration lossLimitMigration = new LossLimitMigration(billfoldConnection, titanLimitConnection);
            while (resultSet.next()) {
                setAutoCommitFalse(billfoldConnection, titanLimitConnection);

                Long    userId   = resultSet.getLong("PK_USER_ID");
                Long    playerId = resultSet.getLong("TITAN_PLAYER_ID");
                Integer siteId   = resultSet.getInt("SITE_ID");

                log.debug("Migrating: userId: {}, siteId: {}, titanPlayerId: {}", userId, siteId, playerId);
                try {
                    lossLimitMigration.migrate(userId, playerId, siteId);
                    commit(billfoldConnection, titanLimitConnection);
                } catch (Exception e) {
                    log.error("Migration failed: user id: {}, site id: {}", userId, siteId, e);
                    rollback(billfoldConnection, titanLimitConnection);
                }
            }
            lossLimitMigration.closeStatements();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
