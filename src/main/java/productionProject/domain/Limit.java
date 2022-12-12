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
public class Limit {

    private Long       id;
    private Long       playerId;
    private String     limitType;
    private String     periodType;
    private String     setTime;
    private String     currentPeriodStartTime;
    private String     currentPeriodEndTime;
    private Boolean    renew;
    private BigDecimal amount;
    private BigDecimal usedAmount;
    private String     effectiveTime;
    private BigDecimal newAmount;
    private String     version;
    private int        siteId;
    private Long       userId;

    public static Limit buildLimitFromRs(final ResultSet rs) throws SQLException {
        Limit limitFromRS = new Limit();
        limitFromRS.setId(rs.getLong("ID"));
        limitFromRS.setPlayerId(rs.getLong("PLAYER_ID"));
        limitFromRS.setLimitType(rs.getString("LIMIT_TYPE"));
        limitFromRS.setPeriodType(rs.getString("PERIOD_TYPE"));
        limitFromRS.setSetTime(rs.getString("SET_TIME"));
        limitFromRS.setCurrentPeriodStartTime(rs.getString("CURRENT_PERIOD_START_TIME"));
        limitFromRS.setCurrentPeriodEndTime(rs.getString("CURRENT_PERIOD_END_TIME"));
        limitFromRS.setRenew(rs.getBoolean("RENEW"));
        limitFromRS.setAmount(rs.getBigDecimal("AMOUNT"));
        limitFromRS.setUsedAmount(rs.getBigDecimal("USED_AMOUNT"));
        limitFromRS.setEffectiveTime(rs.getString("EFFECTIVE_TIME"));
        limitFromRS.setNewAmount(rs.getBigDecimal("NEW_AMOUNT"));
        limitFromRS.setVersion(rs.getString("VERSION"));
        limitFromRS.setSiteId(rs.getInt("SITE_ID"));
        limitFromRS.setUserId(rs.getLong("USER_ID"));

        return limitFromRS;
    }
}
