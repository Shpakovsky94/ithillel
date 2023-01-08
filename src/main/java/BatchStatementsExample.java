import java.sql.Connection;
import java.sql.Statement;

public class BatchStatementsExample {

    //https://www.tutorialspoint.com/jdbc/jdbc-batch-processing.htm
    public static void main(String[] args) {
        Connection connObj = ConnectionHelper.getConnection();

        if (connObj == null) {
            return;
        }

        try {

            connObj.setAutoCommit(false);

            Statement stmtObj = connObj.createStatement();
            stmtObj.addBatch(
                    "INSERT INTO PERSON (FIRST_NAME, LAST_NAME, AGE, CITY) " +
                            "VALUES('Zak', 'Goroshko', 40, 'Kyiv')");
            stmtObj.addBatch("INSERT INTO PERSON (FIRST_NAME, LAST_NAME, AGE, CITY)" +
                    "VALUES('Bob', 'Zlato', 20, 'Kharkiv')");
            stmtObj.addBatch("UPDATE PERSON SET AGE = 15 WHERE PK_PERSON_ID = 4");

            // Execute Batch
            int[] recordsAffected = stmtObj.executeBatch();
            connObj.commit();
        } catch (Exception sqlException) {
            ConnectionHelper.rollbackConnection(connObj);

            sqlException.printStackTrace();
        } finally {
            ConnectionHelper.closeConnection(connObj);
        }

    }

    //production code example
    //https://www.baeldung.com/spring-jdbc-jdbctemplate

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
