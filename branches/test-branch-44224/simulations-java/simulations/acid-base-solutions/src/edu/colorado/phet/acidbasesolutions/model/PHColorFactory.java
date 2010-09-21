/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;

/**
 * Maps a pH value to a color, using a set of custom colors and interpolation.
 * Custom colors are provided for each integer pH value.
 * For an actual pH value, we select the 2 closest colors, then interpolate between them.
 * The interpolating is a linear interpolation of individual RGB components.
 */
public class PHColorFactory {

    // colors as shown in acid-base-solutions/doc/pH-colors.png
    protected static final Color[] COLORS = { 
            ABSColors.PH_0,
            ABSColors.PH_1,
            ABSColors.PH_2,
            ABSColors.PH_3,
            ABSColors.PH_4,
            ABSColors.PH_5,
            ABSColors.PH_6,
            ABSColors.PH_7,
            ABSColors.PH_8,
            ABSColors.PH_9,
            ABSColors.PH_10,
            ABSColors.PH_11,
            ABSColors.PH_12,
            ABSColors.PH_13,
            ABSColors.PH_14,
    };
    
    /* not intended for instantiation */
    private PHColorFactory() {}

    public static Color createColor( double pH ) {
        assert ( COLORS.length == ( ABSConstants.MAX_PH - ABSConstants.MIN_PH + 1 ) );
        Color color = null;
        int lowerPH = (int) pH;
        int upperPH = Math.round( (float) pH );
        if ( lowerPH == upperPH ) {
            color = COLORS[lowerPH];
        }
        else {
            double distance = ( pH - lowerPH ) / ( upperPH - lowerPH );
            color = ColorUtils.interpolateRBGA( COLORS[lowerPH], COLORS[upperPH], distance );
        }
        return color;
    }
}
