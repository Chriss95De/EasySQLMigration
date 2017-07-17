package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by Christian on 16.07.2017.
 */
public class ScriptSearchTest
{
    @Test
    public void searchSQLScriptTest() throws IOException, SQLMigrationException {
        Migration migration = new Migration();
        List<SQLScriptObject> sqlMigrationList = migration.searchSQLScripts(new String[]{"../sql/"}, null, false);

        Assert.assertNotNull(sqlMigrationList);
        Assert.assertNotEquals(sqlMigrationList.size(), 0);
    }
}
