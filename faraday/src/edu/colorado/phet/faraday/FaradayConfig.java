/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday;

import java.awt.Color;
import java.awt.Font;


/**
 * FaradayConfig contains global configuration values for the Color Vision
 * application. See FaradayStrings.properties for localized Strings that are
 * visible to the user.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradayConfig {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Resource bundles
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/FaradayStrings";

    // Clock constants
    public static final double TIME_STEP = 1;
    public static final int WAIT_TIME = 50; //DEBUG

    // Layers
    public static final double DEBUG_LAYER = Double.MAX_VALUE - 1;
    public static final double HELP_LAYER = Double.MAX_VALUE;
    
    // Images
    public static final String IMAGES_DIRECTORY = "images/";
    public static final String BAR_MAGNET_IMAGE = IMAGES_DIRECTORY + "barMagnet.png";
    public static final String BAR_MAGNET_TRANSPARENT_IMAGE = IMAGES_DIRECTORY + "barMagnetTransparent.png";
    public static final String COIL1_FRONT_IMAGE = IMAGES_DIRECTORY + "coil1_front.png";
    public static final String COIL1_BACK_IMAGE = IMAGES_DIRECTORY + "coil1_back.png";
    public static final String COIL2_FRONT_IMAGE = IMAGES_DIRECTORY + "coil2_front.png";
    public static final String COIL2_BACK_IMAGE = IMAGES_DIRECTORY + "coil2_back.png";
    public static final String COMPASS_IMAGE = IMAGES_DIRECTORY + "compass.png";
    public static final String ELECTRONS1_BACK_IMAGE = IMAGES_DIRECTORY + "electrons1_back.png";
    public static final String ELECTRONS1_FRONT_IMAGE = IMAGES_DIRECTORY + "electrons1_front.png";
    public static final String ELECTRONS2_BACK_IMAGE = IMAGES_DIRECTORY + "electrons2_back.png";
    public static final String ELECTRONS2_FRONT_IMAGE = IMAGES_DIRECTORY + "electrons2_front.png";
    public static final String FIELD_PROBE_IMAGE = IMAGES_DIRECTORY + "fieldProbe.png";
    public static final String LIGHTBULB_IMAGE = IMAGES_DIRECTORY + "lightBulb.png";
    public static final String LIGHT_EMISSION_IMAGE = IMAGES_DIRECTORY + "lightEmission.png";
    public static final String VOLTMETER_IMAGE = IMAGES_DIRECTORY + "voltMeter.png";

    // Dimensions
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    public static final int CONTROL_PANEL_MIN_WIDTH = 150;

    // Colors
    public static final Color APPARATUS_BACKGROUND = Color.WHITE;
    public static final Color LABEL_COLOR = Color.BLACK;

    // Fonts
    public static final Font LABEL_FONT = new Font( "SansSerif", Font.PLAIN, 18 );
    
    // Minimums & Maximums
    public static final double MAGNET_STRENGTH_MIN = 100;
    public static final double MAGNET_STRENGTH_MAX = 999;
    public static final int MIN_PICKUP_LOOPS = 1;
    public static final int MAX_PICKUP_LOOPS = 2;
    public static final int MIN_SOURCE_LOOPS = 1;
    public static final int MAX_SOURCE_LOOPS = 4;
    public static final double MAX_EMF = 3.0E7;

    // Enables "Hollywood" versions of various model elements.
    public static final boolean HOLLYWOOD_MAGNET = false;
    public static final boolean HOLLYWOOD_COMPASS = false;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private FaradayConfig() {}
}