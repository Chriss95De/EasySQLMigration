package de.oster.easysqlmigration.migration;

import de.oster.easysqlmigration.Connection;
import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import de.oster.easysqlmigration.migration.exception.errorhandling.ErrorHandler;
import de.oster.easysqlmigration.migration.jdbc.repository.MigrationRepository;
import de.oster.easysqlmigration.vendors.TypedException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static de.oster.easysqlmigration.migration.api.EasySQLMigration.log;

/**
 * Created by Christian on 12.07.2017.
 */
public class EasySQLMigrationImpl
{
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
        List<Resource> files = new ArrayList<Resource>();

        if(paths == null)
            return new ArrayList<SQLScriptObject>();

        for(String path : paths)
        {
            files.addAll(getResourceFiles(path));
        }

        List<SQLScriptObject> sqlScriptObjectsTemp = new ArrayList<SQLScriptObject>();
        for(Resource resource : files)
            sqlScriptObjectsTemp.add(SQLScriptObject.createFromFile(versionFileNameSeparator, resource));

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

        persistenceManager.getTransactionTemplate().execute(status -> {
            log.info("---started migration---");

            Long startTime = System.currentTimeMillis();

            List<SQLScriptObject> sqlScriptObjects;
            try
            {
                sqlScriptObjects = searchSQLScripts(urlPath, prefixes, false);
            }
            catch (IOException e)
            {
                throw new SQLMigrationException(e.getMessage(), e.getCause());
            }

            if(sqlScriptObjects == null){
                log.info("---nothing to migrate---");
                return null;
            }

            //cache all migrations to run checks on them
            migrationRepository.createMigrationTableIfNotExist(schemaWithTabel);
            List<MigrationObject> allMigrations = migrationRepository.getAllMigrations(schemaWithTabel);

            for(SQLScriptObject sqlScriptObj : sqlScriptObjects)
            {
                log.info("");
                log.info("started sqlmigration " + sqlScriptObj.getName());
                try
                {
                    cleanUpFile(sqlScriptObj);
                    migrate(sqlScriptObj, allMigrations);
                }
                catch (SQLMigrationException exc)
                {
                    log.info("rolled back migrations");
                    throw new SQLMigrationException(exc.getMessage());
                }
                log.info("ended sqlmigration: " + sqlScriptObj.getName());
            }

            log.info("");
            log.info("---ended migration---");
            log.info("");
            log.info("migration took " + String.valueOf((System.currentTimeMillis()-startTime)/60) + " seconds.");
            log.info("");
            return null;
        });
    }

    private void cleanUpFile(SQLScriptObject sqlScriptObj)
    {
         String commentLessSQLScript = sqlScriptObj.getSqlScript().replaceAll("((['\"])(?:(?!\\2|\\\\).|\\\\.)*\\2)|\\/\\/[^\\n]*|\\/\\*(?:[^*]|\\*(?!\\/))*\\*\\/", "");
         sqlScriptObj.setSqlScript(commentLessSQLScript);
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
            String lastStr = "";
            try
            {
                //First check: should all be numbers
                for (String number : numbers) {
                    lastStr = number;
                    Integer.parseInt(number);
                }
            }
            catch (NumberFormatException exc)
            {
                throw new SQLMigrationException("\nbad syntax in your sqlmigration filename: " + sqlScriptObject.getName() + "\n"+
                "can not parse the version information out " + lastStr + "\n"+
                "\n"+
                "version file name separator: " + versionFileNameSeparator + "\n" +
                "under version separator: " + versionSeparator + "\n"+
                "should look something like " + "V1"+versionSeparator+"2"+versionFileNameSeparator+"sickmigration.sql");
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


    private List<Resource> getResourceFiles(String url) throws IOException
    {
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = pathMatchingResourcePatternResolver.getResources(url+"/*.sql");
        return new ArrayList<>(Arrays.asList(resources));
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
