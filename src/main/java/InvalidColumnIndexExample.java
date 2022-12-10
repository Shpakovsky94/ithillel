import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InvalidColumnIndexExample {

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

            PreparedStatement prepStmtObj = connObj.prepareStatement("SELECT DISTINCT item FROM order where order_id=?");
            prepStmtObj.setString(0, "101"); // This Will Throw "java.sql.SQLException: Invalid Column Index" Because "0" Is Not Valid Colum Index

            ResultSet resultSetObj = prepStmtObj.executeQuery();
            while(resultSetObj.next()) {
                System.out.println("Item: " + resultSetObj.getString(2)); // This Will Throw "java.sql.SQLException: Invalid column index" Because ResultSet Has Only One Column
            }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        }
    }
}
