package de.oster.easysqlmigration;

import de.oster.easysqlmigration.migration.SQLMigration;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;

/**
 * Created by Christian on 15.07.2017.
 */
public class EasySQLMigration extends SQLMigration
{
    public EasySQLMigration(String jdbcURL, String user, String password) {
        super(jdbcURL, user, password);
    }

    public EasySQLMigration(Connection connection) throws SQLMigrationException {
        super(connection);
    }
}
