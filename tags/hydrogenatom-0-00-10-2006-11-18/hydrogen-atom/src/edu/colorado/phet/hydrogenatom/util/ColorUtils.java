/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.util;

import java.awt.Color;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;


public class ColorUtils {

    /* Not intended for instantiation */
    private ColorUtils() {}
    
    public static Color wavelengthToColor( double wavelength ) {
        Color color = null;
        if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
            color = HAConstants.UV_COLOR;
        }
        else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
            color = HAConstants.IR_COLOR;
        }
        else {
            color = VisibleColor.wavelengthToColor( wavelength );
        }
        return color;
    }
}
