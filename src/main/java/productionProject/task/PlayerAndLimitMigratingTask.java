package productionProject.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import productionProject.Main;
import productionProject.Util;
import productionProject.datasource.Datasource;
import productionProject.migration.DepositLimitMigration;
import productionProject.migration.PlayerMigration;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class PlayerAndLimitMigratingTask implements Runnable {
    private long taskNumber;
    private long fetchSize;

    private Datasource billfoldDatasource;
    private Datasource titanPlayerDatasource;
    private Datasource titanLimitDatasource;

    @Override
    public void run() {
        long offset = taskNumber * fetchSize;

        log.debug(
            String.format("%s - Task #%d: SELECT PK_USER_ID from " + Main.BILLFOLD_TABLE_PREFIX + "USER ORDER BY NATIONAL_ID_NUMBER LIMIT %d, %d", Thread.currentThread().getName(), taskNumber, offset,
                          fetchSize));

        try (
            Connection billfoldConnection = billfoldDatasource.getConnection();
            Connection titanLimitConnection = titanLimitDatasource.getConnection();
            Connection titanPlayerConnection = titanPlayerDatasource.getConnection()
        ) {
            setAutoCommitFalse(billfoldConnection, titanLimitConnection, titanPlayerConnection);

            String query = "SELECT PK_USER_ID, SITE_ID from " + Main.BILLFOLD_TABLE_PREFIX + "USER WHERE TRUE ";

            if (Main.USER_STATUSES.size() > 0) {
                query += " AND STATUS IN " + Util.createInClauseForStatement(Main.USER_STATUSES.size());
            }
            if (Main.INCLUDE_SITE_IDS.size() > 0) {
                query += " AND SITE_ID IN " + Util.createInClauseForStatement(Main.INCLUDE_SITE_IDS.size());
            }
            if (Main.EXCLUDE_SITE_IDS.size() > 0) {
                query += " AND SITE_ID NOT IN " + Util.createInClauseForStatement(Main.EXCLUDE_SITE_IDS.size());
            }
            if (Main.ONLY_MIGRATE_SSN.size() > 0) {
                query += " AND NATIONAL_ID_NUMBER IN " + Util.createInClauseForStatement(Main.ONLY_MIGRATE_SSN.size());
            }
            query += " ORDER BY NATIONAL_ID_NUMBER LIMIT ?, ?";

            PreparedStatement billfoldStatement = billfoldConnection.prepareStatement(query);
            int               lastIndex         = 0;
            int               inOffset          = 0;
            for (int i = 0; i < Main.USER_STATUSES.size(); i++) {
                lastIndex = i + inOffset + 1;
                billfoldStatement.setLong(lastIndex, Main.USER_STATUSES.get(i));
            }
            inOffset = lastIndex;
            for (int i = 0; i < Main.INCLUDE_SITE_IDS.size(); i++) {
                lastIndex = i + inOffset + 1;
                billfoldStatement.setLong(lastIndex, Main.INCLUDE_SITE_IDS.get(i));
            }
            inOffset = lastIndex;
            for (int i = 0; i < Main.EXCLUDE_SITE_IDS.size(); i++) {
                lastIndex = i + inOffset + 1;
                billfoldStatement.setLong(lastIndex, Main.EXCLUDE_SITE_IDS.get(i));
            }
            inOffset = lastIndex;
            for (int i = 0; i < Main.ONLY_MIGRATE_SSN.size(); i++) {
                lastIndex = i + inOffset + 1;
                billfoldStatement.setString(lastIndex, Main.ONLY_MIGRATE_SSN.get(i));
            }
            inOffset = lastIndex;

            billfoldStatement.setLong(inOffset + 1, offset);
            billfoldStatement.setLong(inOffset + 2, fetchSize);

            ResultSet resultSet = billfoldStatement.executeQuery();

            PlayerMigration       playerMigration       = new PlayerMigration(billfoldConnection, titanPlayerConnection);
            DepositLimitMigration depositLimitMigration = new DepositLimitMigration(billfoldConnection, titanLimitConnection);

            while (resultSet.next()) {
                setAutoCommitFalse(billfoldConnection, titanLimitConnection, titanPlayerConnection);

                Long    userId = resultSet.getLong("PK_USER_ID");
                Integer siteId = resultSet.getInt("SITE_ID");

                log.debug("Migrating: user id: {}, site id: {}", userId, siteId);
                try {
                    Long playerId = playerMigration.migrate(userId);
                    if (playerId != null) {
                        depositLimitMigration.migrate(userId, playerId, siteId);
                        commit(billfoldConnection, titanLimitConnection, titanPlayerConnection);
                    }
                } catch (Exception e) {
                    log.error("Migration failed: user id: {}, site id: {}", userId, siteId, e);
                    rollback(billfoldConnection, titanLimitConnection, titanPlayerConnection);
                }
            }
            playerMigration.closeStatements();
            depositLimitMigration.closeStatements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setAutoCommitFalse(
        final Connection billfoldConnection,
        final Connection titanLimitConnection,
        final Connection titanPlayerConnection
    ) throws SQLException {
        billfoldConnection.setAutoCommit(false);
        titanLimitConnection.setAutoCommit(false);
        titanPlayerConnection.setAutoCommit(false);
    }

    private void commit(
        final Connection billfoldConnection,
        final Connection titanLimitConnection,
        final Connection titanPlayerConnection
    ) throws SQLException {
        billfoldConnection.commit();
        titanLimitConnection.commit();
        titanPlayerConnection.commit();
    }

    private void rollback(
        final Connection billfoldConnection,
        final Connection titanLimitConnection,
        final Connection titanPlayerConnection
    ) throws SQLException {
        billfoldConnection.rollback();
        titanLimitConnection.rollback();
        titanPlayerConnection.rollback();
    }
}
