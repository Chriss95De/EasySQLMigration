# EasySQLMigration

EasySQLMigration sqlMigration = new EasySQLMigration("JDBC-URL", "User", "Password");  
sqlMigration.setSQLScripts("/folderToSql/");  
sqlMigration.setMigrationTableName("migration_tableName");  
sqlMigration.setPrefixes("sql");  
sqlMigration.setSeparator("_");  
sqlMigration.migrate();  
