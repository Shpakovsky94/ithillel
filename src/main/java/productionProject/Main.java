package productionProject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import productionProject.datasource.Datasource;
import productionProject.task.DepositLimitUsageMigratingTask;
import productionProject.task.LossLimitMigratingTask;
import productionProject.task.LossLimitUsageMigratingTask;
import productionProject.task.PlayerAndLimitMigratingTask;
import productionProject.task.SessionLimitMigratingTask;
import productionProject.youngPlayerLimitUpdate.prepareTable.PrepareTablesTask;
import productionProject.youngPlayerLimitUpdate.setPendingLimits.SetPendingLimitTask;
import productionProject.youngPlayerLimitUpdate.updateLossLimit.UpdateLimitTask;

@Slf4j
public class Main {
    private final static String DEFAULT_APP_PROPERTIES = "migration/app.properties";
    public static        String PHASE;

    public static String LIMIT_END_TIME_MODE;
    public static String DEPOSIT_LIMIT_TYPE = "deposit";
    public static String SESSION_LIMIT_TYPE = "session";
    public static String LOSS_LIMIT_TYPE    = "loss";
    public static String WAGER_LIMIT_TYPE   = "loss";

    public static List<Long>   INCLUDE_SITE_IDS       = new ArrayList<>();
    public static List<Long>   EXCLUDE_SITE_IDS       = new ArrayList<>();
    public static Set<Integer> SWEDEN_MARKET_SITE_IDS = new HashSet<>();
    public static List<String> ONLY_MIGRATE_SSN       = new ArrayList<>();

    public static List<Long> USER_STATUSES = new ArrayList<>();

    public static String BILLFOLD_TABLE_PREFIX                      = "";
    public static String BILLFOLD_TABLE_PAYMENT_NAME_WITHOUT_PREFIX = "PAYMENT";

    public static String PLAYER_LINKING_RULES;
    public static String LIMIT_SHARING_RULES;

    public static List<String> PLAYER_LINKING_COUNTRIES             = new ArrayList<>();
    public static int          FUZZY_MATCH_ALLOWED_DIFFERENCE_VALUE = 0;

    public static List<String> LOSS_LIMIT_SHARING_COUNTRIES        = new ArrayList<>();
    public static List<String> DEPOSIT_LIMIT_SHARING_COUNTRIES     = new ArrayList<>();
    public static List<String> SESSION_LIMIT_SHARING_COUNTRIES     = new ArrayList<>();
    public static List<String> MAX_BALANCE_LIMIT_SHARING_COUNTRIES = new ArrayList<>();

    public static Long       UPDATE_LIMIT_BO_USER;
    public static String     UPDATE_LIMIT_LIMIT_TYPE;
    public static String     UPDATE_LIMIT_PERIOD_TYPE;
    public static BigDecimal UPDATE_LIMIT_NEW_AMOUNT;

