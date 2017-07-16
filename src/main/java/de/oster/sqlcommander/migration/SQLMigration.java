package de.oster.sqlcommander.migration;

import de.oster.sqlcommander.Connection;
import de.oster.sqlcommander.migration.exception.SQLMigrationException;
import org.apache.log4j.Logger;
import org.springframework.jdbc.BadSqlGrammarException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLMigration extends Migration implements SQLMigrationAPI
{
    public SQLMigration(String jdbcDriver, String jdbcURL, String user, String password)
    {
        this.connection = new Connection(jdbcDriver, jdbcURL, user, password);
        PersistenceManager.initEntityManagerFactory(connection);
    }

    public SQLMigration(Connection connection) throws SQLMigrationException
    {
        this.connection = connection;
        PersistenceManager.initEntityManagerFactory(connection);
    }

    public void migrate() throws SQLMigrationException
    {
        this.doMigration();
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
}
