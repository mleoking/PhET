/** Sam Reid*/
package edu.colorado.phet.forces1d;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:27:37 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DUtil {

    public static Color transparify( Color c, int alpha ) {
        return new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
    }
}
