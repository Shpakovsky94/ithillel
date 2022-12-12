package productionProject.youngPlayerLimitUpdate.prepareTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import productionProject.datasource.Datasource;
import productionProject.task.AbstractMigratingTask;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class PrepareTablesTask extends AbstractMigratingTask {

    private Datasource billfoldDatasource;
    private Datasource titanLimitDatasource;

    @Override
    public void run() {
        try (
            Connection billfoldConnection = billfoldDatasource.getConnection();
            Connection titanLimitConnection = titanLimitDatasource.getConnection()
        ) {
            setAutoCommitFalse(billfoldConnection, titanLimitConnection);

            try {

                // backup titan_limit tables
                String            qDropTableBkpLimit = "DROP TABLE IF EXISTS BKP_LIMIT";
                PreparedStatement sDropTableBkpLimit = titanLimitConnection.prepareStatement(qDropTableBkpLimit);
                sDropTableBkpLimit.execute();

                String            qCreateTableBkpLimit = "CREATE TABLE BKP_LIMIT LIKE `LIMIT`";
                PreparedStatement sCreateTableBkpLimit = titanLimitConnection.prepareStatement(qCreateTableBkpLimit);
                sCreateTableBkpLimit.execute();

                String            qInsertBkpLimit = "INSERT INTO BKP_LIMIT SELECT * FROM `LIMIT`";
                PreparedStatement sInsertBkpLimit = titanLimitConnection.prepareStatement(qInsertBkpLimit);
                sInsertBkpLimit.execute();
                log.debug("Created backup for `LIMIT` table");

//                String            qDropTableBkpLimitChangeHistory = "DROP TABLE IF EXISTS BKP_LIMIT_CHANGE_HISTORY";
//                PreparedStatement sDropTableBkpLimitChangeHistory = titanLimitConnection.prepareStatement(qDropTableBkpLimitChangeHistory);
//                sDropTableBkpLimitChangeHistory.execute();
//
//                String            qCreateTableBkpLimitChangeHistory = "CREATE TABLE BKP_LIMIT_CHANGE_HISTORY LIKE `LIMIT_CHANGE_HISTORY`";
//                PreparedStatement sCreateTableBkpLimitChangeHistory = titanLimitConnection.prepareStatement(qCreateTableBkpLimitChangeHistory);
//                sCreateTableBkpLimitChangeHistory.execute();
//
//                String            qInsertBkpLimitChangeHistory = "INSERT INTO BKP_LIMIT_CHANGE_HISTORY SELECT * FROM `LIMIT_CHANGE_HISTORY`";
//                PreparedStatement sInsertBkpLimitChangeHistory = titanLimitConnection.prepareStatement(qInsertBkpLimitChangeHistory);
//                sInsertBkpLimitChangeHistory.execute();
//                log.debug("Created backup for `LIMIT_CHANGE_HISTORY` table");

                String            qDropTableBkpLimitHistory = "DROP TABLE IF EXISTS BKP_LIMIT_HISTORY";
                PreparedStatement sDropTableBkpLimitHistory = billfoldConnection.prepareStatement(qDropTableBkpLimitHistory);
                sDropTableBkpLimitHistory.execute();

                String            qCreateTableBkpLimitHistory = "CREATE TABLE BKP_LIMIT_HISTORY LIKE `LIMIT_HISTORY`";
                PreparedStatement sCreateTableBkpLimitHistory = billfoldConnection.prepareStatement(qCreateTableBkpLimitHistory);
                sCreateTableBkpLimitHistory.execute();

                String            qInsertBkpLimitHistory = "INSERT INTO BKP_LIMIT_HISTORY SELECT * FROM `LIMIT_HISTORY`";
                PreparedStatement sInsertBkpLimitHistory = billfoldConnection.prepareStatement(qInsertBkpLimitHistory);
                sInsertBkpLimitHistory.execute();
                log.debug("Created backup for `LIMIT_HISTORY` table");

                String qCreateLimitUpdateAffectedUsers =
                    "CREATE TABLE IF NOT EXISTS LIMIT_UPDATE_AFFECTED_USERS ("
                        + " `ID`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT 'Auto generated primary key',"
                        + " `SITE_ID`          int            NOT NULL,"
                        + " `USER_ID`          BIGINT         NOT NULL,"
                        + " `TITAN_PLAYER_ID`  BIGINT         NULL,"
                        + " `CREATED_TIME`     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + " `FIRST_NAME`       varchar(255)   NULL,"
                        + " `LAST_NAME`        varchar(255)   NULL,"
                        + " `EMAIL`            varchar(255)   NULL,"
                        + " `STATUS`           varchar(255)   NULL,"
                        + " `BIRTH_DATE`       DATETIME       NULL,"
                        + " `COUNTRY`          varchar(3)     NULL,"
                        + " `LIMIT_TYPE`       varchar(255)   NULL,"
                        + " `LIMIT_PERIOD`     varchar(255)   NULL,"
                        + " `AMOUNT_BEFORE`    DECIMAL(17, 2) NULL,"
                        + " `AMOUNT_AFTER`     DECIMAL(17, 2) NULL,"
                        + " `PHASE`            varchar(255)   NULL,"
                        + "  PRIMARY KEY (`ID`)"
                        + " ) ENGINE = InnoDB"
                        + " DEFAULT CHARSET = utf8";

                PreparedStatement sCreateLimitUpdateAffectedUsersBillfold = billfoldConnection.prepareStatement(qCreateLimitUpdateAffectedUsers);
                sCreateLimitUpdateAffectedUsersBillfold.execute();
                log.debug("Created `LIMIT_UPDATE_AFFECTED_USERS` table");

                String qCreateLimitUpdateFailedUsers =
                    "CREATE TABLE IF NOT EXISTS LIMIT_UPDATE_FAILED_USERS ("
                        + " `ID`               BIGINT         NOT NULL AUTO_INCREMENT COMMENT 'Auto generated primary key',"
                        + " `SITE_ID`          int            NOT NULL,"
                        + " `USER_ID`          BIGINT         NOT NULL,"
                        + " `TITAN_PLAYER_ID`  BIGINT         NULL,"
                        + " `CREATED_TIME`     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + " `PHASE`            varchar(255)   NULL,"
                        + " `FAIL_REASON`      varchar(255)   NULL,"
                        + "  PRIMARY KEY (`ID`)"
                        + " ) ENGINE = InnoDB"
                        + " DEFAULT CHARSET = utf8";

                PreparedStatement sCreateLimitUpdateFailedUsersBillfold = billfoldConnection.prepareStatement(qCreateLimitUpdateFailedUsers);
                sCreateLimitUpdateFailedUsersBillfold.execute();
                log.debug("Created `LIMIT_UPDATE_FAILED_USERS` table");

                commit(billfoldConnection, titanLimitConnection);
            } catch (Exception e) {
                rollback(billfoldConnection, titanLimitConnection);
                log.error("PrepareTablesTask failed", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
