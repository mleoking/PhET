
import edu.colorado.phet.persistence.PersistenceStrategy;
import edu.colorado.phet.persistence.RemotePersistence;

import java.util.GregorianCalendar;
import java.util.Calendar;

/**
 * Class: RemoteTest
 * Package: PACKAGE_NAME
 * Author: Another Guy
 * Date: Feb 5, 2004
 */

public class RemoteTest {
    public static void main( String[] args ) {
        String testText = "Yo ho ho!";
        Calendar cal = new GregorianCalendar();
        // Get the components of the time
        int year = cal.get(Calendar.YEAR);             // 2002
        int month = cal.get(Calendar.MONTH);           // 0=Jan, 1=Feb, ...
        int day = cal.get(Calendar.DAY_OF_MONTH);      // 1...
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);     // 0..23
        int min = cal.get(Calendar.MINUTE);             // 0..59
        int sec = cal.get(Calendar.SECOND);             // 0..59
        String fileName = "test-" + sec + "-" + min
                + "-" + hour24 + "-" + day + "-" + month + "-" + year;


        PersistenceStrategy ps = new RemotePersistence( "http://cosmos.colorado.edu/phet/phptest/remote-pst.php",
                                                        fileName );
        ps.store(testText);
    }
}
