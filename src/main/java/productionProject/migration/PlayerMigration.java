package productionProject.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import productionProject.Main;

@Slf4j
public class PlayerMigration extends AbstractMigration {

    private Logger loggerIgnoredEmptyCountry                   = LoggerFactory.getLogger("loggerIgnoredEmptyCountry");
    private Logger loggerIgnoredSwedishWithoutNationalIdNumber = LoggerFactory.getLogger("loggerIgnoredSwedishWithoutNationalIdNumber");
    private Logger loggerIgnoredWwedishNonSEK                  = LoggerFactory.getLogger("loggerIgnoredWwedishNonSEK");

    private PreparedStatement sSelectBillfoldUser;
    private PreparedStatement sUpdateBillfoldUser;

    private PreparedStatement sInsertTitanPlayer;
    private PreparedStatement sInsertTitanPlayerToUser;

    private PreparedStatement sSelectTitanPlayerIdentifier;
    private PreparedStatement sSelectTitanPlayerSiteIdUserId;

    public PlayerMigration(
        final Connection billfoldConnection,
        final Connection titanPlayerConnection
    ) throws SQLException {
        // Prepare SQL statements

        // titan_player statements
        sInsertTitanPlayer = titanPlayerConnection.prepareStatement(
            "insert into PLAYER (CREATED_TIME, IDENTIFIER, SITE_ID, USER_ID, COUNTRY, CURRENCY, VERSION) values (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        sInsertTitanPlayerToUser = titanPlayerConnection.prepareStatement(
            "insert into PLAYER_TO_USER (PLAYER_ID, SITE_ID, USER_ID, IDENTIFIER, CREATED_TIME, DISCONNECTED, VERSION) values (?,?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);

        //  billfold statements
        sSelectBillfoldUser = billfoldConnection.prepareStatement(
            "select PK_USER_ID, SITE_ID, NATIONAL_ID_NUMBER, COUNTRY, REGISTRATION_CURRENCY from " + Main.BILLFOLD_TABLE_PREFIX + "USER WHERE PK_USER_ID = ?");
        sUpdateBillfoldUser =
            billfoldConnection.prepareStatement(
                "update " + Main.BILLFOLD_TABLE_PREFIX + "USER set TITAN_PLAYER_ID = ? where PK_USER_ID = ?");

        // titan_player select
        sSelectTitanPlayerIdentifier = titanPlayerConnection.prepareStatement("SELECT ID FROM PLAYER WHERE IDENTIFIER=?");
        sSelectTitanPlayerSiteIdUserId = titanPlayerConnection.prepareStatement("SELECT ID FROM PLAYER WHERE SITE_ID=? AND USER_ID=?");

        statements.add(sInsertTitanPlayer);
        statements.add(sInsertTitanPlayerToUser);
        statements.add(sSelectBillfoldUser);
        statements.add(sUpdateBillfoldUser);
        statements.add(sSelectTitanPlayerIdentifier);
        statements.add(sSelectTitanPlayerSiteIdUserId);
    }

    /**
     * Migrate billfold USER to Titan PLAYER
     *
     * @param userId Id of the billfold user
     * @return Id of the Titan player
     */
    public Long migrate(final Long userId) throws SQLException {
        Timestamp createdTime = new Timestamp(System.currentTimeMillis());

        Long    playerId       = null;
        boolean existedInTitan = false;
        sSelectBillfoldUser.setLong(1, userId);
        ResultSet billfoldUserRS = sSelectBillfoldUser.executeQuery();
        if (billfoldUserRS.next()) {
            int     siteId             = billfoldUserRS.getInt("SITE_ID");
            String  nationalIdNumber   = billfoldUserRS.getString("NATIONAL_ID_NUMBER");
            String  country            = billfoldUserRS.getString("COUNTRY");
            String  currency           = billfoldUserRS.getString("REGISTRATION_CURRENCY");
            boolean isSwedenMarketUser = Main.SWEDEN_MARKET_SITE_IDS.contains(siteId) && country != null && "SWE".equals(country);
            if (country == null) {
                log.error("[userId: {}] user does not have country, will be ignored.", userId);
                loggerIgnoredEmptyCountry.debug(userId.toString());
            } else if (isSwedenMarketUser && nationalIdNumber == null) {
                log.error("[userId: {}] Sweden user has empty nationalIdNumber, will be ignored.", userId);
                loggerIgnoredSwedishWithoutNationalIdNumber.debug(userId.toString());
            } else if (isSwedenMarketUser && !"SEK".equals(currency)) {
                log.error("[userId: {}] Sweden user has different currency than SEK ({}), will be ignored.", userId, currency);
                loggerIgnoredWwedishNonSEK.debug("{}, {}", userId.toString(), currency);
            } else {
                // check if Player exists or not
                ResultSet playerResultSet;
                if (isSwedenMarketUser) {
                    sSelectTitanPlayerIdentifier.setString(1, nationalIdNumber);

                    playerResultSet = sSelectTitanPlayerIdentifier.executeQuery();
                } else {
                    sSelectTitanPlayerSiteIdUserId.setInt(1, siteId);
                    sSelectTitanPlayerSiteIdUserId.setLong(2, userId);

                    playerResultSet = sSelectTitanPlayerSiteIdUserId.executeQuery();
                }

                if (playerResultSet != null && playerResultSet.next()) {
                    playerId = playerResultSet.getLong("ID");
                    existedInTitan = true;

                    log.debug("Found existing player in Titan: {}", playerId);
                }

                if (playerId == null) {
                    // Insert to titan_player.PLAYER table
                    sInsertTitanPlayer.setTimestamp(1, createdTime);

                    if (isSwedenMarketUser) {
                        sInsertTitanPlayer.setString(2, nationalIdNumber);
                        sInsertTitanPlayer.setNull(3, Types.INTEGER);
                        sInsertTitanPlayer.setNull(4, Types.BIGINT);
                    } else {
                        sInsertTitanPlayer.setNull(2, Types.VARCHAR);
                        sInsertTitanPlayer.setInt(3, siteId);
                        sInsertTitanPlayer.setLong(4, userId);
                    }

                    sInsertTitanPlayer.setString(5, country);
                    sInsertTitanPlayer.setString(6, currency);
                    sInsertTitanPlayer.setInt(7, 0);
                    sInsertTitanPlayer.executeUpdate();

                    // Get generated player's id
                    playerId = getInsertedId(sInsertTitanPlayer);

                    log.debug("Registered new player in Titan: {}", playerId);
                }

                // Insert to titan_player.PLAYER_TO_USER table
                sInsertTitanPlayerToUser.setLong(1, playerId);
                sInsertTitanPlayerToUser.setInt(2, siteId);
                sInsertTitanPlayerToUser.setLong(3, userId);
                sInsertTitanPlayerToUser.setString(4, nationalIdNumber);
                sInsertTitanPlayerToUser.setTimestamp(5, createdTime);
                sInsertTitanPlayerToUser.setBoolean(6, false);
                sInsertTitanPlayerToUser.setInt(7, 0);
                sInsertTitanPlayerToUser.executeUpdate();

                log.debug("Inserted new player to user, PLAYER_TO_USER.ID: {}", getInsertedId(sInsertTitanPlayerToUser));

                // update billfold.USER table
                sUpdateBillfoldUser.setLong(1, playerId);
                sUpdateBillfoldUser.setLong(2, userId);
                sUpdateBillfoldUser.executeUpdate();

                log.debug("Migrated: user id: {}, site id: {}, national id number: {}, country: {}, currency: {}, existed: {}", userId, siteId, nationalIdNumber, country, currency, existedInTitan);
            }
        }
        billfoldUserRS.close();
        return playerId;
    }

}
