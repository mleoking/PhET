/*  */
package edu.colorado.phet.movingman;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 19, 2005
 * Time: 7:43:34 AM
 *
 */

public class MMUtil {
    public static boolean isHighScreenResolution() {
        return Toolkit.getDefaultToolkit().getScreenSize().width >= 1240;
    }
}
