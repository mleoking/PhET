/* Copyright 2004, University of Colorado */

/*
 * CVS Info - 
 * Filename : $Source$
 * Branch : $Name$ 
 * Modified by : $Author$ 
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision3;

import java.awt.Color;
import java.awt.Font;

/**
 * ColorVisionConfig contains global configuration values for the Color Vision
 * application. See ColorVisionStrings.properties for localized Strings that are
 * visible to the user.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorVisionConfig {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Resource bundles
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/ColorVisionStrings";

    // Clock constants
    public static final double TIME_STEP = 1;
    public static final int WAIT_TIME = 50;

    // Images
    public static final String IMAGES_DIRECTORY = "images/";
    public static final String HEAD_BACKGROUND_IMAGE = IMAGES_DIRECTORY + "headBackground.png";
    public static final String HEAD_FOREGROUND_IMAGE = IMAGES_DIRECTORY + "headForeground.png";
    public static final String SPECTRUM_IMAGE = IMAGES_DIRECTORY + "spectrum.png";
    public static final String SPOTLIGHT_IMAGE = IMAGES_DIRECTORY + "spotlight.png";
    public static final String SWITCH_ON_IMAGE = IMAGES_DIRECTORY + "universalSwitchOn.png";
    public static final String SWITCH_OFF_IMAGE = IMAGES_DIRECTORY + "universalSwitchOff.png";

    // Dimensions
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    public static final int CONTROL_PANEL_MIN_WIDTH = 150;

    // Colors
    public static final Color APPARATUS_BACKGROUND = Color.BLACK;
    public static final Color LABEL_COLOR = Color.WHITE;

    // Fonts
    public static final Font LABEL_FONT = new Font( "SansSerif", Font.PLAIN, 18 );

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private ColorVisionConfig() {}
}