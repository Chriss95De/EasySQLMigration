package de.oster.easysqlmigration.migration;

import java.util.Date;

/**
 * Created by Christian on 09.08.2017.
 */
public interface Migration
{
    String getName();

    String getHash();

    boolean didRun();

    Date getCreated();
}
