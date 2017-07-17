package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.Connection;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.BadSqlGrammarException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Christian on 12.07.2017.
 */
class Migration
{
    protected static Logger log = Logger.getRootLogger();

    //JDBC Information
    Connection connection;

    protected String[] urlPath;
    protected String[] prefixes = {"sql"};

    protected String versionFileNameSeparator = "_";
    protected String versionSeparator = "_";

    private String internalVersionSeparator = ".";

    protected String migrationTableName = "sql_migration";
    protected String schema = "public";

    protected String schemaWithTabel = schema+"."+migrationTableName;


    protected void addMigrationEntry(SQLScriptObject sqlScriptObj) {

        //create the sqlmigration
        MigrationRepository.createMigrationTableIfNotExist(schemaWithTabel);

        MigrationObject migration = MigrationRepository.getMigrationByVersion(sqlScriptObj, schemaWithTabel);
        if(migration == null)
        {
            MigrationRepository.addNewMigration(sqlScriptObj, schemaWithTabel);
        }
    }

    protected List<SQLScriptObject> searchSQLScripts(String[] paths, String[] prefixes, boolean recursive) throws IOException, SQLMigrationException {
        List<File> files = new ArrayList<File>();

        if(paths == null)
            return new ArrayList<SQLScriptObject>();

        for(String path : paths)
        {
            files.addAll(FileUtils.listFiles(new File(path), prefixes, recursive));
        }

        List<SQLScriptObject> sqlScriptObjectsTemp = new ArrayList<SQLScriptObject>();
        for(File file : files)
            sqlScriptObjectsTemp.add(SQLScriptObject.createFromFile(versionFileNameSeparator, file));

        runSyntaxCheck(sqlScriptObjectsTemp);

        //creat a by version sorted list
        final List<SQLScriptObject> sqlScriptObjects = new ArrayList<SQLScriptObject>()
        {
            @Override
            public boolean add(SQLScriptObject sqlScriptObject) {
                super.add(sqlScriptObject);
                Collections.sort(this, sqlScriptObjectComparator);
                return true;
            }

            @Override
            public boolean addAll(Collection<? extends SQLScriptObject> c)
            {
                for(SQLScriptObject s : c )
                    this.add(s);
                return true;
            }
        };

        sqlScriptObjects.addAll(sqlScriptObjectsTemp);
        convertVersionSeparator(sqlScriptObjectsTemp);

        return sqlScriptObjects;
    }

    protected void doMigration() throws SQLMigrationException {
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

        //cache all migrations to run checks on them
        MigrationRepository.createMigrationTableIfNotExist(schemaWithTabel);
        List<MigrationObject> allMigrations = MigrationRepository.getAllMigrations(schemaWithTabel);

        for(SQLScriptObject sqlScriptObj : sqlScriptObjects)
        {
            log.info("");
            log.info("started sqlmigration " + sqlScriptObj.getName());

            MigrationObject checkMigration = MigrationRepository.getMigrationByVersion(sqlScriptObj, schemaWithTabel);
            if(checkMigration == null)
            {
                MigrationActions.runChecksOnMigration(allMigrations, sqlScriptObj, internalVersionSeparator);
            }

            //Add if missing
            this.addMigrationEntry(sqlScriptObj);

            //check hash
            MigrationObject migration = MigrationRepository.getMigrationByVersion(sqlScriptObj, schemaWithTabel);
            if(!sqlScriptObj.getHash().equals(migration.getHash()))
                throw new SQLMigrationException(
                        "\nfailed to apply migration: "+sqlScriptObj.getName()+ "\n " +
                                "missmatch between allready applied version and current migration \n" +
                                " applied migration -> (hash:"+ migration.getHash() +") \n" +
                                " current migration -> (hash:"+ sqlScriptObj.getHash()+")");

            if(migration.didRun() == false)
            {
                try
                {
                    PersistenceManager.get().execute(sqlScriptObj.getSqlScript());
                }
                catch (BadSqlGrammarException exc)
                {
                    throw new SQLMigrationException("\n"+exc.getSQLException().getMessage());
                }

                migration.setDidRun(true);
                MigrationRepository.updateMigration(migration, schemaWithTabel);
                log.info("applied migration");
            }
            else
            {
                log.info("nothing to migrate");
            }

            log.info("ended sqlmigration: " + sqlScriptObj.getName());
        }
        log.info("");
    }

    private void runSyntaxCheck(List<SQLScriptObject> sqlScriptObjects) throws SQLMigrationException
    {
        for (SQLScriptObject sqlScriptObject : sqlScriptObjects)
        {
            String[] numbers = sqlScriptObject.getVersion().split(versionSeparator);
            try
            {
                //First check: should all be numbers
                for (String number : numbers) {
                    Integer.parseInt(number);
                }
            }
            catch (NumberFormatException exc)
            {
                throw new SQLMigrationException("\nbad syntax in your sqlmigration filename: " + sqlScriptObject.getVersion()+versionFileNameSeparator+sqlScriptObject.getName() + "\n"+
                "can not parse the version information");
            }
        }
    }

    private void convertVersionSeparator(List<SQLScriptObject> sqlScriptObjects) throws SQLMigrationException
    {
        for (SQLScriptObject sqlScriptObject : sqlScriptObjects)
        {
            sqlScriptObject.setVersion(sqlScriptObject.getVersion().replace(versionSeparator, internalVersionSeparator));
        }
    }

    private Comparator<SQLScriptObject> sqlScriptObjectComparator =  new Comparator<SQLScriptObject>()
    {
        public int compare(SQLScriptObject o1, SQLScriptObject o2)
        {
            return MigrationActions.determineOrder(o1.getVersion(), o2.getVersion(), versionSeparator);
        }
    };



    protected void updateSchema()
    {
        schemaWithTabel = schema+"."+migrationTableName;
    }
}
