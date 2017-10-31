package de.oster.easysqlmigration.migration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.oster.easysqlmigration.Connection;
import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import de.oster.easysqlmigration.migration.exception.errorhandling.ErrorHandler;
import de.oster.easysqlmigration.migration.jdbc.repository.MigrationRepository;
import de.oster.easysqlmigration.vendors.TypedException;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;

/**
 * Created by Christian on 12.07.2017.
 */
public class EasySQLMigrationImpl
{
    protected static Logger log = Logger.getRootLogger();
    private final ClassLoader classLoader;

    //JDBC Information
    protected PersistenceManager persistenceManager;
    protected Connection connection;

    //Persistence
    protected MigrationRepository migrationRepository;

    protected String[] urlPath;
    protected String[] prefixes = {"sql"};

    protected String versionFileNameSeparator = "_";
    protected String versionSeparator = "_";

    private String internalVersionSeparator = ".";

    protected String migrationTableName = "sql_migration";
    protected String schema = "public";

    protected String schemaWithTabel = schema+"."+migrationTableName;


    public EasySQLMigrationImpl()
    {
        this.classLoader = getClass().getClassLoader();
    }

    public void initAll()
    {
        this.persistenceManager = new PersistenceManager(connection);
        this.persistenceManager.registerRepository(new MigrationRepository());

        //repo
        migrationRepository = (MigrationRepository)persistenceManager.getRepository(MigrationRepository.class);

        ErrorHandler errorHandler = new ErrorHandler(this.persistenceManager.getDataSource());

    }

