/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

import java.awt.image.BufferedImage;

/**
 * Images used throughout the simulation.
 * Loaded statically to make it easy to debug missing images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLImages {

    /* not intended for instantiation */
    private CLImages() {}
    
    public static final BufferedImage BATTERY_UP = getBufferedImage( "battery_3D_up.png" );
    public static final BufferedImage BATTERY_DOWN = getBufferedImage( "battery_3D_down.png" );
    public static final BufferedImage SLIDER_KNOB = getBufferedImage( "sliderKnob.png" );
    public static final BufferedImage SLIDER_KNOB_HIGHLIGHT = getBufferedImage( "sliderKnobHighlight.png" );
    
    private static final BufferedImage getBufferedImage( String resourceName ) {
        return CLResources.getBufferedImage( resourceName );
    }
}
