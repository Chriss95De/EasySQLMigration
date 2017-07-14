package de.oster.sqlcommander.persistence;

import de.oster.sqlcommander.jdbc.PersistenceManager;
import de.oster.sqlcommander.migration.SQLScriptObject;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.Timestamp;

/**
 * Created by Christian on 13.07.2017.
 */
public class MigrationRepository
{
    public static void createMigrationTableIfNotExist(String migrationTable)
    {
        PersistenceManager.get().execute("CREATE TABLE IF NOT EXISTS "+migrationTable+" ( " +
                "version varchar(255) NOT NULL, " +
                "name varchar(255) NOT NULL, " +
                "hash varchar(255) NOT NULL, " +
                "didRun BOOLEAN NOT NULL, " +
                "created TIMESTAMP NOT NULL " +
                ");");
    }

    public static void addNewMigration(SQLScriptObject sqlScriptObject, String migrationTable)
    {
        PersistenceManager.get().execute("INSERT INTO "+migrationTable+"(version, name, hash, didRun, created) " +
                "VALUES( " +
                "'"+sqlScriptObject.getVersion()    +"', " +
                "'"+sqlScriptObject.getName()       +"', " +
                "'"+sqlScriptObject.getHash()       +"', " +
                "'"+sqlScriptObject.didRun()       +"', " +
                "'"+new Timestamp(System.currentTimeMillis()) +"', " +
                ");");
    }

    public static void updateMigration(Migration migration, String migrationTable)
    {
        PersistenceManager.get().update("UPDATE "+migrationTable +
                " SET " +
                "version = '"+migration.getVersion()    +"', " +
                "name = '"+migration.getName()       +"', " +
                "hash = '"+migration.getHash()       +"', " +
                "didRun = '"+migration.didRun()        +"', " +
                "created = '"+new Timestamp(System.currentTimeMillis())+"' " +
                " WHERE version = '"+migration.getVersion()+"';");
    }

    public static Migration getMigrationByVersion(SQLScriptObject sqlScriptObject, String migrationTable)
    {
        try
        {
            Migration migration = PersistenceManager.get().queryForObject(
                    "SELECT * FROM " + migrationTable + " WHERE version = '" + sqlScriptObject.getVersion() + "'",
                    new MigrationRowMapper());
            return migration;
        }
        catch (EmptyResultDataAccessException exc)
        {
            return null;
        }
    }
}
