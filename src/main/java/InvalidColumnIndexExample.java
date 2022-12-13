import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InvalidColumnIndexExample {

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

            PreparedStatement prepStmtObj = connObj.prepareStatement("SELECT DISTINCT LAST_NAME FROM PERSON where PK_PERSON_ID=?");
            prepStmtObj.setString(0, "1"); // This Will Throw "java.sql.SQLException: Invalid Column Index" Because "0" Is Not Valid Column Index

            ResultSet resultSetObj = prepStmtObj.executeQuery();
            while (resultSetObj.next()) {
                System.out.println("LAST_NAME: " + resultSetObj.getString(2)); // This Will Throw "java.sql.SQLException: Invalid column index" Because ResultSet Has Only One Column
            }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        }
    }
}
