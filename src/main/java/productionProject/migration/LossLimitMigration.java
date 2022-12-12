package productionProject.migration;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import lombok.extern.slf4j.Slf4j;
import productionProject.Main;

@Slf4j
public class LossLimitMigration extends AbstractMigration {

    private PreparedStatement sSelectBillfoldLimit;

    public LossLimitMigration(
        final Connection billfoldConnection,
        final Connection titanLimitConnection
    ) throws SQLException {
        super(billfoldConnection, titanLimitConnection);
        // select loss limit from billfold
        sSelectBillfoldLimit = billfoldConnection.prepareStatement(
            "SELECT LOSS_LIMIT_AMOUNT, LOSS_LIMIT_PERIOD, LOSS_LIMIT_START_TIME, LOSS_LIMIT_AUTO_RENEW FROM " + Main.BILLFOLD_TABLE_PREFIX + "GAMING_LIMIT WHERE FK_USER_ID = ?");

        sInsertTitanLimit.setString(2, Main.LOSS_LIMIT_TYPE);

        sFindExistingLimitInTitan.setString(2, Main.LOSS_LIMIT_TYPE);

        statements.add(sSelectBillfoldLimit);
    }

    public void migrate(
        final Long userId,
        final Long playerId,
        final Integer siteId
    ) throws SQLException {
        log.debug(" Migrating loss limit for: user id: {}, site id: {}, player id: {}", userId, siteId, playerId);

        sSelectBillfoldLimit.setLong(1, userId);
        ResultSet billfoldLimitRS = sSelectBillfoldLimit.executeQuery();

        while (billfoldLimitRS.next()) {
            BigDecimal lossLimitAmount    = billfoldLimitRS.getBigDecimal("LOSS_LIMIT_AMOUNT");
            int        lossLimitPeriod    = billfoldLimitRS.getInt("LOSS_LIMIT_PERIOD");
            Timestamp  lossLimitTimeStart = billfoldLimitRS.getTimestamp("LOSS_LIMIT_START_TIME");
            boolean    lossLimitAutoRenew = billfoldLimitRS.getBoolean("LOSS_LIMIT_AUTO_RENEW");

            String periodType = lossLimitPeriod == 0 ? "daily" : lossLimitPeriod == 1 ? "weekly" : "monthly";

            saveLimitAndLimitChangeHistory(userId, playerId, periodType, siteId, lossLimitAmount, lossLimitAutoRenew, lossLimitTimeStart, null, null);
        }

    }
}
