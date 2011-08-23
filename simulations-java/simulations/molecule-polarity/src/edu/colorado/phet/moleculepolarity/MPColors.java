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
    public static final Color BOND = Color.DARK_GRAY;

    public static final Color PLATE_DISABLED_COLOR = Color.LIGHT_GRAY;
    public static final Color PLATE_NEGATIVE_COLOR = PLATE_DISABLED_COLOR;
    public static final Color PLATE_POSITIVE_COLOR = PLATE_NEGATIVE_COLOR;

    // Color used for potential=0 by Jmol in roygb gradient, see http://jmol.sourceforge.net/jscolors/#gradnt
    public static final Color NEUTRAL_GREEN = new Color( 31, 247, 0 );

    // Gradient used for electron density, more to less.
    public static final Color[] BW_GRADIENT = new Color[] { Color.BLACK, Color.WHITE };

    // Primary gradient for molecular electrostatic polarity (mep), negative to positive.
    public static final Color[] RWB_GRADIENT = new Color[] { Color.RED, Color.WHITE, Color.BLUE };

    /*
     * Secondary gradient for mep, negative to positive.
     * This is Jmol's roybg gradient, documented at http://jmol.sourceforge.net/jscolors/#gradnt.
     * A copy of the Jmol gradients image shown at this link is also checked into this sim's doc directory.
     * The colors below were acquired from the roygb gradient shown in this image.
     */
    public static final Color[] ROYGB_GRADIENT = new Color[] {
            Color.RED,
            new Color( 242, 30, 0 ),
            new Color( 247, 62, 0 ),
            new Color( 247, 93, 0 ),
            new Color( 247, 124, 0 ),
            new Color( 247, 155, 0 ),
            new Color( 244, 214, 0 ),
            new Color( 244, 230, 0 ),
            new Color( 242, 242, 0 ),
            new Color( 227, 227, 0 ),
            new Color( 217, 247, 0 ),
            new Color( 180, 242, 0 ),
            new Color( 121, 247, 0 ),
            new Color( 93, 247, 0 ),
            new Color( 61, 242, 0 ),
            MPColors.NEUTRAL_GREEN,
            new Color( 0, 244, 0 ),
            new Color( 0, 244, 31 ),
            new Color( 0, 247, 93 ),
            new Color( 0, 247, 124 ),
            new Color( 0, 247, 155 ),
            new Color( 0, 250, 188 ),
            new Color( 0, 243, 217 ),
            new Color( 0, 247, 247 ),
            new Color( 0, 184, 244 ),
            new Color( 0, 153, 244 ),
            new Color( 0, 121, 242 ),
            new Color( 0, 89, 236 ),
            new Color( 0, 60, 239 ),
            new Color( 0, 30, 242 ),
            Color.BLUE };
}
