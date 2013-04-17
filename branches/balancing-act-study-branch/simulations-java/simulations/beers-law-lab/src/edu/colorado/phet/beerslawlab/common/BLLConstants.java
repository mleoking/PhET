// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

/**
 * Constants that are used globally.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLConstants {

    public static final int CONTROL_FONT_SIZE = 18;
    public static final int TICK_LABEL_FONT_SIZE = 14;
    public static final Stroke FLUID_STROKE = new BasicStroke( 0.25f );

    // Creates a stroke color for a fluid color.
    public static final Color createFluidStrokeColor( Color fluidColor ) {
        return fluidColor.darker().darker();
    }
}
