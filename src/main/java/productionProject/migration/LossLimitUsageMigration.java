package productionProject.migration;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import productionProject.Main;

@Slf4j
public class LossLimitUsageMigration extends AbstractMigration {

    private Logger loggerPaymentLimitUsageUpdateSql = LoggerFactory.getLogger("paymentLimitUsageUpdateSql");

    private PreparedStatement sInsertLimitUsage;
    private PreparedStatement sUpdateLimitUsedAmount;
    private PreparedStatement sSelectLinkedUsers;
    private PreparedStatement sSelectPlayersLimits;
    private PreparedStatement sSelectGameRounds;
    private PreparedStatement sInsertLimitUsageHistory;

    public LossLimitUsageMigration(
        final Connection billfoldConnection,
        final Connection titanPlayerConnection,
        final Connection titanLimitConnection,
        final List<Integer> siteIds
    ) throws SQLException {
        // Select linked users in TITAN_PLAYER
        String query = "SELECT USER_ID, SITE_ID "
            + " FROM PLAYER_TO_USER "
            + " WHERE PLAYER_ID = ? "
            + " AND SITE_ID IN (";
        String holders = "";
        for (int i = 0; i < siteIds.size(); i++) {
            holders += ",?";
        }
        holders = holders.replaceFirst(",", "");
        query += holders + ")";
        sSelectLinkedUsers = titanPlayerConnection.prepareStatement(query);
        for (int i = 2; i < 2 + siteIds.size(); i++) {
            sSelectLinkedUsers.setInt(i, siteIds.get(i - 2));
        }

        // Select players from LIMIT
        sSelectPlayersLimits = titanLimitConnection.prepareStatement("SELECT ID, USED_AMOUNT, CURRENT_PERIOD_START_TIME, CURRENT_PERIOD_END_TIME "
                                                                         + "FROM `LIMIT` "
                                                                         + "WHERE PLAYER_ID = ? AND LIMIT_TYPE = ?");
        sSelectPlayersLimits.setString(2, "loss");

        // Select gameRound summary from GAME_ROUND
        sSelectGameRounds = billfoldConnection.prepareStatement("SELECT SUM(TOTAL_BET_AMOUNT) AS BET, SUM(TOTAL_WIN_AMOUNT) AS WIN, SUM(TOTAL_JACKPOT_WIN_AMOUNT) AS JACKPOT"
                                                                    + " FROM " + Main.BILLFOLD_TABLE_PREFIX + "GAME_ROUND gr "
                                                                    + " WHERE SITE_ID = ? AND FK_USER_ID = ? AND gr.CREATED_TIME  BETWEEN ? AND ?");

        // Insert titan_limit.LIMIT_USAGE
        sInsertLimitUsage = titanLimitConnection.prepareStatement("INSERT INTO LIMIT_USAGE (CREATED_TIME, PLAYER_ID, SITE_ID, USER_ID, VERSION) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        sInsertLimitUsage.setInt(5, 0);

        // Insert titan_limit.LIMIT_USAGE_HISTORY
        sInsertLimitUsageHistory = titanLimitConnection.prepareStatement(
            "INSERT INTO LIMIT_USAGE_HISTORY (CREATE_TIME, LIMIT_USAGE_ID, LIMIT_ID, DIRECTION, AMOUNT, USED_AMOUNT_BEFORE, USED_AMOUNT_AFTER, LIMIT_REACHED, ROLLBACK, VERSION) VALUES (?,?,?,?,?,?,?,?,?,?)");

        sInsertLimitUsageHistory.setBoolean(9, false);
        sInsertLimitUsageHistory.setInt(10, 0);

        // Update titan_limit.USED_AMOUNT
        sUpdateLimitUsedAmount = titanLimitConnection.prepareStatement("UPDATE `LIMIT` SET USED_AMOUNT = ? WHERE ID = ?");

        statements.add(sSelectLinkedUsers);
        statements.add(sSelectPlayersLimits);
        statements.add(sSelectGameRounds);
        statements.add(sInsertLimitUsageHistory);
        statements.add(sUpdateLimitUsedAmount);
        statements.add(sInsertLimitUsage);
    }

    public void migrateUsagesForPlayer(final Long playerId) throws SQLException {

        sSelectLinkedUsers.setLong(1, playerId);

        ResultSet linkedUsersRS = sSelectLinkedUsers.executeQuery();
        while (linkedUsersRS.next()) {
            long userId = linkedUsersRS.getLong("USER_ID");
            int  siteId = linkedUsersRS.getInt("SITE_ID");

            sSelectPlayersLimits.setLong(1, playerId);
            ResultSet playersLimitsRS = sSelectPlayersLimits.executeQuery();
            while (playersLimitsRS.next()) {
                long       limitId    = playersLimitsRS.getLong("ID");
                BigDecimal usedAmount = playersLimitsRS.getBigDecimal("USED_AMOUNT");
                Timestamp  startTime  = playersLimitsRS.getTimestamp("CURRENT_PERIOD_START_TIME");
                Timestamp  endTime    = playersLimitsRS.getTimestamp("CURRENT_PERIOD_END_TIME");

                sSelectGameRounds.setInt(1, siteId);
                sSelectGameRounds.setLong(2, userId);
                sSelectGameRounds.setTimestamp(3, startTime);
                sSelectGameRounds.setTimestamp(4, endTime);

                ResultSet gameRoundsRS = sSelectGameRounds.executeQuery();

                while (gameRoundsRS.next()) {

                    BigDecimal betAmount     = gameRoundsRS.getBigDecimal("BET");
                    BigDecimal winAmount     = gameRoundsRS.getBigDecimal("WIN");
                    BigDecimal jackpotAmount = gameRoundsRS.getBigDecimal("JACKPOT");
                    log.debug("  Select from GAME_ROUND where USER_ID = {} and SITE_ID ={} and CREATED_TIME between {} and {}. Results: betAmount: {}, winAmount: {}, jackpotAmount: {}", userId,
                              siteId, startTime, endTime, betAmount, winAmount, jackpotAmount);

                    if (betAmount != null || winAmount != null || jackpotAmount != null) {

                        sInsertLimitUsage.setTimestamp(1, startTime);
                        sInsertLimitUsage.setLong(2, playerId);
                        sInsertLimitUsage.setInt(3, siteId);
                        sInsertLimitUsage.setLong(4, userId);

                        BigDecimal currentUsedAmount = usedAmount;
                        if (betAmount != null) {
                            log.debug("   Insert bet usage and history. Amount: {}, limitId: {} ", betAmount, limitId);
                            sInsertLimitUsage.executeUpdate();
                            Long limitUsageId = getInsertedId(sInsertLimitUsage);
                            sInsertLimitUsageHistory.setTimestamp(1, startTime);
                            sInsertLimitUsageHistory.setLong(2, limitUsageId);
                            sInsertLimitUsageHistory.setLong(3, limitId);
                            sInsertLimitUsageHistory.setString(4, "increase");
                            sInsertLimitUsageHistory.setBigDecimal(5, betAmount);
                            sInsertLimitUsageHistory.setBigDecimal(6, currentUsedAmount);
                            sInsertLimitUsageHistory.setBigDecimal(7, currentUsedAmount.add(betAmount));
                            sInsertLimitUsageHistory.setBoolean(8, false);
                            sInsertLimitUsageHistory.executeUpdate();
                            currentUsedAmount = currentUsedAmount.add(betAmount);
                        }
                        if (winAmount != null || jackpotAmount != null) {
                            sInsertLimitUsage.executeUpdate();
                            Long limitUsageId = getInsertedId(sInsertLimitUsage);

                            BigDecimal totalWinAmount = BigDecimal.ZERO;
                            if (winAmount != null) {
                                totalWinAmount = totalWinAmount.add(winAmount);
                            }
                            if (jackpotAmount != null) {
                                totalWinAmount = totalWinAmount.add(jackpotAmount);
                            }
                            log.debug("   Insert bet usage and history. Amount: {}, limitId: {} ", totalWinAmount, limitId);

                            sInsertLimitUsageHistory.setTimestamp(1, startTime);
                            sInsertLimitUsageHistory.setLong(2, limitUsageId);
                            sInsertLimitUsageHistory.setLong(3, limitId);
                            sInsertLimitUsageHistory.setString(4, "decrease");
                            sInsertLimitUsageHistory.setBigDecimal(5, totalWinAmount);
                            sInsertLimitUsageHistory.setBigDecimal(6, currentUsedAmount);
                            sInsertLimitUsageHistory.setBigDecimal(7, currentUsedAmount.subtract(totalWinAmount));
                            sInsertLimitUsageHistory.setBoolean(8, false);
                            sInsertLimitUsageHistory.executeUpdate();

                            currentUsedAmount = currentUsedAmount.subtract(totalWinAmount);
                        }

                        sUpdateLimitUsedAmount.setBigDecimal(1, currentUsedAmount);
                        sUpdateLimitUsedAmount.setLong(2, limitId);
                        sUpdateLimitUsedAmount.executeUpdate();
                    }


                }
            }
        }
    }


}
