// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forces1d;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:27:37 PM
 */
public class Force1DUtil {

    public static Color transparify( Color c, int alpha ) {
        return new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
    }

    public static Border createSmoothBorder( String s ) {
        return BorderFactory.createTitledBorder( s );
    }
}
