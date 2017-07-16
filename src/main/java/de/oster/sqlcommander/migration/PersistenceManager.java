package de.oster.sqlcommander.migration;

import de.oster.sqlcommander.Connection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

import java.sql.Driver;
import java.sql.DriverManager;

/**
 * Created by Christian on 12.07.2017.
 */
class PersistenceManager
{
    private static JdbcTemplate jdbcTemplate;

    public static void initEntityManagerFactory(Connection connection) {

        DataSource ds = null;
        try
        {
            ds = new SimpleDriverDataSource(DriverManager.getDriver(connection.getJdbcURL()),
                    connection.getJdbcURL(),
                    connection.getUser(),
                    connection.getPassword());
        }
        catch (Exception exc)
        {
          exc.printStackTrace();
          System.exit(1);
        }

        jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("SELECT 1");
    }

    public static JdbcTemplate get()
    {
        return jdbcTemplate;
    }
}
