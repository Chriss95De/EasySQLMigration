package de.oster.easysqlmigration.migration.exception.errorhandling.type;

public class HandlerUtil
{
    //TODO work this out right
    public static String pointOutLineAndPos( String sqlFile, String sqlStatement)
    {
        String[] lines = sqlFile.split("\r\n");

        int posInFile = sqlFile.indexOf(sqlStatement);

        int charCount = 0;
        for (int i = 0; i < lines.length; i++)
        {
            String curLine = lines[i] + "\r\n";
            charCount += curLine.length();
            if(charCount >= posInFile)
            {
                return "line: " + i;
            }
        }

        return "undefined";
    }

    public static String info(String message)
    {
        return "~EasySQLMigration~: "+message+"\n";
    }
}
