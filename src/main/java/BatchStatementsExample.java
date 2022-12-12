import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class BatchStatementsExample {

    // JDBC Driver Name & Database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String JDBC_DB_URL = "jdbc:mysql://localhost:3306/test";

    // JDBC Database Credentials
    static final String JDBC_USER = "root";
    static final String JDBC_PASS = "rootroot";


    //https://www.tutorialspoint.com/jdbc/jdbc-batch-processing.htm
    public static void main(String[] args) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connObj = DriverManager.getConnection(JDBC_DB_URL, JDBC_USER, JDBC_PASS);

            connObj.setAutoCommit(false);

            Statement stmtObj = connObj.createStatement();
            stmtObj.addBatch("INSERT INTO PERSON VALUES(3, 'Zak', 'Goroshko', 40, 'Kyiv')");
            stmtObj.addBatch("INSERT INTO PERSON VALUES(4, 'Bob', 'Zlato', 20, 'Kharkiv')");
            stmtObj.addBatch("UPDATE PERSON SET AGE = 15 WHERE id = 4");

            // Execute Batch
            int[] recordsAffected = stmtObj.executeBatch();
            connObj.commit();
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        }
    }

    //production code example
//    private void updateFilesContent(
//        String fileTableName,
//        List<FileDto> fileDtoList
//    ) {
//        Connection connection = null
//        try {
//            connection = titanJdbcTemplate.getDataSource().getConnection()
//            connection.setAutoCommit(false)
//            JdbcTemplate batchUpdateJdbcTemplate = new  JdbcTemplate(new SingleConnectionDataSource(connection, true))
//            def sql = "UPDATE " + fileTableName + " SET CONTENT = ? WHERE PK_FILE_ID = ?"
//
//            batchUpdateJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//                @Override void setValues(
//                    PreparedStatement ps,
//                    int i
//                ) throws SQLException {
//                    ps.setBlob(1, fileDtoList.get(i).getContent())
//                    ps.setLong(2, fileDtoList.get(i).getFileId())
//                }
//
//                @Override int getBatchSize() {
//                    return fileDtoList.size()
//                }
//            })
//
//            if (migrationProperties.getDryRun()) {
//                connection.rollback()
//            } else {
//                connection.commit()
//                connection.setAutoCommit(true)
//                processImportFileResult(fileDtoList, true)
//            }
//
//            addDocContentTrackData(fileDtoList, false, null)
//        } catch (Exception e) {
//            connection.rollback()
//            log.error("File updating failed, fileIds: {} , reason: {} ", e.getMessage(), e)
//            MigrationImportApplication.summaryData.put("totalDocContentImportFailed", MigrationImportApplication.summaryData.get("totalDocContentImportFailed").add(new BigDecimal(fileDtoList?.size())))
//            addDocContentTrackData(fileDtoList, true, e)
//            processImportFileResult(fileDtoList, false)
//            throw e
//        } finally {
//            if (connection != null) {
//                connection.close()
//            }
//        }
//    }
}
