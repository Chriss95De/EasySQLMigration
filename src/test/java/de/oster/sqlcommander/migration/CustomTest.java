package de.oster.sqlcommander.migration;

import org.junit.After;
import org.junit.Before;

public class CustomTest
{
    public static String jdbcDriver = "org.h2.Driver";
    public static String jdbcURL = "jdbc:h2:~/.sqlmigration/h2Test";
    public static String user = "";
    public static String password = "";

    @Before
    public void initManagerFactory() throws Exception {
        PersistenceManager.initEntityManagerFactory(
                jdbcDriver,
                jdbcURL,
                "",
                "");

        PersistenceManager.get().execute("DROP ALL OBJECTS");
    }

    @After
    public void cleanUp()
    {
        PersistenceManager.get().execute("DROP ALL OBJECTS");
    }
}
