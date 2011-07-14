// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fourier;

import java.awt.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.TrigCache;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * FourierConstants contains various global constants.
 * Modify these at your peril ;-)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FourierConstants {

    /* Not intended for instantiation. */
    private FourierConstants() {
    }

    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------

    public static final String PROJECT_NAME = "fourier";

    public static final TrigCache TRIG_CACHE = new TrigCache( 360 );

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------

    public static final double CLOCK_STEP = 1;
    private static final int CLOCK_FRAME_RATE = 25;  // frames per second
    public static final int CLOCK_DELAY = ( 1000 / FourierConstants.CLOCK_FRAME_RATE ); // milliseconds

    //----------------------------------------------------------------------------
    // Harmonics
    //----------------------------------------------------------------------------

    public static final int MIN_HARMONICS = 1;
    public static final int MAX_HARMONICS = 11;
    public static final double MAX_HARMONIC_AMPLITUDE = ( 4 / Math.PI );

    public static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    public static final double L = 1; // Arbitrary value for the length (L) of the fundamental

    //----------------------------------------------------------------------------
    // Animation
    //----------------------------------------------------------------------------

    public static final double ANIMATION_STEPS_PER_CYCLE = 50;

    //----------------------------------------------------------------------------
    // Charts
    //----------------------------------------------------------------------------

    public static final double AUTOSCALE_PERCENTAGE = 1.05;

    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------

    public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );

    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final BufferedImage MAXIMIZE_BUTTON_IMAGE = PhetCommonResources.getImage( "buttons/maximizeButton.png" );
    public static final BufferedImage MINIMIZE_BUTTON_IMAGE = PhetCommonResources.getImage( "buttons/minimizeButton.png" );
    public static final BufferedImage SOUND_MAX_IMAGE = FourierResources.getImage( "soundMax.png" );
    public static final BufferedImage SOUND_MIN_IMAGE = FourierResources.getImage( "soundMin.png" );
}
