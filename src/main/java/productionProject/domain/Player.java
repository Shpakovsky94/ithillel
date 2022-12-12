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
public class Player {

    private Long    id;
    private String  createdTime;
    private String  nationalIdNumber;
    private Integer siteId;
    private Long    userId;
    private String  country;
    private String  currency;
    private String  version;
    private String  firstName;
    private String  middleName;
    private String  lastName;
    private String  birthDate;

    public static Player buildPlayerFromRs(final ResultSet rs) throws SQLException {

        Player playerFromRS = new Player();
        playerFromRS.setId(rs.getLong("ID"));
        playerFromRS.setCreatedTime(rs.getString("CREATED_TIME"));
        playerFromRS.setNationalIdNumber(rs.getString("NATIONAL_ID_NUMBER"));
        playerFromRS.setSiteId(rs.getInt("SITE_ID"));
        playerFromRS.setUserId(rs.getLong("USER_ID"));
        playerFromRS.setCountry(rs.getString("COUNTRY"));
        playerFromRS.setCurrency(rs.getString("CURRENCY"));
        playerFromRS.setVersion(rs.getString("VERSION"));
        playerFromRS.setFirstName(rs.getString("FIRST_NAME"));
        playerFromRS.setMiddleName(rs.getString("MIDDLE_NAME"));
        playerFromRS.setLastName(rs.getString("LAST_NAME"));
        playerFromRS.setBirthDate(rs.getString("BIRTH_DATE"));

        return playerFromRS;
    }
}
