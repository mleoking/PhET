// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import java.awt.Color;

/**
 * Colors that changed frequently during development of this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPColors {

    public static final Color ATOM_A = Color.YELLOW;
    public static final Color ATOM_B = Color.GREEN;
    public static final Color ATOM_C = Color.PINK;

    public static final Color PLATE_DISABLED_COLOR = Color.LIGHT_GRAY;
    public static final Color PLATE_NEGATIVE_COLOR = PLATE_DISABLED_COLOR;//new Color( 255, 255, 153 ); // pale yellow
    public static final Color PLATE_POSITIVE_COLOR = PLATE_NEGATIVE_COLOR;

    // Color used for potential=0 by Jmol in roygb gradient, see http://jmol.sourceforge.net/jscolors/#gradnt
    public static final Color NEUTRAL_GREEN = new Color( 31, 247, 0 );
}
