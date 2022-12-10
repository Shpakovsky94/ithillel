
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class AutoCommitExample {

    // JDBC Driver Name & Database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String JDBC_DB_URL = "jdbc:mysql://localhost:3306/tutorialDb";

    // JDBC Database Credentials
    static final String JDBC_USER = "root";
    static final String JDBC_PASS = "admin@123";

    public static void main(String[] args) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connObj = DriverManager.getConnection(JDBC_DB_URL, JDBC_USER, JDBC_PASS);

            // Assuming A Valid Connection Object
            connObj.setAutoCommit(false);
            Statement stmtObj = connObj.createStatement();

            String correctQuery = "INSERT INTO employee VALUES (001, 20, 'Java', 'Geek')";
            stmtObj.executeUpdate(correctQuery);

            // Submitting A Malformed SQL Statement That Breaks
            String incorrectQuery = "INSERTED IN employee VALUES (002, 22, 'Harry', 'Potter')";
            stmtObj.executeUpdate(incorrectQuery);

            // If There Is No Error.
            connObj.commit();

            // If There Is Error
            connObj.rollback();
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        }
    }
}