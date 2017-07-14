import de.oster.sqlcommander.jdbc.PersistenceManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Christian on 12.07.2017.
 */
public class PersistenceManagerTest extends CustomTest
{

    @Test
    public void createEntityManagerFactory()
    {
        Assert.assertNotNull(PersistenceManager.get());
    }
}
