/** Sam Reid*/
package util;

/**
 * User: Sam Reid
 * Date: Aug 9, 2004
 * Time: 12:33:03 PM
 * Copyright (c) Aug 9, 2004 by Sam Reid
 */
public class ThreadHelper {
    public static void quietNap( int waitTime ) {
        try {
            Thread.sleep( waitTime );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
