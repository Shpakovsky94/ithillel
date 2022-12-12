package productionProject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import productionProject.datasource.Datasource;
import productionProject.task.PrepareTablesTask;
import productionProject.task.SetPendingLimitTask;
import productionProject.task.UpdateLimitTask;

@Slf4j
public class Main {
    private final static String DEFAULT_APP_PROPERTIES = "migration/application.properties";
    public static        String PHASE;

    public static String LIMIT_END_TIME_MODE;

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
            prop = new Properties();
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

        switch (PHASE) {
            case "prepareTables":
                PrepareTablesTask prepareTablesTask = new PrepareTablesTask(billfoldDatasource, titanLimitDatasource);
                prepareTablesTask.run();
                break;
            case "setPendingLimits":
                SetPendingLimitTask setPendingLimitTask = new SetPendingLimitTask(billfoldDatasource, titanLimitDatasource);
                setPendingLimitTask.run();
                break;
            case "updateLimits":
                UpdateLimitTask updateLimitTask = new UpdateLimitTask(billfoldDatasource, titanLimitDatasource);
                updateLimitTask.run();
                break;
        }
        log.debug("Finished migration in {} milliseconds", System.currentTimeMillis() - startTs);
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
