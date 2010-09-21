/* Copyright 2004, University of Colorado */

/*
 * CVS Info - 
 * Filename : $Source$
 * Branch : $Name$ 
 * Modified by : $Author$ 
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * ColorVisionConstants contains global configuration constants.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ColorVisionConstants {

    /* Not intended for instantiation */
    private ColorVisionConstants() {}

    public static final String PROJECT_NAME = "color-vision";
    
    // Clock constants
    private static final int CLOCK_FRAME_RATE = 25;  // frames per second
    public static final int CLOCK_DELAY = ( 1000 / CLOCK_FRAME_RATE ); // milliseconds
    public static final double CLOCK_DT = 1;
    
    // Images
    public static final String IMAGES_DIRECTORY = PROJECT_NAME + "/images/";
    public static final String HEAD_BACKGROUND_IMAGE = IMAGES_DIRECTORY + "headBackground.png";
    public static final String HEAD_FOREGROUND_IMAGE = IMAGES_DIRECTORY + "headForeground.png";
    public static final String SPECTRUM_IMAGE = IMAGES_DIRECTORY + "spectrum.png";
    public static final String SPOTLIGHT_IMAGE = IMAGES_DIRECTORY + "spotlight.png";
    public static final String SWITCH_ON_IMAGE = IMAGES_DIRECTORY + "wallSwitchOn.png";
    public static final String SWITCH_OFF_IMAGE = IMAGES_DIRECTORY + "wallSwitchOff.png";

    // Dimensions
    public static final int CONTROL_PANEL_MIN_WIDTH = 150;

    // Colors
    public static final Color APPARATUS_BACKGROUND = Color.BLACK;
    public static final Color LABEL_COLOR = Color.WHITE;

    // Fonts
    public static final Font LABEL_FONT = new PhetFont( Font.PLAIN, 18 );
}