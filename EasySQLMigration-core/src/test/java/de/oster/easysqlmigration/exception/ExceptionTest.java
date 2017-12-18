package de.oster.easysqlmigration.exception;

import org.junit.Test;

/**
 * Created by Christian on 20.09.2017.
 */
public interface ExceptionTest {

    @Test
    void unknownDataTypeTest();

    @Test
    void sytanxTest();

    @Test
    void handleComments();
}
