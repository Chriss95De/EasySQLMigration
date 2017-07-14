package de.oster.sqlcommander.persistence;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Christian on 14.07.2017.
 */
public class MigrationRowMapper implements RowMapper<Migration>
{
    public Migration mapRow(ResultSet rs, int rowNum) throws SQLException {

        Migration migration = new Migration();
        migration.setName(rs.getString("name"));
        migration.setVersion(rs.getString("version"));
        migration.setHash(rs.getString("hash"));
        migration.setDidRun(rs.getBoolean("didRun"));
        migration.setCreated(rs.getDate("created"));

        return migration;
    }
}
