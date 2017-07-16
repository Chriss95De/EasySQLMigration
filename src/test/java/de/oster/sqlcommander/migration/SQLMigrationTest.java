package de.oster.sqlcommander.migration;

import de.oster.sqlcommander.EasySQLMigration;
import de.oster.sqlcommander.migration.exception.SQLMigrationException;
import org.flywaydb.core.Flyway;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLMigrationTest extends CustomTest
{
    @Test
    public void migrateSQLScript() throws SQLMigrationException {

        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcDriver, CustomTest.jdbcURL, CustomTest.user, CustomTest.password);
        Assert.assertNotNull(sqlMigration);

        sqlMigration.setSQLScripts("./sql", "./sql2");
        sqlMigration.setMigrationTableName("custom_migration");
        sqlMigration.setPrefixes("sql");
        sqlMigration.setSeparator("_");

        sqlMigration.migrate();
        sqlMigration.migrate();

    }
}
