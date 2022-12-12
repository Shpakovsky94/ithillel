package productionProject.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import productionProject.Main;
import productionProject.datasource.Datasource;
import productionProject.migration.LossLimitUsageMigration;

@Slf4j
public class LossLimitUsageMigratingTask extends AbstractMigratingTask {

    private PreparedStatement sSelectBillfoldSiteIds;

    public LossLimitUsageMigratingTask(
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
        log.debug("{} - Task #{}: Select playerIds from billfold offset: {}, fetchSize: {}", Thread.currentThread().getName(), taskNumber, offset, fetchSize);

        try (
            Connection billfoldConnection = billfoldDatasource.getConnection();
            Connection titanLimitConnection = titanLimitDatasource.getConnection();
            Connection titanPlayerConnection = titanPlayerDatasource.getConnection()
        ) {
            sSelectBillfoldSiteIds = billfoldConnection.prepareStatement("SELECT PK_APPLICATION_NAME_ID FROM " + Main.BILLFOLD_TABLE_PREFIX + "BILLFOLD_APPLICATION WHERE APPLICATION_TYPE = ?");
            sSelectBillfoldSiteIds.setInt(1, 0);
            ResultSet     siteIdsRS = sSelectBillfoldSiteIds.executeQuery();
            List<Integer> siteIds   = new ArrayList<>();
            while (siteIdsRS.next()) {
                siteIds.add(siteIdsRS.getInt(1));
            }
            log.debug("Selected siteIds from billfold: {}", siteIds);
            LossLimitUsageMigration limitUsageMigration = new LossLimitUsageMigration(billfoldConnection, titanPlayerConnection, titanLimitConnection, siteIds);

            PreparedStatement sSelectPlayersStatements = titanLimitConnection.prepareStatement("SELECT DISTINCT PLAYER_ID FROM `LIMIT` WHERE LIMIT_TYPE = 'loss' ORDER BY PLAYER_ID LIMIT ?, ?");
            sSelectPlayersStatements.setLong(1, offset);
            sSelectPlayersStatements.setLong(2, fetchSize);
            ResultSet resultSet = sSelectPlayersStatements.executeQuery();

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
