package de.oster.sqlcommander.migration;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Christian on 16.07.2017.
 */
public class DetermineOrderTest
{
    @Test
    public void determineOrderTest()
    {
         Assert.assertEquals(-1, MigrationActions.determineOrder("2", "2_1", "_"));
    }
}
