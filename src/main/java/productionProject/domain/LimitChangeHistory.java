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
public class LimitChangeHistory {

    private Long       id;
    private String     createdTime;
    private Long       limitId;
    private Long       playerId;
    private Integer    siteId;
    private Long       userId;
    private String     action;
    private BigDecimal amountBefore;
    private BigDecimal amountAfter;
    private Long       boUserId;
    private Boolean    highLimitAccepted;
    private String     clientIp;
    private String     clientCountry;
    private String     clientUserAgent;
    private String     clientDeviceType;
    private String     version;

    public static LimitChangeHistory buildLimitChangeHistoryFromRs(final ResultSet rs) throws SQLException {

        LimitChangeHistory limitChangeHistoryFromRs = new LimitChangeHistory();
        limitChangeHistoryFromRs.setId(rs.getLong("ID"));
        limitChangeHistoryFromRs.setCreatedTime(rs.getString("CREATED_TIME"));
        limitChangeHistoryFromRs.setLimitId(rs.getLong("LIMIT_ID"));
        limitChangeHistoryFromRs.setPlayerId(rs.getLong("PLAYER_ID"));
        limitChangeHistoryFromRs.setSiteId(rs.getInt("SITE_ID"));
        limitChangeHistoryFromRs.setUserId(rs.getLong("USER_ID"));
        limitChangeHistoryFromRs.setAction(rs.getString("ACTION"));
        limitChangeHistoryFromRs.setAmountBefore(rs.getBigDecimal("AMOUNT_BEFORE"));
        limitChangeHistoryFromRs.setAmountAfter(rs.getBigDecimal("AMOUNT_AFTER"));
        limitChangeHistoryFromRs.setBoUserId(rs.getLong("BO_USER_ID"));
        limitChangeHistoryFromRs.setHighLimitAccepted(rs.getBoolean("HIGH_LIMIT_ACCEPTED"));
        limitChangeHistoryFromRs.setClientIp(rs.getString("CLIENT_IP"));
        limitChangeHistoryFromRs.setClientCountry(rs.getString("CLIENT_COUNTRY"));
        limitChangeHistoryFromRs.setClientUserAgent(rs.getString("CLIENT_USER_AGENT"));
        limitChangeHistoryFromRs.setClientDeviceType(rs.getString("CLIENT_DEVICE_TYPE"));
        limitChangeHistoryFromRs.setVersion(rs.getString("VERSION"));

        return limitChangeHistoryFromRs;
    }

}
