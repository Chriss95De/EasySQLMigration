package de.oster.easysqlmigration.migration.exception.errorhandling.type;

import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.vendors.TypedException;

public interface Handler
{
    void handle(TypedException e, Migration migration);
}
