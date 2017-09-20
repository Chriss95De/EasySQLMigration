package de.oster.easysqlmigration.vendors.h2;

import de.oster.easysqlmigration.migration.exception.ErrorType;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.Handler;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.SyntaxErrorHandler;
import de.oster.easysqlmigration.vendors.TypedException;
import org.springframework.dao.DataAccessException;

import java.util.regex.Pattern;

public class SyntaxException extends DataAccessException implements TypedException
{

    private int errorPos;
    private String errorMessage;

    public SyntaxException(String msg) {
        super(msg);
    }

    public SyntaxException(String msg, Throwable cause) {

        super(msg, cause);

        try
        {
            errorMessage = "";
            String[] messageParts = msg.split("\n");
            for (int i = 0; i < messageParts.length; i++)
            {
                errorMessage += messageParts[i].replace("\r", "");
                if(messageParts[i].contains("[*]"))
                {
                    errorPos = i;
                    errorMessage += "          <-- syntax error";
                    errorMessage = errorMessage.replace("[*]", "");
                    break;
                }
            }
        }
        catch (Exception e)
        {}
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

    @Override
    public Integer errorPos() {
        return errorPos;
    }
}
