/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday;

import java.awt.Color;
import java.awt.Dimension;


/**
 * FaradayConstants contains global configuration values.
 * See modules for module-specific parameters.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayConstants {
   
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "faraday";
    
    public static final String FLAVOR_FARADAY = "faraday";
    public static final String FLAVOR_MAGNET_AND_COMPASS = "magnet-and-compass";
    public static final String FLAVOR_MAGNETS_AND_ELECTROMAGNETS = "magnets-and-electromagnets";
    public static final String FLAVOR_GENERATOR = "generator";
    
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
    public static final String EARTH_IMAGE = "earth.png";
    public static final String ELECTRON_FOREGROUND_IMAGE = "electron_foreground.png";
    public static final String ELECTRON_BACKGROUND_IMAGE = "electron_background.png";
    public static final String FAUCET_IMAGE = "faucet.png";
    public static final String FIELD_METER_IMAGE = "fieldMeter.png";
    public static final String LIGHTBULB_BASE_IMAGE = "lightbulb-base.png";
    public static final String LIGHTBULB_CAP_IMAGE = "lightbulb-cap.png";
    public static final String LIGHTBULB_GLASS_IMAGE = "lightbulb-glass.png";
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
    public static final String VOLTMETER_PROBE_WHITE_IMAGE = "voltmeterProbeWhite.png";
    public static final String WATER_WHEEL_IMAGE = "waterWheel.png";

    //----------------------------------------------------------------------------
    // Bar Magnet parameters
    //----------------------------------------------------------------------------
    
    public static final Dimension BAR_MAGNET_SIZE = new Dimension( 250, 50 ); // these should match barMagnet.png
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
    // B-field (grid of compass needles) parameters
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
    
    public static final Color NORTH_COLOR = Color.RED;
    public static final Color SOUTH_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Pickup Coil parameters
    //----------------------------------------------------------------------------
    
    /*
     * This is the reference EMF for the pickup coil.
     * Making this value bigger will make electrons move further, light rays bigger, etc.
     * The easiest way to set this value is to go to the Generator module, 
     * set magnet strength=100% and loop area=100%, and turn the water faucet to full on.
     * Then verify that the electrons don't move so far that they look like they're
     * moving in the wrong direction.  If they are moving too far, increase this value.
     * If they are not moving far enough, decrease this value.
     * 
     * Also adjust these scaling factors in the modules:
     * LIGHTBULB_SCALE
     * VOLTMETER_SCALE
     * ELECTRON_SPEED_SCALE
     */
    public static final double PICKUP_REFERENCE_EMF = 5.0E7; // volts
    
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
    
    /* B-field needles with magnitude below this value are not drawn. */
    public static final double GRID_BFIELD_THRESHOLD = 0.02; // Gauss
    /* Absolute current amplitude below this value is treated as zero. */
    public static final double CURRENT_AMPLITUDE_THRESHOLD = 0.001;
    
    //----------------------------------------------------------------------------
    // Flags used to create special "study" versions of this sim
    //----------------------------------------------------------------------------
    
    public static final boolean HIDE_BFIELD_FEATURE = false; // hides the "Show Field" feature
    public static final boolean HIDE_ELECTRONS_FEATURE = false; // hides the "Show Electrons" feature
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private FaradayConstants() {}
}