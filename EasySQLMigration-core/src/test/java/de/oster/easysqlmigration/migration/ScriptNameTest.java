package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.migration.api.EasySQLMigration;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Christian on 08.11.2017.
 */
public class ScriptNameTest extends CustomTest
{
    @Test
    public void invalidFileNameTest()
    {
        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);
        Assert.assertNotNull(sqlMigration);
        sqlMigration.setFileNameVersionSeparator("__");
        sqlMigration.setSQLScripts("/invalidnamedsql");

        Exception exception = null;
        try
        {
            sqlMigration.migrate();
        }
        catch (SQLMigrationException exc)
        {
            exception = exc;
            exc.printStackTrace();
        }

        Assert.assertNotNull(exception);

    }

    @Test
    public void alpaNummericScriptVersionTest()
    {
        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);
        Assert.assertNotNull(sqlMigration);

        sqlMigration.setFileNameVersionSeparator("__");
        sqlMigration.setSQLScripts("/namedsql");

        sqlMigration.migrate();
    }
}
