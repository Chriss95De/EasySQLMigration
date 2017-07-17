# EasySQLMigration

EasySQLMigration sqlMigration = new EasySQLMigration("JDBC-URL", "User", "Password");  
sqlMigration.setSQLScripts("/folderToSql/");  
sqlMigration.migrate();  
