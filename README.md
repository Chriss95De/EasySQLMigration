# EasySQLMigration

How to use:

EasySQLMigration sqlMigration = new EasySQLMigration("JDBC-Driverclass", "JDBC-URL", "User", "Password");
sqlMigration.setSQLScripts("/folderToSql/");
sqlMigration.setMigrationTableName("migration_tableName");
sqlMigration.setPrefixes("sql"); 
sqlMigration.setSeparator("_");

sqlMigration.migrate();
