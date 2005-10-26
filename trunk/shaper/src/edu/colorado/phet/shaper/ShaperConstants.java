/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper;

import java.awt.Color;
import java.awt.Cursor;


/**
 * ShaperConstants constains various constants.
 * Modify these at your peril.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ShaperConstants {

    /* Not intended for instantiation. */
    private ShaperConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging switches
    //----------------------------------------------------------------------------
    
    public static final boolean ANIMATION_ENABLED = true;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    
    //----------------------------------------------------------------------------
    // Localization
    //----------------------------------------------------------------------------
    
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/ShaperStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final double CLOCK_TIME_STEP = 1;
    public static final int CLOCK_FRAME_RATE = 25;  // frames per second
    public static final boolean CLOCK_TIME_STEP_IS_CONSTANT = true;
    public static final boolean CLOCK_ENABLE_CONTROLS = true;
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final String FONT_NAME = "Lucida Sans";
    
    //----------------------------------------------------------------------------
    // Colors
    //----------------------------------------------------------------------------
    
    /** Color of the waveform in the Output Pulse graph. */
    public static final Color OUTPUT_PULSE_COLOR = new Color( 1f, 0.411802f, 0.705893f );
    
    /** Maximum opacity of light rays */
    public static final int MAX_LIGHT_ALPHA = 200; // range is 0-255
    
    //----------------------------------------------------------------------------
    // Harmonics
    //----------------------------------------------------------------------------
    
    public static final int MIN_HARMONICS = 1;
    public static final int MAX_HARMONICS = 7;
    public static final double MAX_HARMONIC_AMPLITUDE = ( 4 / Math.PI );
    
    /** Arbitrary value for the length (L) of the fundamental harmonic */
    public static final double L = 1.0;
        
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
    // Game parameters
    //----------------------------------------------------------------------------
    
    /** The closeness value that is considered a "match" */
    public static final double CLOSENESS_MATCH = 0.95;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    public static final String CLOSE_BUTTON_IMAGE = IMAGES_DIRECTORY + "closeButton.png";
    
}
