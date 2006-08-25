/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.text.DecimalFormat;


/**
 * HAConstants is a collection of constants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAConstants {

    /* Not intended for instantiation. */
    private HAConstants() {}
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    
    //----------------------------------------------------------------------------
    // Localization
    //----------------------------------------------------------------------------
    
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/HAStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    
    // The clock control area has a slider for choosing a clock "speed".
    // These are the clock steps that correspond to each speed setting.
    public static final double[] CLOCK_STEPS = {
        1, 2, 3, 4, 5
    };
       
    // Defaults
    public static final int DEFAULT_CLOCK_INDEX = 0;
    public static final double DEFAULT_CLOCK_STEP = CLOCK_STEPS[ DEFAULT_CLOCK_INDEX ];
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final String FONT_NAME = "Lucida Sans";
    public static final Font TIME_DISPLAY_FONT = new Font( FONT_NAME, Font.BOLD, 22 );
    public static final Font TIME_UNITS_FONT = new Font( FONT_NAME, Font.PLAIN, 22 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 220, 220, 220 );
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
    public static final String IMAGE_GUN = IMAGES_DIRECTORY + "gun.png";
    public static final String IMAGE_GUN_ON_BUTTON = IMAGES_DIRECTORY + "gun-on-button.png";
    public static final String IMAGE_GUN_OFF_BUTTON = IMAGES_DIRECTORY + "gun-off-button.png";
    public static final String IMAGE_SPECTRUM = IMAGES_DIRECTORY + "spectrum.png";
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );
    public static final Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );
}
