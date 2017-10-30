package de.oster.easysqlmigration.migration.jdbc.repository;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class SimpleJDBCRepository
{
    protected JdbcTemplate jdbcTemplate;

    public SimpleJDBCRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate =  jdbcTemplate;
    }

    public SimpleJDBCRepository() {
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate =  jdbcTemplate;
    }
}
