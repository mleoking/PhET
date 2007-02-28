
import edu.colorado.phet.persistence.CckRemotePersistence;

/**
 * Class: CckPersistenceTest
 * Package: PACKAGE_NAME
 * Author: Another Guy
 * Date: Feb 5, 2004
 */

public class CckPersistenceTest {
    public static void main( String[] args ) {
        CckRemotePersistence crp = new CckRemotePersistence(
                "http://cosmos.colorado.edu/phet/cckStore",
                "Fred Flintstone" );
        crp.store( "Hey Wilma!" );
    }
}
