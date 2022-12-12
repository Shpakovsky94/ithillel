package productionProject.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import productionProject.datasource.Datasource;
import productionProject.migration.DepositLimitUsageMigration;

@Slf4j
public class DepositLimitUsageMigratingTask extends AbstractMigratingTask {

    public DepositLimitUsageMigratingTask(
        long taskNumber,
        long fetchSize,
        Datasource billfoldDatasource,
        Datasource titanPlayerDatasource,
        Datasource titanLimitDatasource
    ) {
        super(taskNumber, fetchSize, billfoldDatasource, titanPlayerDatasource, titanLimitDatasource);
    }

    @Override
    public void run() {
        long offset = taskNumber * fetchSize;
        log.debug("{} - Task #{}: SELECT DISTINCT PLAYER_ID FROM `LIMIT` ORDER BY PLAYER_ID LIMIT {}, {}", Thread.currentThread().getName(), taskNumber, offset, fetchSize);

        try (
            Connection billfoldConnection = billfoldDatasource.getConnection();
            Connection titanLimitConnection = titanLimitDatasource.getConnection();
            Connection titanPlayerConnection = titanPlayerDatasource.getConnection()
        ) {
            DepositLimitUsageMigration limitUsageMigration = new DepositLimitUsageMigration(billfoldConnection, titanPlayerConnection, titanLimitConnection);

            PreparedStatement selectPlayersStatements = titanLimitConnection.prepareStatement("SELECT DISTINCT PLAYER_ID FROM `LIMIT` ORDER BY PLAYER_ID LIMIT ?, ?");
            selectPlayersStatements.setLong(1, offset);
            selectPlayersStatements.setLong(2, fetchSize);
            ResultSet resultSet = selectPlayersStatements.executeQuery();

            while (resultSet.next()) {
                setAutoCommitFalse(billfoldConnection, titanLimitConnection, titanPlayerConnection);

                Long playerId = resultSet.getLong("PLAYER_ID");
                log.debug("Migrating: player id: {}", playerId);

                try {
                    limitUsageMigration.migrateUsagesForPlayer(playerId);
                    commit(billfoldConnection, titanLimitConnection, titanPlayerConnection);
                } catch (Exception e) {
                    log.error("Migration failed: player id: {}", playerId, e);
                    rollback(billfoldConnection, titanLimitConnection, titanPlayerConnection);
                }
            }
            limitUsageMigration.closeStatements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
