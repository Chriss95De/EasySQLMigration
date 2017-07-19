# EasySQLMigration

EasySQLMigration sqlMigration = new EasySQLMigration("JDBC-URL", "User", "Password");  
sqlMigration.setSQLScripts("/folderToSql/");  
sqlMigration.migrate();  

# Scriptstructure

1_name.sql  
1_1_scondName.sql  
2_second.sql  

//yes its that easy :)
