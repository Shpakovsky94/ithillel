import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PreparedStmtExample {

    // JDBC Driver Name & Database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String JDBC_DB_URL = "jdbc:mysql://localhost:3306/test";

    // JDBC Database Credentials
    static final String JDBC_USER = "root";
    static final String JDBC_PASS = "rootroot";

    public static void main(String[] args) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connObj = DriverManager.getConnection(JDBC_DB_URL, JDBC_USER, JDBC_PASS);
            String city = "Lviv";
            PreparedStatement prepStatement = connObj.prepareStatement(
                    "SELECT DISTINCT LAST_NAME FROM PERSON WHERE CITY = ?");
            prepStatement.setString(1, city);

            ResultSet resObj = prepStatement.executeQuery();
            while (resObj.next()) {
                String lastName = resObj.getString("LAST_NAME");
                System.out.println("LAST_NAME: " + lastName);
            }
            connObj.close();
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        }
    }
}
