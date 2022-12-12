package productionProject.domain;

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
public class LimitUsage {

    private static final long    serialVersionUID = 1L;
    private              Long    id;
    private              String  createdTime;
    private              Long    playerId;
    private              Integer siteId;
    private              Long    userId;
    private              String  version;

    public static LimitUsage buildLimitUsageFromRs(final ResultSet rs) throws SQLException {

        LimitUsage limitUsageFromRS = new LimitUsage();
        limitUsageFromRS.setId(rs.getLong("ID"));
        limitUsageFromRS.setPlayerId(rs.getLong("PLAYER_ID"));
        limitUsageFromRS.setCreatedTime(rs.getString("CREATED_TIME"));
        limitUsageFromRS.setSiteId(rs.getInt("SITE_ID"));
        limitUsageFromRS.setUserId(rs.getLong("USER_ID"));
        limitUsageFromRS.setVersion(rs.getString("VERSION"));

        return limitUsageFromRS;
    }
}
