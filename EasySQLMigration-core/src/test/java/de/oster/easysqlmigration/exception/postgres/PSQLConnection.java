package de.oster.easysqlmigration.exception.postgres;


import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.DriverManager;
import java.sql.SQLException;

public class PSQLConnection
{
    private JdbcTemplate jdbcTemplate;

    public static String jdbcURL = "jdbc:postgresql://localhost:5432/test";
    public static String user = "postgres";
    public static String password = "postgres";

    @Before
    public void connect()
    {
        try
        {
            SimpleDriverDataSource ds = new SimpleDriverDataSource(DriverManager.getDriver(jdbcURL),
                    jdbcURL,
                    user,
                    password);

            jdbcTemplate = new JdbcTemplate(ds);
            jdbcTemplate.execute("DROP SCHEMA public CASCADE;\n" +
                    "CREATE SCHEMA public;");
        }
        catch (CannotGetJdbcConnectionException exc)
        {
            //silent exception, because some test need to get behind here
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void disconnect()
    {
        try
        {
            jdbcTemplate.execute("DROP SCHEMA public CASCADE;\n" +
                    "CREATE SCHEMA public;");
        }
        catch (CannotGetJdbcConnectionException exc)
        {
            //silent exception, because some test need to get behind here
        }
    }
}
