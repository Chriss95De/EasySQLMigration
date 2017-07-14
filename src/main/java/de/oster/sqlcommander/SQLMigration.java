package de.oster.sqlcommander;

import de.oster.sqlcommander.jdbc.PersistenceManager;
import de.oster.sqlcommander.jdbc.exception.SQLMigrationException;
import de.oster.sqlcommander.migration.MigrationImpl;
import de.oster.sqlcommander.migration.SQLScriptObject;
import de.oster.sqlcommander.persistence.Migration;
import de.oster.sqlcommander.persistence.MigrationRepository;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.io.IOException;
import java.util.List;

/**
 * Created by Christian on 12.07.2017.
 */
public final class SQLMigration extends MigrationImpl implements EasySQLMigration
{
    private static Logger log = Logger.getRootLogger();

    //JDBC Information
    private String jdbcDriver;
    private String jdbcURL;
    private String user;
    private String password;

    private String[] urlPath;
    private String[] prefixes = {"sql"};

    public SQLMigration(String jdbcDriver, String jdbcURL, String user, String password) throws Exception
    {
        PersistenceManager.initEntityManagerFactory(jdbcDriver, jdbcURL, user, password);
    }

    public void migrate() throws SQLMigrationException
    {
        List<SQLScriptObject> sqlScriptObjects = null;
        try
        {
            sqlScriptObjects = this.searchSQLScripts(urlPath, prefixes, false);
        }
        catch (IOException e)
        {
            throw new SQLMigrationException(e.getMessage(), e.getCause());
        }

        if(sqlScriptObjects == null)
            return;

        for(SQLScriptObject sqlScriptObj : sqlScriptObjects)
        {
            String completeScriptName = sqlScriptObj.getVersion() + versionFileNameSeparator + sqlScriptObj.getName();
            log.info("");
            log.info("started migration " + completeScriptName);

            //Add if missing
            this.addMigrationEntry(sqlScriptObj);

            //check hash
            Migration migration = MigrationRepository.getMigrationByVersion(sqlScriptObj, migrationTableName);
            if(!sqlScriptObj.getHash().equals(migration.getHash()))
                throw new SQLMigrationException(
                        "\nfailed to apply migration: "+completeScriptName+ "\n " +
                        "missmatch between allready applied version and current migration \n" +
                        " applied migration -> (hash:"+ migration.getHash() +") \n" +
                        " current migration -> (hash:"+ sqlScriptObj.getHash()+")");

            if(migration.didRun() == false)
            {

                PersistenceManager.get().execute(sqlScriptObj.getSqlScript());

                migration.setDidRun(true);
                MigrationRepository.updateMigration(migration, migrationTableName);
                log.info("applied migration");
            }
            else
            {
                log.info("nothing to migrate");
            }

            log.info("ended migration: " + completeScriptName);
        }
        log.info("");
    }

    //GET SET
    public void setSQLScripts(String...folders)
    {
        urlPath = folders;
    }

    public void setSeparator(String separator)
    {
        this.versionFileNameSeparator = separator;
    }

    public String getSeparator()
    {
        return this.versionFileNameSeparator;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String[] getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(String... prefixes) {
        this.prefixes = prefixes;
    }
}
