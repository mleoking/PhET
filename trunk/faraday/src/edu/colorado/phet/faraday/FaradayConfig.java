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

import java.awt.Dimension;


/**
 * FaradayConfig contains global configuration values.
 * See FaradayStrings.properties for localized Strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradayConfig {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Resource bundles for localization.
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/FaradayStrings";

    // Clock constants
    public static final double TIME_STEP = 1;
    public static final int FRAME_RATE = 25;  // frames per second
    public static final int WAIT_TIME = ( 1000 / FRAME_RATE );  // milliseconds
    
    // Images
    private static final String IMAGES_DIRECTORY = "images/";
    public static final String AC_POWER_SUPPLY_IMAGE = IMAGES_DIRECTORY + "acPowerSupply.png";
    public static final String AC_POWER_SUPPLY_ICON = IMAGES_DIRECTORY + "acPowerSupply_icon.png";
    public static final String AC_POWER_SUPPLY_ICON_SELECTED = IMAGES_DIRECTORY + "acPowerSupply_icon_selected.png";
    public static final String BAR_MAGNET_IMAGE = IMAGES_DIRECTORY + "barMagnet.png";
    public static final String BATTERY_IMAGE = IMAGES_DIRECTORY + "battery.png";
    public static final String BATTERY_ICON = IMAGES_DIRECTORY + "battery_icon.png";
    public static final String BATTERY_ICON_SELECTED = IMAGES_DIRECTORY + "battery_icon_selected.png";
    public static final String COMPASS_IMAGE = IMAGES_DIRECTORY + "compass.png";
    public static final String ELECTRON_FOREGROUND_IMAGE = IMAGES_DIRECTORY + "electron_foreground.png";
    public static final String ELECTRON_BACKGROUND_IMAGE = IMAGES_DIRECTORY + "electron_background.png";
    public static final String FAUCET_IMAGE = IMAGES_DIRECTORY + "faucet.png";
    public static final String FIELD_METER_IMAGE = IMAGES_DIRECTORY + "fieldMeter.png";
    public static final String LIGHTBULB_IMAGE = IMAGES_DIRECTORY + "lightbulb.png";
    public static final String LIGHTBULB_ICON = IMAGES_DIRECTORY + "lightbulb_icon.png";
    public static final String LIGHTBULB_ICON_SELECTED = IMAGES_DIRECTORY + "lightbulb_icon_selected.png";
    public static final String SLIDER_KNOB_IMAGE = IMAGES_DIRECTORY + "sliderKnob.png";
    public static final String SLIDER_KNOB_HIGHLIGHT_IMAGE = IMAGES_DIRECTORY + "sliderKnobHighlight.png";
    public static final String TURBINE_PIVOT_IMAGE = IMAGES_DIRECTORY + "turbinePivot.png";
    public static final String VOLTMETER_IMAGE = IMAGES_DIRECTORY + "voltmeter.png";
    public static final String VOLTMETER_ICON = IMAGES_DIRECTORY + "voltmeter_icon.png";
    public static final String VOLTMETER_ICON_SELECTED = IMAGES_DIRECTORY + "voltmeter_icon_selected.png";
    public static final String WATER_WHEEL_IMAGE = IMAGES_DIRECTORY + "waterWheel.png";

    // Dimensions
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    public static final int CONTROL_PANEL_SPACER_HEIGHT = 15;

    // Bar Magnet parameters
    public static final double BAR_MAGNET_STRENGTH_MAX = 300.0; // Gauss
    public static final double BAR_MAGNET_STRENGTH_MIN = 0.50 * BAR_MAGNET_STRENGTH_MAX; // Gauss

    // Electromagnet parameters
    public static final double ELECTROMAGNET_STRENGTH_MAX = 300.0; // Gauss
    public static final int ELECTROMAGNET_LOOPS_MAX = 4;
    public static final int ELECTROMAGNET_LOOPS_MIN = 1;
    public static final int ELECTROMAGNET_WIRE_WIDTH = 20;
    public static final double ELECTROMAGNET_ASPECT_RATIO = 4/1;  // width:height
    
    // Turbine parameters
    public static final double TURBINE_STRENGTH_MAX = BAR_MAGNET_STRENGTH_MAX;
    public static final double TURBINE_STRENGTH_MIN = BAR_MAGNET_STRENGTH_MIN;
    
    // Compass Grid parameters
    public static final int GRID_SPACING_MAX = 100;
    public static final int GRID_SPACING_MIN = 35;
    public static final int GRID_SPACING = 40;
    public static final double GRID_NEEDLE_ASPECT_RATIO = 25.0/7.0; // tips:waist
    public static final int GRID_NEEDLE_WIDTH_MAX = 60;
    public static final int GRID_NEEDLE_WIDTH_MIN = 20;
    private static final int GRID_NEEDLE_WIDTH = 25;
    private static final int GRID_NEEDLE_HEIGHT = (int) ( GRID_NEEDLE_WIDTH / GRID_NEEDLE_ASPECT_RATIO );
    public static final Dimension GRID_NEEDLE_SIZE = new Dimension( GRID_NEEDLE_WIDTH, GRID_NEEDLE_HEIGHT );
    
    // Pickup Coil parameters
    public static final double MAX_PICKUP_EMF = 4.0E7; // volts
    public static final int MAX_PICKUP_LOOPS = 3;
    public static final int MIN_PICKUP_LOOPS = 1;
    private static final double MAX_PICKUP_LOOP_RADIUS = 125.0;
    public static final double MAX_PICKUP_LOOP_AREA = Math.PI * MAX_PICKUP_LOOP_RADIUS * MAX_PICKUP_LOOP_RADIUS;
    public static final double MIN_PICKUP_LOOP_AREA = 0.5 * MAX_PICKUP_LOOP_AREA;

    // Battery parameters 
    public static final double BATTERY_VOLTAGE_MAX = 10;  // volts
    public static final double BATTERY_AMPLITUDE_MAX = 1.0; // -1...1
    public static final double BATTERY_AMPLITUDE_MIN = -1.0; // -1...1

    // AC parameters
    public static final double AC_VOLTAGE_MAX = MAX_PICKUP_EMF;  // volts
    public static final double AC_MAXAMPLITUDE_MAX = 1.0;  // 0...1
    public static final double AC_MAXAMPLITUDE_MIN = 0.0;  // 0...1
    public static final double AC_FREQUENCY_MAX = 1.0;  // 0...1
    public static final double AC_FREQUENCY_MIN = 0.05;  // 0...1
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private FaradayConfig() {}
}