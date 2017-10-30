package de.oster.easysqlmigration.vendors;

import de.oster.easysqlmigration.migration.exception.ErrorType;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.Handler;

/**
 * Created by Christian on 20.09.2017.
 */
public interface TypedException
{
    Class<? extends Handler> getHandler();

    ErrorType getErrorType();

    String getErrorMessage();
}
