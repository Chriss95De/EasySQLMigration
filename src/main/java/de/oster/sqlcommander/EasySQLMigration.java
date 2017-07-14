package de.oster.sqlcommander;

import de.oster.sqlcommander.jdbc.exception.SQLMigrationException;

/**
 * Created by Christian on 15.07.2017.
 */
public interface EasySQLMigration
{
    public void migrate() throws SQLMigrationException;

    public void setSQLScripts(String...folders);

    public void setSeparator(String separator);

    public String getSeparator();


    public String getJdbcDriver();

    public void setJdbcDriver(String jdbcDriver);

    public String getJdbcURL();

    public void setJdbcURL(String jdbcURL);

    public String getUser();

    public void setUser(String user);

    public String getPassword();

    public void setPassword(String password);

    public String[] getUrlPath();

    public void setUrlPath(String[] urlPath);

    public String getMigrationTableName();

    public void setMigrationTableName(String migrationTableName);

    public String[] getPrefixes();

    public void setPrefixes(String... prefixes);
}
