package de.oster.easysqlmigration.migration.exception.errorhandling;

import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.jdbc.support.SQLErrorCodesFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class ErrorHandler
{
    private SQLErrorCodesFactory sqlErrorCodesFactory;

    public ErrorHandler(DataSource dataSource)
    {
        sqlErrorCodesFactory = SQLErrorCodesFactory.getInstance();
        try
        {
            sqlErrorCodesFactory.registerDatabase(dataSource, dataSource.getConnection().getMetaData().getDatabaseProductName());
            SQLErrorCodes errorCodes = sqlErrorCodesFactory.getErrorCodes(dataSource);
            System.out.println();
        }
        catch (SQLException e)
        {
            new SQLConnectionException(e.getMessage());
        }
    }

    public String extractErroMessage(String reason, Throwable cause)
    {
        String message = "";

        

        return message;
    }
}
