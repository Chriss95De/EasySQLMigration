package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.Connection;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;

import java.util.List;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLMigration extends MigrationImpl implements SQLMigrationAPI
{
    public SQLMigration(String jdbcURL, String user, String password)
    {
        this.connection = new Connection(jdbcURL, user, password);
        PersistenceManager.initEntityManagerFactory(connection);
    }

    public SQLMigration(Connection connection) throws SQLMigrationException
    {
        this.connection = connection;
        PersistenceManager.initEntityManagerFactory(connection);
    }

    /***
     * Starts the migration by searching all sql script files that it can find
     * in the given paths.
     *
     * @throws SQLMigrationException jdbc problems, migration miss match or just
     * a syntax error in your sql script
     *
     * @throws java.io.IOException if the given path cant be accessed
     *
     */
    public void migrate()
    {
        log.info("---started migration---");
        this.doMigration();
        log.info("---ended migration---");
    }

    //GET SET
    public void setSQLScripts(String...folders)
    {
        this.urlPath = folders;
    }

    public void setSeparator(String separator)
    {
        this.versionFileNameSeparator = separator;
    }

    public String getSeparator()
    {
        return this.versionFileNameSeparator;
    }

    public String getConnection() {
        return this.getConnection();
    }

    public String[] getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String[] urlPath) {
        this.urlPath = urlPath;
    }

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

    public void setSchema(String schema) {
        this.schema = schema;
        this.updateSchema();
    }

    public String[] getPrefixes() {
        return prefixes;
    }

    /**
     * overrides the default prefix for sql scripts, default -> sql
     * @param prefixes for example
     */
    public void setPrefixes(String... prefixes) {
        this.prefixes = prefixes;
    }

    @Override
    public List<Migration> retriveMigrationInfo() {
        return this.getRunnedMigrations();
    }
}
