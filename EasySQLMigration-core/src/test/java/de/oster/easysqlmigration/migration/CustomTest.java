package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.Connection;
import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.DriverManager;

public class CustomTest
{
    private JdbcTemplate jdbcTemplate;

    public static String jdbcURL = "jdbc:h2:~/.sqlmigration/h2Test";
    public static String user = "";
    public static String password = "";

    @Before
    public void initManagerFactory() throws Exception {
        try
        {
             SimpleDriverDataSource ds = new SimpleDriverDataSource(DriverManager.getDriver(jdbcURL),
                     jdbcURL,
                     user,
                     password);

            jdbcTemplate = new JdbcTemplate(ds);
            jdbcTemplate.execute("DROP ALL OBJECTS");
        }
        catch (CannotGetJdbcConnectionException exc)
        {
           //silent exception, because some test need to get behind here
        }
    }

    @After
    public void cleanUp()
    {
        try
        {
            jdbcTemplate.execute("DROP ALL OBJECTS");
        }
        catch (CannotGetJdbcConnectionException exc)
        {
            //silent exception, because some test need to get behind here
        }
    }
}
