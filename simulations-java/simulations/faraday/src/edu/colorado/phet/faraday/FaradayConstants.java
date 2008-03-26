/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday;

import java.awt.Dimension;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;


/**
 * FaradayConstants contains global configuration values.
 * See modules for module-specific parameters.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayConstants {
   
    //----------------------------------------------------------------------------
    // Debugging switches
    //----------------------------------------------------------------------------
    
    public static final boolean DEBUG_PICKUP_COIL_EMF = false;
    public static final boolean DEBUG_ENABLE_SCALE_PANEL = false;
    
    //XXX for testing sample point strategies
    public static final boolean USED_VARIABLE_NUMBER_OF_PICKUP_COIL_SAMPLE_POINTS = true;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Clock parameters
    //----------------------------------------------------------------------------
    
    public static final double CLOCK_STEP = 1; // clock ticks
    public static final int CLOCK_FRAME_RATE = 25;  // frames per second
    public static final int CLOCK_DELAY = ( 1000 / FaradayConstants.CLOCK_FRAME_RATE ); // milliseconds
    public static final boolean CLOCK_TIME_STEP_IS_CONSTANT = true;
    public static final boolean CLOCK_ENABLE_CONTROLS = true;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    public static final String AC_POWER_SUPPLY_IMAGE = "acPowerSupply.png";
    public static final String AC_POWER_SUPPLY_ICON = "acPowerSupply_icon.png";
    public static final String AC_POWER_SUPPLY_ICON_SELECTED = "acPowerSupply_icon_selected.png";
    public static final String BAR_MAGNET_IMAGE = "barMagnet.png";
    public static final String BATTERY_IMAGE = "battery.png";
    public static final String BATTERY_ICON = "battery_icon.png";
    public static final String BATTERY_ICON_SELECTED = "battery_icon_selected.png";
    public static final String COMPASS_IMAGE = "compass.png";
    public static final String ELECTRON_FOREGROUND_IMAGE = "electron_foreground.png";
    public static final String ELECTRON_BACKGROUND_IMAGE = "electron_background.png";
    public static final String FAUCET_IMAGE = "faucet.png";
    public static final String FIELD_METER_IMAGE = "fieldMeter.png";
    public static final String LIGHTBULB_IMAGE = "lightbulb.png";
    public static final String LIGHTBULB_ICON = "lightbulb_icon.png";
    public static final String LIGHTBULB_ICON_SELECTED = "lightbulb_icon_selected.png";
    public static final String RESISTOR_IMAGE = "resistor.png";
    public static final String SLIDER_KNOB_IMAGE = "sliderKnob.png";
    public static final String SLIDER_KNOB_HIGHLIGHT_IMAGE = "sliderKnobHighlight.png";
    public static final String TURBINE_PIVOT_IMAGE = "turbinePivot.png";
    public static final String VOLTMETER_IMAGE = "voltmeter.png";
    public static final String VOLTMETER_ICON = "voltmeter_icon.png";
    public static final String VOLTMETER_ICON_SELECTED = "voltmeter_icon_selected.png";
    public static final String VOLTMETER_PROBE_BLACK_IMAGE = "voltmeterProbeBlack.png";
    public static final String VOLTMETER_PROBE_RED_IMAGE = "voltmeterProbeRed.png";
    public static final String WATER_WHEEL_IMAGE = "waterWheel.png";

    //----------------------------------------------------------------------------
    // Bar Magnet parameters
    //----------------------------------------------------------------------------
    
    public static final double BAR_MAGNET_STRENGTH_MAX = 300.0; // Gauss
    public static final double BAR_MAGNET_STRENGTH_MIN = 0; // Gauss

    //----------------------------------------------------------------------------
    // Electromagnet parameters
    //----------------------------------------------------------------------------
    
    public static final double ELECTROMAGNET_STRENGTH_MAX = 300.0; // Gauss
    public static final int ELECTROMAGNET_LOOPS_MAX = 4;
    public static final int ELECTROMAGNET_LOOPS_MIN = 1;
    public static final int ELECTROMAGNET_WIRE_WIDTH = 20;
    
    //----------------------------------------------------------------------------
    // Turbine parameters
    //----------------------------------------------------------------------------
    
    public static final double TURBINE_STRENGTH_MAX = 300.0; // Gauss
    public static final double TURBINE_STRENGTH_MIN = 0; // Gauss;
    
    //----------------------------------------------------------------------------
    // Compass Grid parameters
    //----------------------------------------------------------------------------
    
    public static final int GRID_SPACING_MAX = 100;
    public static final int GRID_SPACING_MIN = 35;
    public static final int GRID_SPACING = 40;
    public static final double GRID_NEEDLE_ASPECT_RATIO = 25.0/7.0; // tips:waist
    public static final int GRID_NEEDLE_WIDTH_MAX = 60;
    public static final int GRID_NEEDLE_WIDTH_MIN = 20;
    private static final int GRID_NEEDLE_WIDTH = 25;
    private static final int GRID_NEEDLE_HEIGHT = (int) ( GRID_NEEDLE_WIDTH / GRID_NEEDLE_ASPECT_RATIO );
    public static final Dimension GRID_NEEDLE_SIZE = new Dimension( GRID_NEEDLE_WIDTH, GRID_NEEDLE_HEIGHT );
    
    //----------------------------------------------------------------------------
    // Pickup Coil parameters
    //----------------------------------------------------------------------------
    
    public static final double MAX_PICKUP_EMF = 4.0E7; // volts
    public static final int MAX_PICKUP_LOOPS = 3;
    public static final int MIN_PICKUP_LOOPS = 1;
    private static final double MAX_PICKUP_LOOP_RADIUS = 150.0;
    private static final double MIN_PICKUP_LOOP_RADIUS = 68.0;
    public static final double MAX_PICKUP_LOOP_AREA = Math.PI * MAX_PICKUP_LOOP_RADIUS * MAX_PICKUP_LOOP_RADIUS;
    public static final double MIN_PICKUP_LOOP_AREA = Math.PI * MIN_PICKUP_LOOP_RADIUS * MIN_PICKUP_LOOP_RADIUS;
    public static final double DEFAULT_PICKUP_LOOP_AREA = MAX_PICKUP_LOOP_AREA / 2; 
    
    //----------------------------------------------------------------------------
    // Battery parameters 
    //----------------------------------------------------------------------------
    
    public static final double BATTERY_VOLTAGE_MAX = 10;  // volts
    public static final double BATTERY_AMPLITUDE_MAX = 1.0; // -1...1
    public static final double BATTERY_AMPLITUDE_MIN = -1.0; // -1...1

    //----------------------------------------------------------------------------
    // AC Power Supply parameters
    //----------------------------------------------------------------------------
    
    public static final double AC_VOLTAGE_MAX = 110.0;  // volts
    public static final double AC_MAXAMPLITUDE_MAX = 1.0;  // 0...1
    public static final double AC_MAXAMPLITUDE_MIN = 0.0;  // 0...1
    public static final double AC_FREQUENCY_MAX = 1.0;  // 0...1
    public static final double AC_FREQUENCY_MIN = 0.05;  // 0...1

    //----------------------------------------------------------------------------
    // Thresholds
    //----------------------------------------------------------------------------
    
    /* Compass grid needles with B-field magnitude below this value are not drawn. */
    public static final double GRID_BFIELD_THRESHOLD = 0.02; // Gauss
    /* Absolute current amplitude below this value is treated as zero. */
    public static final double CURRENT_AMPLITUDE_THRESHOLD = 0.001;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private FaradayConstants() {}
}