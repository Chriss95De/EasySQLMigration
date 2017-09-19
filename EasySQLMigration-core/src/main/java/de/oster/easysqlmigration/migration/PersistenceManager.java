package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.Connection;
import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import de.oster.easysqlmigration.migration.exception.errorhandling.ErrorHandler;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christian on 12.07.2017.
 */
class PersistenceManager
{
    private JdbcTemplate jdbcTemplate;

    private DataSource ds;

    List<SimpleJDBCRepository> simpleJDBCRepositoryList = new ArrayList<>();

    public PersistenceManager(Connection connection)
    {
        try
        {
            ds = new SimpleDriverDataSource(DriverManager.getDriver(connection.getJdbcURL()),
                    connection.getJdbcURL(),
                    connection.getUser(),
                    connection.getPassword());
        }
        catch (Exception exc)
        {
            throw new SQLConnectionException("error while creating the jdbc connection", exc.getCause());
        }

        //SQLMigrationException.errorHandler = new ErrorHandler(ds);
        jdbcTemplate = new JdbcTemplate(ds);

        try
        {
            jdbcTemplate.execute("SELECT 1");
        }
        catch (CannotGetJdbcConnectionException exc)
        {
            throw new SQLConnectionException("could not create jdbc connection \nerror message:\n" + exc.getCause().getMessage());
        }
    }

    public void registerRepository(SimpleJDBCRepository repositoryClass)
    {
        repositoryClass.setJdbcTemplate(jdbcTemplate);
        simpleJDBCRepositoryList.add(repositoryClass);
    }

    public SimpleJDBCRepository getRepository(Class<?> clazz)
    {
        for (SimpleJDBCRepository repository : simpleJDBCRepositoryList)
        {
            if(repository.getClass().equals(clazz))
            {
                return repository;
            }
        }

        throw  new RuntimeException("No repository found for class: " + clazz.getName());
    }

    public JdbcTemplate get()
    {
        return jdbcTemplate;
    }

    public DataSource getDataSource() {
        return ds;
    }
}
