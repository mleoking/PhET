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
    public static final int WAIT_TIME = 50;

    // Images
    public static final String IMAGES_DIRECTORY = "images/";
    public static final String BAR_MAGNET_IMAGE = IMAGES_DIRECTORY + "barMagnet.png";
    public static final String BFIELD_IMAGE = IMAGES_DIRECTORY + "bField.png";
    public static final String LIGHTBULB_IMAGE = IMAGES_DIRECTORY + "lightBulb.png";
    public static final String LOOP_IMAGE = IMAGES_DIRECTORY + "loop.png";
    public static final String SOURCE_COIL_IMAGE = IMAGES_DIRECTORY + "sourceCoil.png";
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

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private FaradayConfig() {}
}