package de.oster.sqlcommander.migration;

import de.oster.sqlcommander.jdbc.exception.SQLMigrationException;
import de.oster.sqlcommander.migration.util.ByteArrayHexConverter;
import de.oster.sqlcommander.migration.util.HashGenerator;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLScriptObject
{
    private String name;

    private String version;

    private String hash;

    private boolean didRun = false;

    private String sqlScript;

    public SQLScriptObject() {
    }

    public SQLScriptObject(String name, String version, String hash, File sqlFile) throws IOException {
        this.name = name;
        this.version = version;
        this.hash = hash;
        this.sqlScript = FileUtils.readFileToString(sqlFile, Charset.defaultCharset());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public static SQLScriptObject createFromFile(String separator, File file) throws IOException, SQLMigrationException {

        int lastOcc = file.getName().lastIndexOf(separator);

        if(lastOcc == -1)
            throw new SQLMigrationException("\nbad syntax in your migration filename: " + file.getName() + "\n"+
                    "seperator -> " + separator  + " not found");

        SQLScriptObject sqlScriptObject = new SQLScriptObject(
                file.getName().substring(lastOcc+1, file.getName().length()),
                file.getName().substring(0, lastOcc),
                HashGenerator.getHash(file),
                file);

        return sqlScriptObject;
    }
}
