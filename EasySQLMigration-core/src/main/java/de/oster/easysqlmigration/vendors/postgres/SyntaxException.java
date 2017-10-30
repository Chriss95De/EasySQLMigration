package de.oster.easysqlmigration.vendors.postgres;

import de.oster.easysqlmigration.migration.exception.ErrorType;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.Handler;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.SyntaxErrorHandler;
import de.oster.easysqlmigration.vendors.TypedException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.dao.DataAccessException;

public class SyntaxException extends DataAccessException implements TypedException
{
    private String errorMessage;

    public SyntaxException(String msg) {
        super(msg);
    }

    public SyntaxException(String msg, Throwable cause) {

        super(msg, cause);

        try
        {
            PSQLException realException = (PSQLException) cause;
            ServerErrorMessage serverErrorMessage = realException.getServerErrorMessage();
            errorMessage = msg;
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public Class<? extends Handler> getHandler() {
        return SyntaxErrorHandler.class;
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.SYNTAX_ERROR;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
