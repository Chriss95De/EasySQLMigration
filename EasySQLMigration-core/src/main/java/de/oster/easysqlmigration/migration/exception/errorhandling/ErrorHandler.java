package de.oster.easysqlmigration.migration.exception.errorhandling;

import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.jdbc.support.SQLErrorCodesFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class ErrorHandler
{
    private SQLErrorCodesFactory sqlErrorCodesFactory;

    private DataSource dataSource;

    public ErrorHandler(DataSource dataSource)
    {
        this.dataSource = dataSource;
        try
        {
            sqlErrorCodesFactory = SQLErrorCodesFactory.getInstance();
            sqlErrorCodesFactory.registerDatabase(dataSource, dataSource.getConnection().getMetaData().getDatabaseProductName());
            SQLErrorCodes errorCodes = sqlErrorCodesFactory.getErrorCodes(dataSource.getConnection().getMetaData().getDatabaseProductName());
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
