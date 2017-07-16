package de.oster.sqlcommander.migration;

import de.oster.sqlcommander.Connection;
import de.oster.sqlcommander.migration.exception.SQLMigrationException;
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
    private static Logger log = Logger.getRootLogger();

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
            String completeScriptName = sqlScriptObj.getVersion().replace(internalVersionSeparator, versionSeparator) + versionFileNameSeparator + sqlScriptObj.getName();
            log.info("");
            log.info("started sqlmigration " + completeScriptName);

            //Add if missing
            this.addMigrationEntry(sqlScriptObj);

            //check hash
            MigrationObject migration = MigrationRepository.getMigrationByVersion(sqlScriptObj, schemaWithTabel);
            if(!sqlScriptObj.getHash().equals(migration.getHash()))
                throw new SQLMigrationException(
                        "\nfailed to apply migration: "+completeScriptName+ "\n " +
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

            log.info("ended sqlmigration: " + completeScriptName);
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
            return determineOrder(o1.getVersion(), o2.getVersion());
        }

        /***
         * smallest value is the lowest
         * @return order value , the lower the earlier in the order
         */
        private int determineOrder(String version, String version2) {
            String numbers[] = version.split(versionSeparator);
            String numbers2[] = version2.split(versionSeparator);

            for (int i = 0; i < numbers.length; i++) {
                if (numbers2.length < i)
                    return -1; //version 1 wins


                int value1 = Integer.parseInt(numbers[i]);
                int value2 = Integer.parseInt(numbers2[i]);

                if (value1 < value2)
                    return -1;
                else if (value1 > value2)
                    return 1;

                if (numbers.length < i + 1)
                    return 1;
            }

            return 0;
        }
    };

    protected void updateSchema()
    {
        schemaWithTabel = schema+"."+migrationTableName;
    }
}
