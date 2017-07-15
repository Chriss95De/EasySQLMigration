package de.oster.sqlcommander.migration;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

import java.sql.Driver;

/**
 * Created by Christian on 12.07.2017.
 */
class PersistenceManager
{
    private static JdbcTemplate jdbcTemplate;

    public static void initEntityManagerFactory(String jdbcDriver, String jdbcURL, String user, String password) throws Exception {

        DataSource ds = null;
        try
        {
            ds = new SimpleDriverDataSource((Driver)Class.forName(jdbcDriver).newInstance(),jdbcURL, user, password);
        }
        catch (Exception e)
        {
            throw e;
        }
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("SELECT 1");
    }

    public static JdbcTemplate get()
    {
        return jdbcTemplate;
    }
}
