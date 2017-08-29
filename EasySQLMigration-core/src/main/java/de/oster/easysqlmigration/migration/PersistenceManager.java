package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.Connection;
import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

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
          throw new SQLConnectionException("error while creating the jdbc connection", exc.getCause());
        }

        jdbcTemplate = new JdbcTemplate(ds);

        try
        {
            jdbcTemplate.execute("SELECT 1");
        }
        catch (CannotGetJdbcConnectionException exc)
        {
            throw new SQLConnectionException("could not create jdbc connection \nerror message:\n" + exc.getCause().getMessage());
        }
    }

    public static JdbcTemplate get()
    {
        return jdbcTemplate;
    }
}
