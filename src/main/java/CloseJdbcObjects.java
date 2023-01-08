
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
    }
}
