import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class BatchStatementsExample {

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

            connObj.setAutoCommit(false);

            Statement stmtObj = connObj.createStatement();
            stmtObj.addBatch("INSERT INTO student VALUES(101, 'JavaGeek', 20)");
            stmtObj.addBatch("INSERT INTO student VALUES(102, 'Lucifer', 19)");
            stmtObj.addBatch("UPDATE employee SET age = 05 WHERE id = 001");

            // Execute Batch
            int[] recordsAffected = stmtObj.executeBatch();
            connObj.commit();
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        }
    }
}
