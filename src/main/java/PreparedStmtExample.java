import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PreparedStmtExample {

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

            PreparedStatement prepStatement = connObj.prepareStatement(
                    "SELECT DISTINCT loan_type FROM bank_loans WHERE bank_name=?");
            prepStatement.setString(1, "Citibank");

            ResultSet resObj = prepStatement.executeQuery();
            while (resObj.next()) {
                System.out.println("Loan Type?= " + resObj.getString("loan_type"));
            }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        }
    }
}
