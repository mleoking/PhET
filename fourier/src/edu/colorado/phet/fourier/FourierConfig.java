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


/**
 * FourierConfig contains global configuration values.
 * See FourierStrings.properties for localized Strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierConfig {

    //----------------------------------------------------------------------------
    // Debugging switches
    //----------------------------------------------------------------------------
    
    public static final boolean ANIMATION_ENABLED = true;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String APP_VERSION = "0.0 (fourier-sandbox-2005-06-23)";
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    
    //----------------------------------------------------------------------------
    // Localization
    //----------------------------------------------------------------------------
    
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/FourierStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final double CLOCK_TIME_STEP = 1;
    public static final int CLOCK_FRAME_RATE = 25;  // frames per second
    public static final boolean CLOCK_TIME_STEP_IS_CONSTANT = true;
    public static final boolean CLOCK_ENABLE_CONTROLS = true;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    private static final String IMAGES_DIRECTORY = "images/";
    public static final String ZOOM_BACKGROUND_HORIZONTAL_IMAGE = IMAGES_DIRECTORY + "zoomBackgroundHorizontal.png";
    public static final String ZOOM_BACKGROUND_VERTICAL_IMAGE = IMAGES_DIRECTORY + "zoomBackgroundVertical.png";
    public static final String ZOOM_IN_BUTTON_IMAGE = IMAGES_DIRECTORY + "zoomInButton.png";
    public static final String ZOOM_IN_BUTTON_PRESSED_IMAGE = IMAGES_DIRECTORY + "zoomInButtonPressed.png";
    public static final String ZOOM_IN_BUTTON_DISABLED_IMAGE = IMAGES_DIRECTORY + "zoomInButtonDisabled.png";
    public static final String ZOOM_OUT_BUTTON_IMAGE = IMAGES_DIRECTORY + "zoomOutButton.png";
    public static final String ZOOM_OUT_BUTTON_PRESSED_IMAGE = IMAGES_DIRECTORY + "zoomOutButtonPressed.png";
    public static final String ZOOM_OUT_BUTTON_DISABLED_IMAGE = IMAGES_DIRECTORY + "zoomOutButtonDisabled.png";
    
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
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private FourierConfig() {}
}
