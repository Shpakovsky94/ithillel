package sampleProject.service;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceHelperImpl {

    protected int countResultSetSize(final ResultSet rs) throws SQLException {
        int size = 0;
        if (rs != null) {
            rs.last();    // moves cursor to the last row
            size = rs.getRow(); // get row id
            rs.first();   // moves cursor back to the start
        }
        return size;
    }
}
