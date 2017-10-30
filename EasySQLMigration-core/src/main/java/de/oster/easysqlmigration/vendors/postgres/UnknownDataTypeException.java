package de.oster.easysqlmigration.vendors.postgres;

import de.oster.easysqlmigration.migration.exception.ErrorType;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.Handler;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.UnkownDataTypeHandler;
import de.oster.easysqlmigration.vendors.TypedException;
import org.springframework.dao.DataAccessException;

public class UnknownDataTypeException extends DataAccessException implements TypedException
{
    private ErrorType errorType;

    private String errorMessage;

    private Integer errorPos;

    public UnknownDataTypeException(String msg) {
        super(msg);
    }

    public UnknownDataTypeException(String msg, Throwable cause)
    {
        super(msg, cause);

        try {
            errorType = ErrorType.UNKOWNDATATYPE;
            errorMessage = parseRelevantMessage(msg);
        }
        catch (Exception e)
        {
            //something changed, sucks but can happen
        }

    }

    private String parseRelevantMessage(String message)
    {
        String[] messageParts = message.split("\n");

        return "";
    }

    @Override
    public Class<? extends Handler> getHandler() {
        return UnkownDataTypeHandler.class;
    }

    @Override
    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
