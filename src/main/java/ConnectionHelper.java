import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    // JDBC Driver Name & Database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String JDBC_DB_URL = "jdbc:mysql://localhost:3306/test";

    // JDBC Database Credentials
    static final String JDBC_USER = "root";
    static final String JDBC_PASS = "rootroot";

    public static Connection getConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            return DriverManager.getConnection(JDBC_DB_URL, JDBC_USER, JDBC_PASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void rollbackConnection(Connection connection){
        try {
            connection.rollback();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void closeConnection(Connection connection){
        try {
            connection.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}