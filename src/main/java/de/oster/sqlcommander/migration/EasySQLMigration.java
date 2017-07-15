package de.oster.sqlcommander.migration;

/**
 * Created by Christian on 15.07.2017.
 */
public class EasySQLMigration extends SQLMigrationImpl
{
    public EasySQLMigration(String jdbcDriver, String jdbcURL, String user, String password) throws Exception
    {
        super(jdbcDriver, jdbcURL, user, password);
    }
}
