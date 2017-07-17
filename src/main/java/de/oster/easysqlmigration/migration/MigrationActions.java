package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.migration.exception.SQLMigrationException;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Christian on 16.07.2017.
 */
class MigrationActions
{
    public static void runChecksOnMigration(List<MigrationObject> allMigrations, SQLScriptObject toCheck, String versionSeparator) throws SQLMigrationException
    {
        if(allMigrations == null)
            return;
        if(allMigrations.size() == 0)
            return;

        String highestVersion = determineHighestVersion(allMigrations, versionSeparator);

        //should be higher then the version
        if(determineOrder(highestVersion, toCheck.getVersion(), versionSeparator) == 1)
        {
            throw new SQLMigrationException("\n" +
                    "tried to apply new version that is lower then the current applied migration version \n" +
                    "current applied version: " + highestVersion + "\n" +
                    "tried to apply version: " + toCheck.getVersion() + "\n" +
                    "problem migration -> " + toCheck.getName());
        }
    }

    protected static String determineHighestVersion(List<MigrationObject> allMigrations, String versionSeparator)
    {
        String highestVersion = allMigrations.get(0).getVersion();
        for(MigrationObject migrationObj : allMigrations)
        {
            if(determineOrder(highestVersion, migrationObj.getVersion(), versionSeparator) == -1)
                highestVersion = migrationObj.getVersion();
        }

       return highestVersion;
    }

    /***
     * smallest value is the lowest
     * @return order value , the lower the earlier in the order
     */
    protected static int determineOrder(String version, String version2, String versionSeparator) {

        if(version.equals(version2))
            return 0;

        String numbers[] = version.split(Pattern.quote(versionSeparator));
        String numbers2[] = version2.split(Pattern.quote(versionSeparator));

        for (int i = 0; i < numbers.length; i++) {

            int value1 = Integer.parseInt(numbers[i]);
            int value2 = Integer.parseInt(numbers2[i]);

            if (value1 < value2)
                return -1;
            else if (value1 > value2)
                return 1;

            if(i+1 > numbers2.length-1)
                return 1;
        }

        return -1;
    }
}
