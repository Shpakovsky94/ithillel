package productionProject.youngPlayerLimitUpdate.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@ToString
@EqualsAndHashCode
@Slf4j
public class User {

    private String  nationalIdNumber;
    private Integer siteId;
    private Long    userId;
    private String  country;
    private String  currency;
    private String  firstName;
    private String  middleName;
    private String  email;
    private String  lastName;
    private String  birthDate;
    private Long    titanPlayerId;
    private String  status;

    public static User buildUserFromRs(final ResultSet rs) throws SQLException {

        User userFromRS = new User();
        if (isThere(rs, "NATIONAL_ID_NUMBER")) {
            userFromRS.setNationalIdNumber(rs.getString("NATIONAL_ID_NUMBER"));
        }

        if (isThere(rs, "SITE_ID")) {
            userFromRS.setSiteId(rs.getInt("SITE_ID"));
        }

        if (isThere(rs, "PK_USER_ID")) {
            userFromRS.setUserId(rs.getLong("PK_USER_ID"));
        }

        if (isThere(rs, "COUNTRY")) {
            userFromRS.setCountry(rs.getString("COUNTRY"));
        }

        if (isThere(rs, "REGISTRATION_CURRENCY")) {
            userFromRS.setCurrency(rs.getString("REGISTRATION_CURRENCY"));
        }

        if (isThere(rs, "FIRST_NAME")) {
            userFromRS.setFirstName(rs.getString("FIRST_NAME"));
        }

        if (isThere(rs, "MIDDLE_NAME")) {
            userFromRS.setMiddleName(rs.getString("MIDDLE_NAME"));
        }

        if (isThere(rs, "LAST_NAME")) {
            userFromRS.setLastName(rs.getString("LAST_NAME"));
        }

        if (isThere(rs, "EMAIL")) {
            userFromRS.setEmail(rs.getString("EMAIL"));
        }

        if (isThere(rs, "BIRTH_DATE")) {
            userFromRS.setBirthDate(rs.getString("BIRTH_DATE"));
        }

        if (isThere(rs, "TITAN_PLAYER_ID")) {
            userFromRS.setTitanPlayerId(rs.getLong("TITAN_PLAYER_ID"));
        }

        if (isThere(rs, "STATUS")) {
            userFromRS.setStatus(rs.getString("STATUS"));
        }

        return userFromRS;
    }

    private static boolean isThere(
        ResultSet rs,
        String column
    ) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException ignore) {
        }
        return false;
    }
}