package de.oster.easysqlmigration.migration.exception;

public enum  ErrorType
{
    UNKOWNDATATYPE("UNKNOWN DATA TYPE"), SYNTAX_ERROR("SYNTAX ERROR");

    ErrorType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
