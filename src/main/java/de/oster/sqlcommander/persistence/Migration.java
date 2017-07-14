package de.oster.sqlcommander.persistence;

import java.util.Date;

public class Migration
{
    private String version;

    private String name;

    private String hash;

    private boolean didRun;

    private Date created;

    public Migration() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
