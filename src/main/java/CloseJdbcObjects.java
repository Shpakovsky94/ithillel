
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CloseJdbcObjects {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection        connObj      = null;
        PreparedStatement prepStmtObj  = null;
        ResultSet         resultSetObj = null;
        try {
            // Business Logic!
        } finally {
            try {
                // Close Result Set Object
                if (resultSetObj != null) {
                    resultSetObj.close();
                }
                // Close Prepared Statement Object
                if (prepStmtObj != null) {
                    prepStmtObj.close();
                }
                // Close Connection Object
                if (connObj != null) {
                    connObj.close();
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }

        //How to use try-with-resources with JDBC?
        //https://www.tutorialspoint.com/how-to-use-try-with-resources-with-jdbc

        //Getting the connection
        String mysqlUrl = "jdbc:mysql://localhost/mydatabase";
        System.out.println("Connection established......");
        //Registering the Driver
        try (Connection con = DriverManager.getConnection(mysqlUrl, "root", "password");
            Statement stmt = con.createStatement();) {
            try (ResultSet rs = stmt.executeQuery("select * from MyPlayers");) {
                //Retrieving the data
                while (rs.next()) {
                    System.out.print(rs.getInt("ID") + ", ");
                    System.out.print(rs.getString("First_Name") + ", ");
                    System.out.print(rs.getString("Last_Name") + ", ");
                    System.out.print(rs.getDate("Date_Of_Birth") + ", ");
                    System.out.print(rs.getString("Place_Of_Birth") + ", ");
                    System.out.print(rs.getString("Country"));
                    System.out.println();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class ConnectionHelper {
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
}
