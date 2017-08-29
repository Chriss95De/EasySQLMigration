package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.Connection;
import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

public class CustomTest
{
    public static String jdbcURL = "jdbc:h2:~/.sqlmigration/h2Test";
    public static String user = "";
    public static String password = "";

    @Before
    public void initManagerFactory() throws Exception {
        try
        {
            PersistenceManager.initEntityManagerFactory(new Connection(
                    jdbcURL,
                    user,
                    password));

            PersistenceManager.get().execute("DROP ALL OBJECTS");
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
            PersistenceManager.get().execute("DROP ALL OBJECTS");
        }
        catch (CannotGetJdbcConnectionException exc)
        {
            //silent exception, because some test need to get behind here
        }
    }
}
