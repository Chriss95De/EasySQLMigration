package de.oster.sqlcommander.migration;

import de.oster.sqlcommander.migration.exception.SQLMigrationException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Christian on 12.07.2017.
 */
class Migration
{
    public String versionFileNameSeparator = "_";
    public String versionSeparator = "_";

    public String migrationTableName = "sql_migration";


    public void addMigrationEntry(SQLScriptObject sqlScriptObj) {

        //create the sqlmigration
        MigrationRepository.createMigrationTableIfNotExist(migrationTableName);

        MigrationObject migration = MigrationRepository.getMigrationByVersion(sqlScriptObj, migrationTableName);
        if(migration == null)
        {
            MigrationRepository.addNewMigration(sqlScriptObj, migrationTableName);
        }
    }

    public List<SQLScriptObject> searchSQLScripts(String[] paths, String[] prefixes, boolean recursive) throws IOException, SQLMigrationException {
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



        return sqlScriptObjects;
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
}
