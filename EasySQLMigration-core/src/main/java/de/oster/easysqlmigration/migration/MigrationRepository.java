package de.oster.easysqlmigration.migration;


import java.sql.Timestamp;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

/**
 * Created by Christian on 13.07.2017.
 */
class MigrationRepository
{
    public static void createMigrationTableIfNotExist(String table)
    {
        PersistenceManager.get().execute("CREATE TABLE IF NOT EXISTS "+table+" ( " +
                "version varchar(255) NOT NULL, " +
                "name varchar(255) NOT NULL, " +
                "hash varchar(255) NOT NULL, " +
                "didRun BOOLEAN NOT NULL, " +
                "created TIMESTAMP NOT NULL " +
                ");");
    }

    public static void addNewMigration(SQLScriptObject sqlScriptObject, String table)
    {
        PersistenceManager.get().execute("INSERT INTO "+table+"(version, name, hash, didRun, created) " +
                "VALUES( " +
                "'"+sqlScriptObject.getVersion()    +"', " +
                "'"+sqlScriptObject.getName()       +"', " +
                "'"+sqlScriptObject.getHash()       +"', " +
                "'"+sqlScriptObject.didRun()       +"', " +
                "'"+new Timestamp(System.currentTimeMillis()) +"'" +
                ");");
    }

    public static void updateMigration(MigrationObject migration, String table)
    {
        PersistenceManager.get().update("UPDATE "+table +
                " SET " +
                "version = '"+migration.getVersion()    +"', " +
                "name = '"+migration.getName()       +"', " +
                "hash = '"+migration.getHash()       +"', " +
                "didRun = '"+migration.didRun()        +"', " +
                "created = '"+new Timestamp(System.currentTimeMillis())+"' " +
                " WHERE version = '"+migration.getVersion()+"';");
    }

    public static MigrationObject getMigrationByVersion(SQLScriptObject sqlScriptObject, String table)
    {
        try
        {
            MigrationObject migration = PersistenceManager.get().queryForObject(
                    "SELECT * FROM " + table + " WHERE version = '" + sqlScriptObject.getVersion() + "'",
                    new MigrationRowMapper());
            return migration;
        }
        catch (EmptyResultDataAccessException exc)
        {
            return null;
        }
    }

    public static List<MigrationObject> getAllMigrations(String table)
    {
        List<MigrationObject> migrationObjects = PersistenceManager.get().query("SELECT * FROM "+table, new RowMapperResultSetExtractor<MigrationObject>(new MigrationRowMapper()));
        return migrationObjects;
    }
}
