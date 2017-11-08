package de.oster.easysqlmigration.migration;

import java.io.IOException;
import java.nio.charset.Charset;

import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import de.oster.easysqlmigration.migration.util.HashGenerator;
import org.apache.commons.io.IOUtils;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLScriptObject
{
    private String name;

    private String file;

    private String version;

    private String hash;

    private boolean didRun = false;

    private String sqlScript;

    public SQLScriptObject() {
    }

    public SQLScriptObject(String name, String path, String version, String sqlFile) throws IOException {
        this.name = name;
        this.file = path;
        this.version = version;
        this.hash = HashGenerator.getHash(this.getClass().getResourceAsStream(sqlFile));
        this.sqlScript = IOUtils.toString(this.getClass().getResourceAsStream(sqlFile), Charset.defaultCharset());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isDidRun() {
        return didRun;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean didRun() {
        return didRun;
    }

    public void setDidRun(boolean didRun) {
        this.didRun = didRun;
    }

    public String getSqlScript() {
        return sqlScript;
    }

    public void setSqlScript(String sqlScript) {
        this.sqlScript = sqlScript;
    }

    public static SQLScriptObject createFromFile(String separator, String file) throws IOException, SQLMigrationException {

        String name = file.substring(file.lastIndexOf("/")+1, file.length());

        int lastOcc = name.lastIndexOf(separator);

        if(lastOcc == -1)
            throw new SQLMigrationException("\nbad syntax in your sqlmigration filename: " + name + "\n"+
                    "seperator -> " + separator  + " not found");

        SQLScriptObject sqlScriptObject = new SQLScriptObject(
                name,
                file,
                name.substring(0, lastOcc).replaceAll("[^\\d.]", ""),
                file);

        return sqlScriptObject;
    }
}
