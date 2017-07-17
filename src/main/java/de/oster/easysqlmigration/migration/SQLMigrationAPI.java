package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.migration.exception.SQLMigrationException;

/**
 * Created by Christian on 15.07.2017.
 */
interface SQLMigrationAPI
{
    public void migrate() throws SQLMigrationException;

    public void setSQLScripts(String...folders);

    public void setSeparator(String separator);

    public String getSeparator();

    public String getConnection();

    public String[] getUrlPath();

    public void setUrlPath(String[] urlPath);

    public String getMigrationTableName();

    public void setMigrationTableName(String migrationTableName);

    public String getSchema();

    public void setSchema(String schema);

    public String[] getPrefixes();

    public void setPrefixes(String... prefixes);
}
