/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

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
    public static final BufferedImage VOLTMETER = getBufferedImage( "voltmeter.png" );
    public static final BufferedImage RED_PROBE = getBufferedImage( "redProbe.png" );
    public static final BufferedImage BLACK_PROBE = getBufferedImage( "blackProbe.png" );
    
    // common images
    public static final BufferedImage CLOSE_BUTTON = getCommonBufferedImage( PhetCommonResources.IMAGE_CLOSE_BUTTON );
    
    private static final BufferedImage getBufferedImage( String resourceName ) {
        return CLResources.getBufferedImage( resourceName );
    }
    
    private static final BufferedImage getCommonBufferedImage( String resourceName ) {
        return PhetCommonResources.getImage( resourceName );
    }
}
