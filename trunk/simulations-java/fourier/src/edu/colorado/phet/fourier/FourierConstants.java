/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier;

import java.awt.Cursor;
import java.awt.geom.Point2D;


/**
 * FourierConstants constains various constants.
 * Modify these at your peril ;-)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierConstants {

    /* Not intended for instantiation. */
    private FourierConstants() {}
    
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
    // Properties files
    //----------------------------------------------------------------------------
    
    public static final String SIM_PROPERTIES_NAME = "fourier";
    public static final String SIM_STRINGS_NAME = "localization/FourierStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final double CLOCK_STEP = 1;
    private static final int CLOCK_FRAME_RATE = 25;  // frames per second
    public static final int CLOCK_DELAY = ( 1000 / FourierConstants.CLOCK_FRAME_RATE ); // milliseconds
    public static final boolean CLOCK_TIME_STEP_IS_CONSTANT = true;
    public static final boolean CLOCK_ENABLE_CONTROLS = true;
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final String FONT_NAME = "Lucida Sans";
    
    //----------------------------------------------------------------------------
    // Harmonics
    //----------------------------------------------------------------------------
    
    public static final int MIN_HARMONICS = 1;
    public static final int MAX_HARMONICS = 11;
    public static final double MAX_HARMONIC_AMPLITUDE = ( 4 / Math.PI );
    
    /** Arbitrary value for the length (L) of the fundamental harmonic */
    public static final double L = 1.0;
    
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

    private static final String IMAGES_DIRECTORY = "images/";
    public static final String MAXIMIZE_BUTTON_IMAGE = IMAGES_DIRECTORY + "maximizeButton.png";
    public static final String MINIMIZE_BUTTON_IMAGE = IMAGES_DIRECTORY + "minimizeButton.png";
    public static final String SOUND_MAX_IMAGE = IMAGES_DIRECTORY + "soundMax.png";
    public static final String SOUND_MIN_IMAGE = IMAGES_DIRECTORY + "soundMin.png";
    public static final String ZOOM_BACKGROUND_HORIZONTAL_IMAGE = IMAGES_DIRECTORY + "zoomBackgroundHorizontal.png";
    public static final String ZOOM_BACKGROUND_VERTICAL_IMAGE = IMAGES_DIRECTORY + "zoomBackgroundVertical.png";
    public static final String ZOOM_IN_BUTTON_IMAGE = IMAGES_DIRECTORY + "zoomInButton.png";
    public static final String ZOOM_IN_BUTTON_PRESSED_IMAGE = IMAGES_DIRECTORY + "zoomInButtonPressed.png";
    public static final String ZOOM_OUT_BUTTON_IMAGE = IMAGES_DIRECTORY + "zoomOutButton.png";
    public static final String ZOOM_OUT_BUTTON_PRESSED_IMAGE = IMAGES_DIRECTORY + "zoomOutButtonPressed.png";
}
