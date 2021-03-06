package de.oster.easysqlmigration.migration.api;

import de.oster.easysqlmigration.Connection;
import de.oster.easysqlmigration.migration.EasySQLMigrationImpl;
import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by Christian on 12.07.2017.
 */
public class EasySQLMigration extends EasySQLMigrationImpl
{
    public static Logger log = Logger.getRootLogger();

    /**
     * @throws SQLConnectionException if there is something wrong with your jdbc connection
     */
    public static EasySQLMigration createInstance(String jdbcURL, String user, String password)
    {
       Connection connection = new Connection(jdbcURL, user, password);
       return new EasySQLMigration(connection);
    }
    /**
     * @throws SQLConnectionException if there is something wrong with your jdbc connection
     */
    public EasySQLMigration(Connection connection)
    {
        this.connection = connection;
        initAll();
    }

    /**
     * @throws SQLConnectionException if there is something wrong with your jdbc connection
     */
    public EasySQLMigration(String jdbcURL, String user, String password)
    {
        this.connection = new Connection(jdbcURL, user, password);
        initAll();
    }

    /**
     * Starts the migration by searching all sql script files that it can find
     * in the given paths.
     *
     * @throws SQLMigrationException jdbc problems, migration miss match or just
     * a syntax error in your sql script
     *
     * @throws java.io.IOException if the given path cant be accessed
     *
     * @throws SQLConnectionException if there is something wrong with your jdbc connection
     *
     */
    public void migrate()
    {
        this.doMigration();
    }

    //GET SET

    /**
     * Specifies the folders in your resource where the migration should look for your sql scripts
     * @param folders
     */
    public void setSQLScripts(String...folders)
    {
        this.urlPath = folders;
    }

    /**
     * Separates the version in the filename from the actual script name
     * For example: 1_Init.sql  <- separator = "_"
     *
     * DEFAULT: _
     *
     * @param separator
     */
    public void setFileNameVersionSeparator(String separator)
    {
        this.versionFileNameSeparator = separator;
    }

    public String getConnection()
    {
        return this.getConnection();
    }

    /**
     * @return the name of the created table name where all the migrations are stored in
     */
    public String getMigrationTableName() {
        return migrationTableName;
    }

    public void setMigrationTableName(String migrationTableName) {
        this.migrationTableName = migrationTableName;
        this.updateSchema();
    }

    public String getSchema() {
        return this.schema;
    }

    /**
     * Set the schema where your migration table should be created in
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
        this.updateSchema();
    }

    public String[] getPrefixes() {
        return prefixes;
    }

    /**
     * overrides the default prefix for the filename, default -> sql
     *
     * For example: 1_Test.sql <- prefix = "sql"
     *
     * @param prefixes for example
     */
    public void setPrefixes(String... prefixes) {
        this.prefixes = prefixes;
    }

    public List<Migration> retriveMigrationInfo() {
        return this.getRunnedMigrations();
    }
}
