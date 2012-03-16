// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * GlaciersConstants is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersConstants {

    /* Not intended for instantiation. */
    private GlaciersConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    // displays alignment points for background image
    public static final boolean DEBUG_BACKGROUND_IMAGE_ALIGNMENT = false;
    
    // alignment points for background image. 
    // The artist should put a marker on the valley floor at these x locations.
    public static final double[] DEBUG_BACKGROUND_IMAGE_ALIGNMENT_X_VALUES = { 0, 10000, 70000 }; // meters
    
    // display ELA value at the lower right corner of the toolbox
    public static final boolean DEBUG_ELA_VALUE_VISIBLE = false;
    
    //----------------------------------------------------------------------------
    // Flags
    //----------------------------------------------------------------------------
    
    // if true, model is updated while dragging slider.
    // if false, model is not updated until slider is released.
    public static final boolean UPDATE_WHILE_DRAGGING_SLIDERS = true;
    
    // if true, tools dropped to the left of the headwall will snap to the headwall.
    public static final boolean SNAP_TOOLS_TO_HEADWALL = false;
    
    // English or metric units as the default?
    public static final boolean DEFAULT_TO_ENGLISH_UNITS = true;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "glaciers";
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final double CLOCK_DT = 1; // years
    public static final IntegerRange CLOCK_FRAME_RATE_RANGE = new IntegerRange( 1, 24, 4 ); // frames per second (years per second)
    public static final DecimalFormat CLOCK_DISPLAY_FORMAT = new DecimalFormat( "0" );
    public static final int CLOCK_DISPLAY_COLUMNS = 10;
    
    // Climate
    public static final DoubleRange TEMPERATURE_RANGE = new DoubleRange( 13, 20, 19 );  // temperature at sea level (degrees C)
    public static final DoubleRange SNOWFALL_RANGE = new DoubleRange( 0, 1.5, 0.93 ); // average snow accumulation (meters/year)
    
    //----------------------------------------------------------------------------
    // View
    //----------------------------------------------------------------------------
    
    // How y axis scaling is built into the background image
    public static final double Y_AXIS_SCALE_IN_IMAGE = 2.0;
    
    // model-view transform (MVT) parameters
    public static final double MVT_X_SCALE = 0.1; // scale x by this amount when going from model to view
    public static final double MVT_Y_SCALE = 0.1 * Y_AXIS_SCALE_IN_IMAGE; // scale y by this amount when going from model to view
    public static final double MVT_X_OFFSET = 0; // translate x by this amount when going from model to view
    public static final double MVT_Y_OFFSET = 0; // translate y by this amount when going from model to view
    public static final boolean MVT_FLIP_SIGN_X = false;
    public static final boolean MVT_FLIP_SIGN_Y = true;
    
    // maximum x coordinate for panning the zoomed view
    public static final double ZOOMED_VIEW_MAX_X = 84000; // meters
    
    // constant height of the birds-eye view, in pixels
    public static final double BIRDS_EYE_VIEW_HEIGHT = 75;
    
    // camera view scales
    public static final double BIRDS_EYE_CAMERA_VIEW_SCALE = 0.2 / Y_AXIS_SCALE_IN_IMAGE;
    public static final double ZOOMED_CAMERA_VIEW_SCALE = 1;
    
    // offset of upper-left corner of birds-eye viewport from top of headwall
    public static final Point2D BIRDS_EYE_VIEWPORT_OFFSET = new Point2D.Double( -4400, +1000 ); // meters
    
    // default x location of the zoomed viewport
    public static final double DEFAULT_ZOOMED_VIEWPORT_X = -3400;
    
    // where the glacier intersects the right edge of the zoomed viewport, percentage of zoomed viewport height
    public static final double ALIGNMENT_FACTOR_FOR_GLACIER_IN_ZOOMED_VIEWPORT = 0.25; // percent
    
    // y offset used to fake pitch (rotation about the horizontal axis) 
    public static final double PITCH_Y_OFFSET = 250; // meters
    
    // rotation angle used to fake yaw (rotation about the vertical axis)
    public static final double YAW_ROTATION = Math.toRadians( 30 ); // radians
    // x offset that corresponds to YAW_ROTATION, set via visual inspection
    public static final double YAW_X_OFFSET = 500; // meters TODO: compute as a function of YAW_ROTATION and PITCH_Y_OFFSET?
    
    public static final int TOOLTIPS_INITIAL_DELAY = 1500; // ms
    
    //----------------------------------------------------------------------------
    // Colors
    //----------------------------------------------------------------------------
    
    // color of the Piccolo canvases
    public static final Color BIRDS_EYE_CANVAS_COLOR = new Color( 99, 173, 255 ); // sky blue
    public static final Color ZOOMED_CANVAS_COLOR = BIRDS_EYE_CANVAS_COLOR;
    
    // color of ice
    public static final Color ICE_SURFACE_ABOVE_ELA_COLOR = Color.WHITE;
    public static final Color ICE_SURFACE_BELOW_ELA_COLOR = new Color( 217, 217, 217 );
    public static final Color ICE_CROSS_SECTION_COLOR = new Color( 207, 255, 255 ); // ice blue
    
    // snow patch below ELA
    public static final Color SNOW_PATCH_SURFACE_COLOR = ICE_SURFACE_ABOVE_ELA_COLOR;
    public static final Color SNOW_PATCH_CROSS_SECTION_COLOR = Color.LIGHT_GRAY;
    
    // ripples in ice surface
    public static final Color RIPPLE_ABOVE_ELA_COLOR = ICE_SURFACE_ABOVE_ELA_COLOR.darker();
    public static final Color RIPPLE_BELOW_ELA_COLOR = ICE_SURFACE_BELOW_ELA_COLOR.darker();
    
    // debris in and on ice
    public static final Color DEBRIS_COLOR = Color.BLACK;
    
    // color of ground below valley floor
    public static final Color UNDERGROUND_COLOR = new Color( 180, 158, 134 ); // tan
    
    // main control panel color
    public static final Color CONTROL_PANEL_BACKGROUND_COLOR = new Color( 219, 255, 224 ); // pale green
    
    // colors for climate aspects
    public static final Color GLACIAL_BUDGET_COLOR = Color.RED;
    public static final Color ACCUMULATION_COLOR = Color.BLUE;
    public static final Color ABLATION_COLOR = Color.GREEN.darker();
    public static final Color NEGATIVE_ABLATION_COLOR = Color.GREEN.darker();
    
    //----------------------------------------------------------------------------
    // Various components
    //----------------------------------------------------------------------------
    
    // Subpanels of the main control panel
    public static final Font SUBPANEL_TITLE_FONT = new PhetFont( Font.BOLD, 12 );
    public static final Font SUBPANEL_CONTROL_FONT = new PhetFont( Font.PLAIN, 12 );
    public static final Color SUBPANEL_BACKGROUND_COLOR = new Color( 82, 126, 90 ); // dark green
    public static final Color SUBPANEL_TITLE_COLOR = Color.WHITE;
    public static final Color SUBPANEL_CONTROL_COLOR = Color.WHITE;
    
    // Coordinate axes
    public static final DoubleRange ELEVATION_AXIS_RANGE = new DoubleRange( 0, 7000 ); // meters
    public static final int ELEVATION_AXIS_TICK_SPACING_METRIC = 250; // meters
    public static final int ELEVATION_AXIS_TICK_SPACING_ENGLISH = 1000; // feet
    public static final int DISTANCE_AXIS_TICK_SPACING_METRIC = 1000; // meters
    public static final int DISTANCE_AXIS_TICK_SPACING_ENGLISH = 5000; // feet
}
