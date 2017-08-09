package de.oster.easysqlmigration;

import java.io.IOException;
import java.util.List;

import de.oster.easysqlmigration.migration.CustomTest;
import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLMigrationTest extends CustomTest
{
    @Test
    public void migrateSQLScript() throws SQLMigrationException, IOException {

        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);
        Assert.assertNotNull(sqlMigration);

        sqlMigration.setSQLScripts("/sql");

        sqlMigration.migrate();
    }

    @Test
    public void retrieveMetricInfo() throws SQLMigrationException {
        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);
        Assert.assertNotNull(sqlMigration);

        sqlMigration.setSQLScripts("/sql");

        sqlMigration.migrate();

        List<Migration> migrations = sqlMigration.retriveMigrationInfo();
        for (Migration migration : migrations)
        {
            System.out.println("Applied migrations: " + migration.getName());
            Assert.assertNotNull(migration.getCreated());
            Assert.assertNotNull(migration.getHash());
            Assert.assertNotNull(migration.getClass());
        }
    }
}
