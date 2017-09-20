package de.oster.easysqlmigration.vendors.h2;

import de.oster.easysqlmigration.migration.exception.ErrorType;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.Handler;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.UnkownDataTypeHandler;
import de.oster.easysqlmigration.vendors.TypedException;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;
import java.util.regex.Pattern;

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

        String unknownType = messageParts[1].substring(messageParts[1].indexOf("\"")+1, messageParts[1].lastIndexOf("\""));

        for (int i = 4; i < messageParts.length; i++)
        {
            String line = messageParts[i].toLowerCase();
            if(line.contains(unknownType.toLowerCase()))
                errorPos = i;
        }

        String newMessage = "";
        for (int i = 0; i < messageParts.length; i++)
        {
            newMessage += messageParts[i].replace("\r", "");

            if(errorPos == i)
            {
                newMessage += "             <-- UNKNOWN TYPE";
                break;
            }

            newMessage += "\n";
        }

        return newMessage;
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

    @Override
    public Integer errorPos() {
        return errorPos;
    }
}
