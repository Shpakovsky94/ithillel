import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AutoCommitExample {
    // JDBC Driver Name & Database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    public static void main(String[] args) throws SQLException {
        Connection connObj = ConnectionHelper.getConnection();
        if (connObj == null){
            return;
        }

        try {
            Class.forName(JDBC_DRIVER);

            // Assuming A Valid Connection Object
            connObj.setAutoCommit(false);
            Statement stmtObj = connObj.createStatement();

            String correctQuery = "INSERT INTO PERSON (LAST_NAME)VALUES ('Geek')";
            stmtObj.executeUpdate(correctQuery);

            // Submitting A Malformed SQL Statement That Breaks
            String incorrectQuery = "INSERTED IN PERSON VALUES (002, 22, 'Harry', 'Potter')";
            stmtObj.executeUpdate(incorrectQuery);

            // If There Is No Error.
            connObj.commit();

        } catch (Exception sqlException) {
            // If There Is Error
            connObj.rollback();
            sqlException.printStackTrace();
        }
        finally {
            connObj.close();

        }
    }
}