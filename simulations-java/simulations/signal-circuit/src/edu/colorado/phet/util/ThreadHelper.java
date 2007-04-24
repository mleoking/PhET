/*, 2003.*/
package edu.colorado.phet.util;

/**
 * User: Sam Reid
 * Date: Oct 19, 2003
 * Time: 6:25:05 AM
 *
 */
public class ThreadHelper {
    public static void quietNap( int waitTime ) {
        try {
            Thread.sleep( waitTime );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }
}
