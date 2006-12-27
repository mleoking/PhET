/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 10:10:38 AM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */

public class RampUtil {

    public static Color transparify( Color c, int alpha ) {
        return new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
    }
}
