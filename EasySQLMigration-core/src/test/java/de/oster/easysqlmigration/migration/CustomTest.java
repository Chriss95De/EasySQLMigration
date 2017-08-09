package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.Connection;
import org.junit.After;
import org.junit.Before;

public class CustomTest
{
    public static String jdbcURL = "jdbc:h2:~/.sqlmigration/h2Test";
    public static String user = "";
    public static String password = "";

    @Before
    public void initManagerFactory() throws Exception {
        PersistenceManager.initEntityManagerFactory(new Connection(
                jdbcURL,
                user,
                password));

        PersistenceManager.get().execute("DROP ALL OBJECTS");
    }

    @After
    public void cleanUp()
    {
        PersistenceManager.get().execute("DROP ALL OBJECTS");
    }
}
