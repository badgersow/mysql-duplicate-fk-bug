
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DuplicateFKTest {

    private Connection connection;

    @Before
    public void setup() throws Exception {
        connection = getMySqlConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    /**
     * Please setup the running DB with necessary tables
     */
    @Test
    public void testForeignKeyIsNotDuplicated() throws Exception {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getImportedKeys(connection.getCatalog(), null, "AO_9412A1_USER_APP_LINK");
        int numberOfFk = 0;
        while (rs.next()) {
            String fkTableName = rs.getString("FKTABLE_NAME");
            String fkColumnName = rs.getString("FKCOLUMN_NAME");
            int fkSequence = rs.getInt("KEY_SEQ");
            numberOfFk++;

            // Additional logging
            System.out.println("getExportedKeys(): index=" + numberOfFk);
            System.out.println("getExportedKeys(): fkTableName=" + fkTableName);
            System.out.println("getExportedKeys(): fkColumnName=" + fkColumnName);
            System.out.println("getExportedKeys(): fkSequence=" + fkSequence);
            System.out.println();
        }
        Assume.assumeTrue("There should be at least one FK. " +
                "If this fails, please make sure you set up the DB and tables correctly. See README.md for more details.",
                numberOfFk >= 1);
        Assert.assertEquals("There should be exactly one FK", numberOfFk, 1);
    }

    public static Connection getMySqlConnection() throws Exception {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost/dbdemo";
        String username = "root";
        String password = "Mysql123!";

        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

}
