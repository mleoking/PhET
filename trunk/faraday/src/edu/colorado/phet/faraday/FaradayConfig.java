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
import java.awt.Dimension;
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

    // Debugging
    public static final boolean ENABLE_DEVELOPER_CONTROLS = true;
    
    // Resource bundles
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/FaradayStrings";

    // Clock constants
    public static final double TIME_STEP = 1;
    public static final int FRAME_RATE = 10;  // frame per second
    public static final int WAIT_TIME = ( 1000 / FRAME_RATE );  // milliseconds

    // Layers
    public static final double DEBUG_LAYER = Double.MAX_VALUE - 1;
    public static final double HELP_LAYER = Double.MAX_VALUE;
    
    // Images
    private static final String IMAGES_DIRECTORY = "images/";
    public static final String BAR_MAGNET_IMAGE = IMAGES_DIRECTORY + "barMagnet.png";
    public static final String COMPASS_IMAGE = IMAGES_DIRECTORY + "compass.png";
    public static final String FIELD_METER_IMAGE = IMAGES_DIRECTORY + "fieldMeter.png";
    public static final String LIGHTBULB_IMAGE = IMAGES_DIRECTORY + "lightBulb.png";
    public static final String VOLTMETER_IMAGE = IMAGES_DIRECTORY + "voltMeter.png";

    // Dimensions
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    public static final int CONTROL_PANEL_MIN_WIDTH = 150;

    // Colors
    public static final Color LABEL_COLOR = Color.BLACK;

    // Fonts
    public static final Font LABEL_FONT = new Font( "SansSerif", Font.PLAIN, 18 );
    
    // EMF parameters
    public static final double MAX_EMF = 1.0E6; // volts
    
    // Magnet parameters (applicable to all magnet types)
    public static final double MAGNET_STRENGTH_MIN = 100; // Gauss
    public static final double MAGNET_STRENGTH_MAX = 300; // Gauss
    
    // Bar Magnet parameters
    public static final Dimension BAR_MAGNET_SIZE_MIN = new Dimension( 10, 10 );
    public static final Dimension BAR_MAGNET_SIZE_MAX = new Dimension( 500, 200 );
    public static final Dimension BAR_MAGNET_SIZE = new Dimension( 250, 50 );

    // Compass Grid parameters
    public static final int GRID_SPACING_MIN = 20;
    public static final int GRID_SPACING_MAX = 70;
    public static final int GRID_SPACING = 40;
    public static final Dimension GRID_NEEDLE_SIZE_MIN = new Dimension( 1, 4 );
    public static final Dimension GRID_NEEDLE_SIZE_MAX = new Dimension( 100, 50 );
    public static final Dimension GRID_NEEDLE_SIZE = new Dimension( 25, 7 );
    
    // Pickup Coil parameters
    public static final int MIN_PICKUP_LOOPS = 1;
    public static final int MAX_PICKUP_LOOPS = 3;
    
    // Source Coil parameters
    public static final int MIN_SOURCE_LOOPS = 1;
    public static final int MAX_SOURCE_LOOPS = 4;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private FaradayConfig() {}
}