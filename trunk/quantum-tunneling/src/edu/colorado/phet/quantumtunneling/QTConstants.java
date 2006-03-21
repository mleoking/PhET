/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.text.DecimalFormat;

import org.jfree.data.Range;

import edu.colorado.phet.quantumtunneling.control.ZoomControl.ZoomSpec;


/**
 * QTConstants is a collection of constants.
 * Modify these at your peril.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTConstants {

    /* Not intended for instantiation. */
    private QTConstants() {}
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    // displayes a dialog with UI controls the Richardson algorithm
    public static final boolean RICHARDSON_CONTROLS_VISIBLE = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    
    //----------------------------------------------------------------------------
    // Localization
    //----------------------------------------------------------------------------
    
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/QTStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double CLOCK_STEP = 0.1; // fs, femtoseconds (model time)
    
    /* Controls how time is displayed, should match CLOCK_STEP precision */
    public static final DecimalFormat TIME_FORMAT = new DecimalFormat( "0.0" );
    
    /* Is the time display visible next to the clock controls? */
    public static final boolean TIME_DISPLAY_VISIBLE = true;
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final String FONT_NAME = "Lucida Sans";
    public static final Font AXIS_LABEL_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 16 );
    public static final Font DRAG_HANDLE_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    public static final Font TIME_DISPLAY_FONT = new Font( QTConstants.FONT_NAME, Font.BOLD, 22 );
    public static final Font TIME_UNITS_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 22 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------
    
    public static final Stroke TOTAL_ENERGY_SOLID_STROKE = new BasicStroke( 4f );
    public static final Stroke TOTAL_ENERGY_DASHED_STROKE = 
        new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5,5}, 0 );
    public static final Stroke POTENTIAL_ENERGY_STROKE = new BasicStroke( 2f );
    public static final Stroke INCIDENT_REAL_WAVE_STROKE = new BasicStroke( 1f );
    public static final Stroke INCIDENT_IMAGINARY_WAVE_STROKE = new BasicStroke( 1f );
    public static final Stroke INCIDENT_MAGNITUDE_WAVE_STROKE = new BasicStroke( 1f );
    public static final Stroke INCIDENT_PHASE_WAVE_STROKE = new BasicStroke( 1f );
    public static final Stroke REFLECTED_REAL_WAVE_STROKE = INCIDENT_REAL_WAVE_STROKE;
    public static final Stroke REFLECTED_IMAGINARY_WAVE_STROKE = INCIDENT_IMAGINARY_WAVE_STROKE;
    public static final Stroke REFLECTED_MAGNITUDE_WAVE_STROKE = INCIDENT_MAGNITUDE_WAVE_STROKE;
    public static final Stroke REFLECTED_PHASE_WAVE_STROKE = INCIDENT_PHASE_WAVE_STROKE;
    public static final Stroke PROBABILITY_DENSITY_STROKE = new BasicStroke( 1f );
    public static final Stroke REGION_MARKER_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5,5}, 0 );
    public static final Stroke DRAG_HANDLE_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    public static final Color CANVAS_BACKGROUND = new Color( 240, 240, 240 );
    public static final Color CHART_BACKGROUND = Color.BLACK;
    public static final Color PLOT_BACKGROUND = Color.BLACK;
    public static final Color TOTAL_ENERGY_COLOR = Color.GREEN;
    public static final Color POTENTIAL_ENERGY_COLOR = new Color( 178, 25, 205 ); // purple
    public static final Color INCIDENT_REAL_WAVE_COLOR = Color.RED;
    public static final Color INCIDENT_IMAGINARY_WAVE_COLOR = new Color( 26, 135, 255 ); // bright blue
    public static final Color INCIDENT_MAGNITUDE_WAVE_COLOR = Color.WHITE;
    public static final Color REFLECTED_REAL_WAVE_COLOR = INCIDENT_REAL_WAVE_COLOR;
    public static final Color REFLECTED_IMAGINARY_WAVE_COLOR = INCIDENT_IMAGINARY_WAVE_COLOR;
    public static final Color REFLECTED_MAGNITUDE_WAVE_COLOR = INCIDENT_MAGNITUDE_WAVE_COLOR;
    public static final Color PROBABILITY_DENSITY_COLOR = Color.WHITE;
    public static final Color REGION_MARKER_COLOR = Color.LIGHT_GRAY;
    public static final Color TICK_LABEL_COLOR = Color.BLACK;
    public static final Color TICK_MARK_COLOR = Color.BLACK;
    public static final Color GRIDLINES_COLOR = Color.DARK_GRAY;
    public static final Color DRAG_HANDLE_FILL_COLOR = Color.WHITE;
    public static final Color DRAG_HANDLE_STROKE_COLOR = Color.BLACK;
    public static final Color DRAG_HANDLE_TEXT_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    public static final String IMAGE_ARROW_L2R = IMAGES_DIRECTORY + "arrowL2R.png";
    public static final String IMAGE_ARROW_L2R_SELECTED = IMAGES_DIRECTORY + "arrowL2RSelected.png";
    public static final String IMAGE_ARROW_R2L = IMAGES_DIRECTORY + "arrowR2L.png";
    public static final String IMAGE_ARROW_R2L_SELECTED = IMAGES_DIRECTORY + "arrowR2LSelected.png";
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
    public static final String IMAGE_PAUSE = IMAGES_DIRECTORY + "pauseButton.gif";
    public static final String IMAGE_PLAY = IMAGES_DIRECTORY + "playButton.gif";
    public static final String IMAGE_RESTART = IMAGES_DIRECTORY + "restartButton.gif";
    public static final String IMAGE_STEP = IMAGES_DIRECTORY + "stepButton.gif";
    public static final String IMAGE_ZOOM_IN = IMAGES_DIRECTORY + "zoomIn.gif";
    public static final String IMAGE_ZOOM_OUT = IMAGES_DIRECTORY + "zoomOut.gif";
 
    //----------------------------------------------------------------------------
    // Ranges
    //----------------------------------------------------------------------------
    
    // Energy
    public static final Range ENERGY_RANGE = new Range( -1, 1 ); // eV
    public static final double ENERGY_TICK_SPACING = 0.5;
    public static final DecimalFormat ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    
    // Wave Function
    public static final ZoomSpec[] WAVE_FUNCTION_ZOOM_SPECS = {
                          /* range, tickSpacing, tickPattern */
            new ZoomSpec( new Range( -0.5, 0.5 ), 0.25, "0.00" ),
            new ZoomSpec( new Range( -1.0, 1.0 ), 0.5,  "0.0" ),
            new ZoomSpec( new Range( -1.5, 1.5 ), 0.5,  "0.0" ),
            new ZoomSpec( new Range( -2.0, 2.0 ), 1,    "0" ),
            new ZoomSpec( new Range( -2.5, 2.5 ), 1,    "0" ),
            new ZoomSpec( new Range( -3.0, 3.0 ), 1,    "0" )
    };
    public static final int DEFAULT_WAVE_FUNCTION_ZOOM_INDEX = 3;
    public static final Range DEFAULT_WAVE_FUNCTION_RANGE = WAVE_FUNCTION_ZOOM_SPECS[ DEFAULT_WAVE_FUNCTION_ZOOM_INDEX ].getRange();
    
    // Probability Density
    public static final ZoomSpec[] PROBABILITY_DENSITY_ZOOM_SPECS = {
                         /* range, tickSpacing, tickPattern */
            new ZoomSpec( new Range( 0, 0.25 ), 0.05, "0.00" ),
            new ZoomSpec( new Range( 0, 1 ),    0.25, "0.00" ),
            new ZoomSpec( new Range( 0, 2.25 ), 0.5,  "0.0" ),
            new ZoomSpec( new Range( 0, 4 ),    1,    "0" ),
            new ZoomSpec( new Range( 0, 6.25 ), 1,    "0" ),
            new ZoomSpec( new Range( 0, 9 ),    1,    "0" )
    };
    public static final int DEFAULT_PROBABILTY_DENSITY_ZOOM_INDEX = 3;
    public static final Range DEFAULT_PROBABILITY_DENSITY_RANGE = PROBABILITY_DENSITY_ZOOM_SPECS[ DEFAULT_PROBABILTY_DENSITY_ZOOM_INDEX ].getRange();
    
    // Position
    public static final Range POSITION_RANGE = new Range( -4, 8 ); // nm
    public static final double POSITION_TICK_SPACING = 1; // nm
    public static final DecimalFormat POSITION_TICK_FORMAT = new DecimalFormat( "0" );
    
    //----------------------------------------------------------------------------
    // Charts
    //----------------------------------------------------------------------------
    
    /**
     * Controls when and how JFreeChart is used.
     * true = use JFreeChart to do all chart elements (static and dynamic)
     * false = use JFreeChart to draw static elements, custom code for dynamic elements
     */
    public static final boolean JFREECHART_DYNAMIC = false;
    
    public static final boolean SHOW_VERTICAL_GRIDLINES = true;
    public static final boolean SHOW_HORIZONTAL_GRIDLINES = true;
    
    public static final double PIXELS_PER_SAMPLE_POINT_PLANE_WAVE = 1; 
    public static final double PIXELS_PER_SAMPLE_POINT_WAVE_PACKET = 3;
    
    //----------------------------------------------------------------------------
    // Wave Function constants
    //----------------------------------------------------------------------------
    
    public static final double HBAR = 0.658;  // Planck's constant, in eV fs
    public static final double MASS = 5.7;  // electron mass, in eV/c^2
    
    //----------------------------------------------------------------------------
    // Precisions & Formats
    //----------------------------------------------------------------------------
    
    public static final double POSITION_STEP = 0.1;
    public static final String POSITION_FORMAT = "0.0";
    
    public static final double ENERGY_STEP = 0.01; 
    public static final String ENERGY_FORMAT = "0.00";
    
    //----------------------------------------------------------------------------
    // Energies
    //----------------------------------------------------------------------------
    
    public static final double DEFAULT_TOTAL_ENERGY = .8; // eV
    public static final double DEFAULT_POTENTIAL_ENERGY = .5; // eV
    
    //----------------------------------------------------------------------------
    // Regions
    //----------------------------------------------------------------------------
    
    // This value must be > 0 and some integer multiple of POSITION_STEP.
    public static final double MIN_REGION_WIDTH = POSITION_STEP * 1;
    
    // Step potential
    public static final double DEFAULT_STEP_POSITION = 0;
    public static final double DEFAULT_STEP_ENERGY = DEFAULT_POTENTIAL_ENERGY;
    
    // Barrier potential
    public static final double DEFAULT_BARRIER_POSITION = 0;
    public static final double DEFAULT_BARRIER_WIDTH = 1;
    public static final double DEFAULT_SPACE_BETWEEN_BARRIERS = 1;
    public static final double DEFAULT_BARRIER_ENERGY = DEFAULT_POTENTIAL_ENERGY;
    
    //----------------------------------------------------------------------------
    // Wave Packet
    //----------------------------------------------------------------------------
    
    // packet width
    public static final double MIN_PACKET_WIDTH = 0.1; // nm
    public static final double MAX_PACKET_WIDTH = 4.0; // nm
    public static final double DEFAULT_PACKET_WIDTH = 0.5; // nm
    public static final double MEASURING_WIDTH = 0.1; // nm
    
    // packet center
    public static final double MIN_PACKET_CENTER = POSITION_RANGE.getLowerBound(); // nm
    public static final double MAX_PACKET_CENTER = POSITION_RANGE.getUpperBound(); // nm
    public static final double DEFAULT_PACKET_CENTER = -2; // nm
}