    public static void main(String[] args) throws SQLException {
        String appPropertiesPath = DEFAULT_APP_PROPERTIES;

        if (args.length > 0) {
            appPropertiesPath = args[0];
        }
        if (args.length > 1) {
            PHASE = args[1];
        }
        if (args.length > 2) {
            UPDATE_LIMIT_BO_USER = Long.parseLong(args[2]);
        }
        if (args.length > 3) {
            UPDATE_LIMIT_LIMIT_TYPE = args[3];
        }
        if (args.length > 4) {
            UPDATE_LIMIT_PERIOD_TYPE = args[4];
        }
        if (args.length > 5) {
            UPDATE_LIMIT_NEW_AMOUNT = BigDecimal.valueOf(Long.parseLong(args[5]));
        }

        Properties prop;

        if (appPropertiesPath.equals("fromEnvironmentVariables")) {
            prop = loadPropertiesFromSystemEnv();
        } else {
            prop = loadProperties(appPropertiesPath);
        }

        if (prop == null) {
            return;
        }

        log.debug("Inline args variables: {}", Arrays.toString(args));
        log.debug("Environment properties: {}", prop.toString());

        LIMIT_END_TIME_MODE = prop.getProperty("endTimeMode");

        String includeSiteIds = prop.getProperty("includeSiteIds");
        if (includeSiteIds != null) {
            INCLUDE_SITE_IDS = Stream.of(includeSiteIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
        }

        String excludeSiteIds = prop.getProperty("excludeSiteIds");
        if (excludeSiteIds != null) {
            EXCLUDE_SITE_IDS = Stream.of(excludeSiteIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
        }

        String swedenMarketSiteIds = prop.getProperty("swedenMarketSiteIds");
        if (swedenMarketSiteIds != null) {
            SWEDEN_MARKET_SITE_IDS = Stream.of(swedenMarketSiteIds.split(",")).map(Integer::valueOf).collect(Collectors.toSet());
        }

        String onlyMigrateSSN = prop.getProperty("onlyMigrateSSN");
        if (onlyMigrateSSN != null) {
            ONLY_MIGRATE_SSN = Stream.of(onlyMigrateSSN.split(",")).collect(Collectors.toList());
        }

        String userStatuses = prop.getProperty("userStatuses");
        if (userStatuses != null) {
            USER_STATUSES = Stream.of(userStatuses.split(",")).map(Long::valueOf).collect(Collectors.toList());
        }

        String billfoldTablePrefix = prop.getProperty("billfoldTablePrefix");
        if (billfoldTablePrefix != null) {
            BILLFOLD_TABLE_PREFIX = billfoldTablePrefix;
        }

        String billfoldTablePAYMENTNameWithoutPrefix = prop.getProperty("billfoldTablePAYMENTNameWithoutPrefix");
        if (billfoldTablePAYMENTNameWithoutPrefix != null && !"".equals(billfoldTablePAYMENTNameWithoutPrefix)) {
            BILLFOLD_TABLE_PAYMENT_NAME_WITHOUT_PREFIX = billfoldTablePAYMENTNameWithoutPrefix;
        }
        String playerLinkingRules = prop.getProperty("playerLinkingRules");
        if (playerLinkingRules != null && !"".equals(playerLinkingRules)) {
            PLAYER_LINKING_RULES = playerLinkingRules;
        }

        String limitSharingRules = prop.getProperty("limitSharingRules");
        if (limitSharingRules != null && !"".equals(limitSharingRules)) {
            LIMIT_SHARING_RULES = limitSharingRules;
        }

        String playerLinkingCountries = prop.getProperty("playerLinkingCountries");
        if (playerLinkingCountries != null && !"".equals(playerLinkingCountries)) {
            PLAYER_LINKING_COUNTRIES = Arrays.asList(playerLinkingCountries.split("\\s*,\\s*"));
        }

        String fuzzyMatchAllowedDifferenceValue = prop.getProperty("fuzzyMatchAllowedDifferenceValue");
        if (fuzzyMatchAllowedDifferenceValue != null && !"".equals(fuzzyMatchAllowedDifferenceValue)) {
            FUZZY_MATCH_ALLOWED_DIFFERENCE_VALUE = Integer.parseInt(fuzzyMatchAllowedDifferenceValue);
        }

        String lossLimitSharingCountries = prop.getProperty("lossLimitSharingCountries");
        if (lossLimitSharingCountries != null && !"".equals(lossLimitSharingCountries)) {
            LOSS_LIMIT_SHARING_COUNTRIES = Arrays.asList(lossLimitSharingCountries.split("\\s*,\\s*"));
        }

        String depositLimitSharingCountries = prop.getProperty("depositLimitSharingCountries");
        if (depositLimitSharingCountries != null && !"".equals(depositLimitSharingCountries)) {
            DEPOSIT_LIMIT_SHARING_COUNTRIES = Arrays.asList(depositLimitSharingCountries.split("\\s*,\\s*"));
        }

        String sessionLimitSharingCountries = prop.getProperty("sessionLimitSharingCountries");
        if (sessionLimitSharingCountries != null && !"".equals(sessionLimitSharingCountries)) {
            SESSION_LIMIT_SHARING_COUNTRIES = Arrays.asList(sessionLimitSharingCountries.split("\\s*,\\s*"));
        }

        String maxBalanceLimitSharingCountries = prop.getProperty("maxBalanceLimitSharingCountries");
        if (maxBalanceLimitSharingCountries != null && !"".equals(maxBalanceLimitSharingCountries)) {
            MAX_BALANCE_LIMIT_SHARING_COUNTRIES = Arrays.asList(maxBalanceLimitSharingCountries.split("\\s*,\\s*"));
        }

        int threads = Integer.parseInt(prop.getProperty("threads"));

        log.debug("Start to migrate with configuration - {}", appPropertiesPath);
        long startTs = System.currentTimeMillis();

        Datasource billfoldDatasource = new Datasource(prop.getProperty("billfold.datasource.url"),
                                                       prop.getProperty("billfold.datasource.username"),
                                                       prop.getProperty("billfold.datasource.password"),
                                                       threads);

        Datasource titanLimitDatasource = new Datasource(prop.getProperty("titan.limit.datasource.url"),
                                                         prop.getProperty("titan.limit.datasource.username"),
                                                         prop.getProperty("titan.limit.datasource.password"),
                                                         threads);

        Datasource titanPlayerDatasource = new Datasource(prop.getProperty("titan.player.datasource.url"),
                                                          prop.getProperty("titan.player.datasource.username"),
                                                          prop.getProperty("titan.player.datasource.password"),
                                                          threads);

        if (PHASE.equals("playersAndLimits")) {
            List<Long>      offsetInformation = calculatePageSizeAndThreadSizeForUsers(billfoldDatasource, threads - 1);
            ExecutorService executor          = Executors.newFixedThreadPool(threads);

            for (long i = 0; i < offsetInformation.get(1); i++) {
                PlayerAndLimitMigratingTask playerAndLimitMigratingTask = new PlayerAndLimitMigratingTask(i, offsetInformation.get(0), billfoldDatasource, titanPlayerDatasource, titanLimitDatasource);
                executor.execute(playerAndLimitMigratingTask);
            }

            executor.shutdown();
        } else if (PHASE.equals("limitUsages")) {
            List<Long>      offsetInformation = calculatePageSizeAndThreadSizeForPlayersLimitType(titanLimitDatasource, "deposit", threads - 1);
            ExecutorService executor          = Executors.newFixedThreadPool(threads);

            for (long i = 0; i < offsetInformation.get(1); i++) {
                DepositLimitUsageMigratingTask limitUsageMigratingTask = new DepositLimitUsageMigratingTask(i, offsetInformation.get(0), billfoldDatasource, titanPlayerDatasource,
                                                                                                            titanLimitDatasource);
                executor.execute(limitUsageMigratingTask);
            }
            executor.shutdown();
        } else if (PHASE.equals("sessionLimits")) {
            List<Long>      offsetInformation = calculatePageSizeAndThreadSizeForSessionLimits(billfoldDatasource, threads - 1);
            ExecutorService executor          = Executors.newFixedThreadPool(threads);

            for (long i = 0; i < offsetInformation.get(1); i++) {
                SessionLimitMigratingTask sessionLimitMigratingTask = new SessionLimitMigratingTask(i, offsetInformation.get(0), billfoldDatasource, titanLimitDatasource);
                executor.execute(sessionLimitMigratingTask);
            }
            executor.shutdown();
        } else if (PHASE.equals("lossLimits")) {
            List<Long>      offsetInformation = calculatePageSizeAndThreadSizeForLossLimits(billfoldDatasource, threads - 1);
            ExecutorService executor          = Executors.newFixedThreadPool(threads);

            for (long i = 0; i < offsetInformation.get(1); i++) {
                LossLimitMigratingTask lossLimitMigratingTask = new LossLimitMigratingTask(i, offsetInformation.get(0), billfoldDatasource, titanLimitDatasource);
                executor.execute(lossLimitMigratingTask);
            }
            executor.shutdown();
        } else if (PHASE.equals("lossLimitsUsages")) {
            List<Long>      offsetInformation = calculatePageSizeAndThreadSizeForPlayersLimitType(titanLimitDatasource, "loss", threads - 1);
            ExecutorService executor          = Executors.newFixedThreadPool(threads);

            for (long i = 0; i < offsetInformation.get(1); i++) {
                LossLimitUsageMigratingTask lossLimitMigratingTask = new LossLimitUsageMigratingTask(i, offsetInformation.get(0), billfoldDatasource, titanPlayerDatasource, titanLimitDatasource);
                executor.execute(lossLimitMigratingTask);
            }
            executor.shutdown();
        } else if (PHASE.equals("backupTables")) {
            BackupTablesTask backupTablesTask = new BackupTablesTask(billfoldDatasource, titanPlayerDatasource, titanLimitDatasource);
            backupTablesTask.run();
        } else if (PHASE.equals("populatePlayersData")) {
            List<Long>      offsetInformation = calculatePageSizeAndThreadSizeForUsers(billfoldDatasource, threads);
            ExecutorService executor          = Executors.newFixedThreadPool(threads);

            for (long i = 0; i < offsetInformation.get(1); i++) {
                PopulatePlayersDataTask populatePlayersDataTask = new PopulatePlayersDataTask(i, offsetInformation.get(0), billfoldDatasource, titanPlayerDatasource, titanLimitDatasource);
                executor.execute(populatePlayersDataTask);
            }
            executor.shutdown();
        } else if (PHASE.equals("mergePlayers")) {
            MergePlayersTask mergePlayersTask = new MergePlayersTask(billfoldDatasource, titanPlayerDatasource, titanLimitDatasource);
            mergePlayersTask.run();
        } else if (PHASE.equals("mergeLimits")) {
            MergeLimitsTask mergeLimitsTask = new MergeLimitsTask(billfoldDatasource, titanPlayerDatasource, titanLimitDatasource);
            mergeLimitsTask.run();
        } else if (PHASE.equals("prepareTables")) {
            PrepareTablesTask prepareTablesTask = new PrepareTablesTask(billfoldDatasource, titanLimitDatasource);
            prepareTablesTask.run();
        } else if (PHASE.equals("setPendingLimits")) {
            SetPendingLimitTask setPendingLimitTask = new SetPendingLimitTask(billfoldDatasource, titanLimitDatasource);
            setPendingLimitTask.run();
        } else if (PHASE.equals("updateLimits")) {
            UpdateLimitTask updateLimitTask = new UpdateLimitTask(billfoldDatasource, titanLimitDatasource);
            updateLimitTask.run();
        }
        log.debug("Finished migration in {} milliseconds", System.currentTimeMillis() - startTs);
    }

    private static List<Long> calculatePageSizeAndThreadSizeForPlayersLimitType(
        Datasource titanLimitDatasource,
        String limitType,
        int threads
    ) {
        Long playerCount = null;
        try (Connection connection = titanLimitDatasource.getConnection()) {
            // 1. get player with limits count
            // 2. split into groups with count of threads
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(DISTINCT (PLAYER_ID)) FROM `LIMIT` WHERE LIMIT_TYPE = ?");
            statement.setString(1, limitType);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                playerCount = resultSet.getLong(1);
            }
            statement.close();
        } catch (SQLException e) {
            log.error("titan_limit SQL error", e);
        }
        log.debug("Total player with limits count: " + playerCount);

        return getOffsetInformation(threads, playerCount);
    }

    private static List<Long> calculatePageSizeAndThreadSizeForSessionLimits(
        Datasource billfoldDatasource,
        int threads
    ) {
        Long limitCount = null;
        try (Connection connection = billfoldDatasource.getConnection()) {
            String query = "SELECT COUNT(*) FROM " + Main.BILLFOLD_TABLE_PREFIX + "GAMING_LIMIT GL JOIN " + Main.BILLFOLD_TABLE_PREFIX
                + "USER U on GL.FK_USER_ID = U.PK_USER_ID WHERE TITAN_PLAYER_ID IS NOT NULL AND TIME_LIMIT_AMOUNT IS NOT NULL AND TIME_LIMIT_START_TIME IS NOT NULL AND TIME_LIMIT_PERIOD IS NOT NULL";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet         resultSet = statement.executeQuery();

            if (resultSet.next()) {
                limitCount = resultSet.getLong(1);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        log.debug("Total session time limit count: " + limitCount);
        return getOffsetInformation(threads, limitCount);
    }

    private static List<Long> calculatePageSizeAndThreadSizeForLossLimits(
        Datasource billfoldDatasource,
        int threads
    ) {
        Long limitCount = null;
        try (Connection connection = billfoldDatasource.getConnection()) {
            String query = "SELECT COUNT(*) FROM " + Main.BILLFOLD_TABLE_PREFIX + "GAMING_LIMIT GL JOIN " + Main.BILLFOLD_TABLE_PREFIX
                + "USER U on GL.FK_USER_ID = U.PK_USER_ID WHERE U.TITAN_PLAYER_ID IS NOT NULL AND LOSS_LIMIT_AMOUNT IS NOT NULL AND LOSS_LIMIT_PERIOD IS NOT NULL AND LOSS_LIMIT_START_TIME IS NOT NULL AND (LOSS_LIMIT_AUTO_RENEW = true OR ( (LOSS_LIMIT_AUTO_RENEW IS NULL OR LOSS_LIMIT_AUTO_RENEW = false) AND LOSS_LIMIT_END_TIME > NOW()))";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet         resultSet = statement.executeQuery();

            if (resultSet.next()) {
                limitCount = resultSet.getLong(1);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        log.debug("Total loss time limit count: " + limitCount);
        return getOffsetInformation(threads, limitCount);
    }


    private static List<Long> calculatePageSizeAndThreadSizeForUsers(
        Datasource billfoldDatasource,
        int threads
    ) {
        Long userCount = null;
        try (Connection connection = billfoldDatasource.getConnection()) {
            // 1. get user count
            // 2. split into groups with count of threads

            String query = "SELECT COUNT(*) FROM `" + Main.BILLFOLD_TABLE_PREFIX + "USER` WHERE TRUE";
            if (USER_STATUSES.size() > 0) {
                query += " AND STATUS IN " + Util.createInClauseForStatement(USER_STATUSES.size());
            }
            if (INCLUDE_SITE_IDS.size() > 0) {
                query += " AND SITE_ID IN " + Util.createInClauseForStatement(INCLUDE_SITE_IDS.size());
            }
            if (EXCLUDE_SITE_IDS.size() > 0) {
                query += " AND SITE_ID NOT IN " + Util.createInClauseForStatement(EXCLUDE_SITE_IDS.size());
            }
            if (ONLY_MIGRATE_SSN.size() > 0) {
                query += " AND NATIONAL_ID_NUMBER IN " + Util.createInClauseForStatement(ONLY_MIGRATE_SSN.size());
            }

            PreparedStatement statement = connection.prepareStatement(query);
            int               lastIndex = 0;
            int               offset    = 0;
            for (int i = 0; i < USER_STATUSES.size(); i++) {
                lastIndex = i + offset + 1;
                statement.setLong(lastIndex, USER_STATUSES.get(i));
            }
            offset = lastIndex;
            for (int i = 0; i < INCLUDE_SITE_IDS.size(); i++) {
                lastIndex = i + offset + 1;
                statement.setLong(lastIndex, INCLUDE_SITE_IDS.get(i));
            }
            offset = lastIndex;
            for (int i = 0; i < EXCLUDE_SITE_IDS.size(); i++) {
                lastIndex = i + offset + 1;
                statement.setLong(lastIndex, EXCLUDE_SITE_IDS.get(i));
            }
            offset = lastIndex;
            for (int i = 0; i < ONLY_MIGRATE_SSN.size(); i++) {
                lastIndex = i + offset + 1;
                statement.setString(lastIndex, ONLY_MIGRATE_SSN.get(i));
            }

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userCount = resultSet.getLong(1);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        log.debug("Total user count: " + userCount);
        return getOffsetInformation(threads, userCount);
    }

    private static List<Long> getOffsetInformation(
        int threads,
        Long recordCount
    ) {
        List<Long> offsetInformation = new ArrayList<>();
        if (recordCount != null) {
            if (threads > 0) {
                if (recordCount < threads) {
                    log.debug("Count in each thread: " + recordCount);
                    log.debug("Total threads: 1");

                    offsetInformation.add(recordCount);
                    offsetInformation.add((long) 1);
                } else {
                    Long countInEachThread = recordCount / threads;
                    int  totalOffsetGroup  = recordCount > countInEachThread * threads ? threads + 1 : threads;

                    log.debug("Count in each thread: " + countInEachThread);
                    log.debug("Total threads: " + totalOffsetGroup);

                    offsetInformation.add(countInEachThread);
                    offsetInformation.add((long) totalOffsetGroup);
                }
            } else {
                log.debug("Count in each thread: " + recordCount);
                log.debug("Total threads: 1");

                offsetInformation.add(recordCount);
                offsetInformation.add((long) 1);
            }
        }

        return offsetInformation;
    }

    private static Properties loadProperties(final String absolutePath) {
        try {
            Properties  properties  = new Properties();
            InputStream inputStream = new FileInputStream(absolutePath);
            properties.load(inputStream);

            return properties;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Properties loadPropertiesFromSystemEnv() {
        Properties properties               = new Properties();
        String     billfoldDbHost           = System.getenv("BILLFOLD_DB_HOST");
        String     billfoldDbPort           = System.getenv("BILLFOLD_DB_PORT");
        String     billfoldDbServerTimezone = System.getenv("DB_SERVER_TIMEZONE");
        String     billfoldDbName           = System.getenv("BILLFOLD_DB_NAME");

        String titanLimitDbHost           = System.getenv("TITAN_LIMIT_DB_HOST");
        String titanLimitDbPort           = System.getenv("TITAN_LIMIT_DB_PORT");
        String titanLimitDbServerTimezone = System.getenv("DB_SERVER_TIMEZONE");
        String titanLimitDbName           = System.getenv("TITAN_LIMIT_DB_NAME");

        String titanPlayerDbHost           = System.getenv("TITAN_PLAYER_DB_HOST");
        String titanPlayerDbPort           = System.getenv("TITAN_PLAYER_DB_PORT");
        String titanPlayerDbServerTimezone = System.getenv("DB_SERVER_TIMEZONE");
        String titanPlayerDbName           = System.getenv("TITAN_PLAYER_DB_NAME");
        String billfoldTablePrefix         = System.getenv("BILLFOLD_TABLE_PREFIX");

        String playerLinkingCountries = System.getenv("PLAYER_LINKING_COUNTRIES");

        String lossLimitSharingCountries       = System.getenv("LOSS_LIMIT_SHARING_COUNTRIES");
        String depositLimitSharingCountries    = System.getenv("DEPOSIT_LIMIT_SHARING_COUNTRIES");
        String sessionLimitSharingCountries    = System.getenv("SESSION_LIMIT_SHARING_COUNTRIES");
        String maxBalanceLimitSharingCountries = System.getenv("MAX_BALANCE_LIMIT_SHARING_COUNTRIES");

        properties.put("threads", System.getenv("THREADS"));
        properties.put("playerLinkingCountries", playerLinkingCountries != null ? playerLinkingCountries : "");
        properties.put("fuzzyMatchAllowedDifferenceValue", System.getenv("FUZZY_MATCH_ALLOWED_DIFFERENCE_VALUE"));

        properties.put("lossLimitSharingCountries", lossLimitSharingCountries != null ? lossLimitSharingCountries : "");
        properties.put("depositLimitSharingCountries", depositLimitSharingCountries != null ? depositLimitSharingCountries : "");
        properties.put("sessionLimitSharingCountries", sessionLimitSharingCountries != null ? sessionLimitSharingCountries : "");
        properties.put("maxBalanceLimitSharingCountries", maxBalanceLimitSharingCountries != null ? maxBalanceLimitSharingCountries : "");

        properties.put("billfoldTablePrefix", billfoldTablePrefix != null ? billfoldTablePrefix : "");
        properties.put("billfold.datasource.url", "jdbc:mysql://" + billfoldDbHost + ":" + billfoldDbPort + "/" + billfoldDbName
            + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&useLegacyDatetimeCode=false&serverTimezone=" + billfoldDbServerTimezone);
        properties.put("billfold.datasource.username", System.getenv("BILLFOLD_DB_USER_NAME"));
        properties.put("billfold.datasource.password", System.getenv("BILLFOLD_DB_PASSWORD"));
        properties.put("titan.limit.datasource.url", "jdbc:mysql://" + titanLimitDbHost + ":" + titanLimitDbPort + "/" + titanLimitDbName
            + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&useLegacyDatetimeCode=false&serverTimezone=" + titanLimitDbServerTimezone);
        properties.put("titan.limit.datasource.username", System.getenv("TITAN_LIMIT_DB_USER_NAME"));
        properties.put("titan.limit.datasource.password", System.getenv("TITAN_LIMIT_DB_PASSWORD"));
        properties.put("titan.player.datasource.url", "jdbc:mysql://" + titanPlayerDbHost + ":" + titanPlayerDbPort + "/" + titanPlayerDbName
            + "?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&useLegacyDatetimeCode=false&serverTimezone=" + titanPlayerDbServerTimezone);
        properties.put("titan.player.datasource.username", System.getenv("TITAN_PLAYER_DB_USER_NAME"));
        properties.put("titan.player.datasource.password", System.getenv("TITAN_PLAYER_DB_PASSWORD"));
        return properties;
    }
}
