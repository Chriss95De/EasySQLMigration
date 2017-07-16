package de.oster.sqlcommander;

/**
 * Created by Christian on 15.07.2017.
 */
public class Connection
{
    private String jdbcDriver;
    private String jdbcURL;
    private String user;
    private String password;

    public Connection() {
    }

    public Connection(String jdbcDriver, String jdbcURL, String user, String password) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcURL = jdbcURL;
        this.user = user;
        this.password = password;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
