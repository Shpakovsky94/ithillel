package productionProject.domain;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@ToString
@EqualsAndHashCode
public class LimitUsageHistory {

    private Long       id;
    private String     createdTime;
    private Long       limitUsageId;
    private Long       limitId;
    private String     direction;
    private BigDecimal amount;
    private BigDecimal usedAmountBefore;
    private BigDecimal usedAmountAfter;
    private Boolean    limitReached;
    private Boolean    rollback;
    private String     clientIp;
    private String     clientCountry;
    private String     clientUserAgent;
    private String     clientDeviceType;
    private String     version;

    public static LimitUsageHistory buildLimitUsageHistoryFromRs(final ResultSet rs) throws SQLException {

        LimitUsageHistory limitUsageHistoryFromRS = new LimitUsageHistory();
        limitUsageHistoryFromRS.setId(rs.getLong("ID"));
        limitUsageHistoryFromRS.setCreatedTime(rs.getString("CREATE_TIME"));
        limitUsageHistoryFromRS.setLimitUsageId(rs.getLong("LIMIT_USAGE_ID"));
        limitUsageHistoryFromRS.setLimitId(rs.getLong("LIMIT_ID"));
        limitUsageHistoryFromRS.setDirection(rs.getString("DIRECTION"));
        limitUsageHistoryFromRS.setAmount(rs.getBigDecimal("AMOUNT"));
        limitUsageHistoryFromRS.setUsedAmountBefore(rs.getBigDecimal("USED_AMOUNT_BEFORE"));
        limitUsageHistoryFromRS.setUsedAmountAfter(rs.getBigDecimal("USED_AMOUNT_AFTER"));
        limitUsageHistoryFromRS.setLimitReached(rs.getBoolean("LIMIT_REACHED"));
        limitUsageHistoryFromRS.setClientIp(rs.getString("CLIENT_IP"));
        limitUsageHistoryFromRS.setClientCountry(rs.getString("CLIENT_COUNTRY"));
        limitUsageHistoryFromRS.setClientUserAgent(rs.getString("CLIENT_USER_AGENT"));
        limitUsageHistoryFromRS.setClientDeviceType(rs.getString("CLIENT_DEVICE_TYPE"));
        limitUsageHistoryFromRS.setVersion(rs.getString("VERSION"));

        return limitUsageHistoryFromRS;
    }
}
