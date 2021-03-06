package de.oster.easysqlmigration.migration;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MigrationRowMapper implements RowMapper<MigrationObject>
{
    public MigrationObject mapRow(ResultSet rs, int rowNum) throws SQLException {

        MigrationObject migration = new MigrationObject();
        migration.setName(rs.getString("name"));
        migration.setVersion(rs.getString("version"));
        migration.setHash(rs.getString("hash"));
        migration.setDidRun(rs.getBoolean("didRun"));
        migration.setCreated(rs.getDate("created"));

        return migration;
    }


}