    public EasySQLMigrationImpl(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    protected void addMigrationEntry(SQLScriptObject sqlScriptObj) {

        //create the sqlmigration
        migrationRepository.createMigrationTableIfNotExist(schemaWithTabel);

        MigrationObject migration = migrationRepository.getMigrationByVersion(sqlScriptObj, schemaWithTabel);
        if(migration == null)
        {
            migrationRepository.addNewMigration(sqlScriptObj, schemaWithTabel);
        }
    }

    protected List<SQLScriptObject> searchSQLScripts(String[] paths, final String[] prefixes, boolean recursive)
          throws IOException, SQLMigrationException

    {
        List<String> files = new ArrayList<String>();

        if(paths == null)
            return new ArrayList<SQLScriptObject>();

        for(String path : paths)
        {
            files.addAll(getResourceFiles(path));
        }

        List<SQLScriptObject> sqlScriptObjectsTemp = new ArrayList<SQLScriptObject>();
        for(String file : files)
            sqlScriptObjectsTemp.add(SQLScriptObject.createFromFile(versionFileNameSeparator, file));

        runSyntaxCheck(sqlScriptObjectsTemp);

        //create sorted list by version
        final List<SQLScriptObject> sqlScriptObjects = new ArrayList<SQLScriptObject>()
        {
            @Override
            public boolean add(SQLScriptObject sqlScriptObject) {
                super.add(sqlScriptObject);
                Collections.sort(this, sqlScriptObjectComparator);
                return true;
            }

            @Override
            public boolean addAll(Collection<? extends SQLScriptObject> c)
            {
                for(SQLScriptObject s : c )
                    this.add(s);
                return true;
            }
        };

        sqlScriptObjects.addAll(sqlScriptObjectsTemp);
        convertVersionSeparator(sqlScriptObjectsTemp);

        return sqlScriptObjects;
    }

    @Transactional(rollbackFor = java.lang.Exception.class)
    protected void doMigration() throws SQLMigrationException {

        persistenceManager.getTransactionTemplate().execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status)
            {
                log.info("---started migration---");

                Long startTime = System.currentTimeMillis();

                List<SQLScriptObject> sqlScriptObjects = new ArrayList<>();
                try
                {
                    sqlScriptObjects = searchSQLScripts(urlPath, prefixes, false);
                }
                catch (IOException e)
                {
                    throw new SQLMigrationException(e.getMessage(), e.getCause());
                }

                if(sqlScriptObjects == null)
                    return null;

                //cache all migrations to run checks on them
                migrationRepository.createMigrationTableIfNotExist(schemaWithTabel);
                List<MigrationObject> allMigrations = migrationRepository.getAllMigrations(schemaWithTabel);

                for(SQLScriptObject sqlScriptObj : sqlScriptObjects) {
                    log.info("");
                    log.info("started sqlmigration " + sqlScriptObj.getName());
                    try
                    {
                        migrate(sqlScriptObj, allMigrations);
                    }
                    catch (SQLMigrationException exc)
                    {
                        throw new SQLMigrationException(exc.getMessage());
                    }
                    log.info("ended sqlmigration: " + sqlScriptObj.getName());
                }

                log.info("");
                log.info("---ended migration---");
                log.info("");
                log.info("createInstance took " + String.valueOf((System.currentTimeMillis()-startTime)/60) + " seconds.");
                log.info("");
                return null;
            }
        });
    }

    private void migrate(SQLScriptObject sqlScriptObj, List<MigrationObject> allMigrations)
    {
        MigrationObject checkMigration = migrationRepository.getMigrationByVersion(sqlScriptObj, schemaWithTabel);
        if(checkMigration == null)
        {
            MigrationActions.runChecksOnMigration(allMigrations, sqlScriptObj, internalVersionSeparator);
        }

        //Add if missing
        this.addMigrationEntry(sqlScriptObj);

        //check hash
        MigrationObject migration = migrationRepository.getMigrationByVersion(sqlScriptObj, schemaWithTabel);
        if(!sqlScriptObj.getHash().equals(migration.getHash()))
            throw new SQLMigrationException(
                    "\nfailed to apply migration: "+sqlScriptObj.getName()+ "\n " +
                            "missmatch between allready applied version and current migration \n" +
                            " applied migration -> (hash:"+ migration.getHash() +") \n" +
                            " current migration -> (hash:"+ sqlScriptObj.getHash()+")");

        if(migration.didRun() == false)
        {
            String curStatement = "";
            try
            {
                String[] sqlStatements = sqlScriptObj.getSqlScript().split(";");
                for (String sqlStr : sqlStatements)
                {
                    curStatement = sqlStr+";";
                    persistenceManager.get().update(curStatement);
                }
            }
            catch (BadSqlGrammarException exc)
            {
                throw new SQLMigrationException("\n"+sqlScriptObj.getName()+": "+exc.getSQLException().getMessage(), migration, sqlScriptObj, curStatement);
            }
            catch (UncategorizedSQLException exc)
            {
                throw new SQLMigrationException("\n"+sqlScriptObj.getName()+": "+exc.getSQLException().getMessage(), migration, sqlScriptObj, curStatement);
            }
            catch (CannotGetJdbcConnectionException exc)
            {
                throw new SQLConnectionException("could not create jdbc connection", exc.getCause());
            }
            catch (DataAccessException exc)
            {
                if(exc instanceof TypedException)
                    throw new SQLMigrationException((TypedException) exc, migration, sqlScriptObj, curStatement);
                else
                    throw new SQLMigrationException("\n"+sqlScriptObj.getName()+": "+exc.getMessage(), migration, sqlScriptObj, curStatement);
            }

            migration.setDidRun(true);
            migrationRepository.updateMigration(migration, schemaWithTabel);
            log.info("applied migration");
        }
        else
        {
            log.info("nothing to migrate");
        }
    }

    private void runSyntaxCheck(List<SQLScriptObject> sqlScriptObjects) throws SQLMigrationException
    {
        for (SQLScriptObject sqlScriptObject : sqlScriptObjects)
        {
            String[] numbers = sqlScriptObject.getVersion().split(versionSeparator);
            try
            {
                //First check: should all be numbers
                for (String number : numbers) {
                    Integer.parseInt(number);
                }
            }
            catch (NumberFormatException exc)
            {
                throw new SQLMigrationException("\nbad syntax in your sqlmigration filename: " + sqlScriptObject.getName() + "\n"+
                "can not parse the version information");
            }
        }
    }

    private void convertVersionSeparator(List<SQLScriptObject> sqlScriptObjects) throws SQLMigrationException
    {
        for (SQLScriptObject sqlScriptObject : sqlScriptObjects)
        {
            sqlScriptObject.setVersion(sqlScriptObject.getVersion().replace(versionSeparator, internalVersionSeparator));
        }
    }

    private Comparator<SQLScriptObject> sqlScriptObjectComparator =  new Comparator<SQLScriptObject>()
    {
        public int compare(SQLScriptObject o1, SQLScriptObject o2)
        {
            return MigrationActions.determineOrder(o1.getVersion(), o2.getVersion(), versionSeparator);
        }
    };


    private List<String> getResourceFiles( String path ) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
              InputStream in = getResourceAsStream( path );
              BufferedReader br = new BufferedReader( new InputStreamReader( in ) ) ) {
            String resource;

            while( (resource = br.readLine()) != null ) {
                if(!path.equals("/"+resource))
                    filenames.add( path+"/"+resource );
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream( String resource ) {
        final InputStream in
              = getContextClassLoader().getResourceAsStream( resource );

        return in == null ? getClass().getResourceAsStream( resource ) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    protected void updateSchema()
    {
        schemaWithTabel = schema+"."+migrationTableName;
    }

    protected List<Migration> getRunnedMigrations()
    {
        List<MigrationObject> migrations = migrationRepository.getAllMigrations(this.schemaWithTabel);
        List<Migration> apiMigrations = new ArrayList<>();
        for (MigrationObject migrationObject : migrations)
            apiMigrations.add(migrationObject);
        return apiMigrations;
    }
}
