package productionProject.task;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import productionProject.datasource.Datasource;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractMigratingTask implements Runnable {
    protected long taskNumber;
    protected long fetchSize;

    protected Datasource billfoldDatasource;
    protected Datasource titanPlayerDatasource;
    protected Datasource titanLimitDatasource;

    protected void setAutoCommitFalse(final Connection... connections) throws SQLException {
        for (Connection connection : connections) {
            connection.setAutoCommit(false);
        }
    }

    protected void commit(final Connection... connections) throws SQLException {
        for (Connection connection : connections) {
            connection.commit();
        }
    }

    protected void rollback(final Connection... connections) throws SQLException {
        for (Connection connection : connections) {
            connection.rollback();
        }
    }

}
