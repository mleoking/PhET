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

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jfree.data.Range;


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
    
    public static final double CLOCK_TIME_STEP = 1;
    public static final int CLOCK_FRAME_RATE = 25;  // frames per second
    public static final boolean CLOCK_ENABLE_CONTROLS = true;
    
    public static final double TIME_SCALE = 0.1;  // must be a 1/(power of 10)
    public static final DecimalFormat TIME_FORMAT = new DecimalFormat( "0.0" );
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final String FONT_NAME = "Lucida Sans";
    public static final Font AXIS_LABEL_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 16 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------
    
    public static final Stroke TOTAL_ENERGY_STROKE = new BasicStroke( 4f );
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
    
    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    public static final Color CANVAS_BACKGROUND = Color.WHITE;
    public static final Color CHART_BACKGROUND = Color.WHITE;
    public static final Color PLOT_BACKGROUND = Color.WHITE;
    public static final Color TOTAL_ENERGY_COLOR = Color.GREEN;
    public static final Color POTENTIAL_ENERGY_COLOR = new Color( 178, 25, 205 ); // purple
    public static final Color INCIDENT_REAL_WAVE_COLOR = Color.RED;
    public static final Color INCIDENT_IMAGINARY_WAVE_COLOR = Color.BLUE;
    public static final Color INCIDENT_MAGNITUDE_WAVE_COLOR = Color.BLACK;
    public static final Color INCIDENT_PHASE_WAVE_COLOR = Color.BLACK;
    public static final Color REFLECTED_REAL_WAVE_COLOR = INCIDENT_REAL_WAVE_COLOR;
    public static final Color REFLECTED_IMAGINARY_WAVE_COLOR = INCIDENT_IMAGINARY_WAVE_COLOR;
    public static final Color REFLECTED_MAGNITUDE_WAVE_COLOR = INCIDENT_MAGNITUDE_WAVE_COLOR;
    public static final Color REFLECTED_PHASE_WAVE_COLOR = INCIDENT_PHASE_WAVE_COLOR;
    public static final Color PROBABILITY_DENSITY_COLOR = Color.BLACK;
    public static final Color REGION_MARKER_COLOR = Color.BLACK;
    public static final Color TICK_LABEL_COLOR = Color.BLACK;
    public static final Color TICK_MARK_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
    public static final Cursor INTERACTIVE_CURSOR = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    public static final String IMAGE_ARROW_L2R = IMAGES_DIRECTORY + "arrowL2R.png";
    public static final String IMAGE_ARROW_L2R_SELECTED = IMAGES_DIRECTORY + "arrowL2RSelected.png";
    public static final String IMAGE_ARROW_R2L = IMAGES_DIRECTORY + "arrowR2L.png";
    public static final String IMAGE_ARROW_R2L_SELECTED = IMAGES_DIRECTORY + "arrowR2LSelected.png";
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
    public static final String IMAGE_LOOP_OFF = IMAGES_DIRECTORY + "loopOffButton.png";
    public static final String IMAGE_LOOP_ON = IMAGES_DIRECTORY + "loopOnButton.png";
    public static final String IMAGE_PAUSE = IMAGES_DIRECTORY + "pauseButton.gif";
    public static final String IMAGE_PLAY = IMAGES_DIRECTORY + "playButton.gif";
    public static final String IMAGE_RESTART = IMAGES_DIRECTORY + "restartButton.gif";
    public static final String IMAGE_STEP = IMAGES_DIRECTORY + "stepButton.gif";
    
    //----------------------------------------------------------------------------
    // Formats
    //----------------------------------------------------------------------------
    
    public static final double POSITION_STEP = 0.1;
    public static final String POSITION_FORMAT = "0.0";
    
    public static final double ENERGY_STEP = 0.01; 
    public static final String ENERGY_FORMAT = "0.00";
   
    //----------------------------------------------------------------------------
    // Wave Function constants
    //----------------------------------------------------------------------------
    
    public static final double PLANCKS_CONSTANT = 0.658;  // Planck's constant, in eV fs
    public static final double ELECTRON_MASS = 5.7;  // mass, in eV/c^2
    
    //----------------------------------------------------------------------------
    // Wave Packet
    //----------------------------------------------------------------------------
    
    // packet width
    public static final double MIN_PACKET_WIDTH = 0.1; // nm
    public static final double MAX_PACKET_WIDTH = 4.0; // nm
    public static final double DEFAULT_PACKET_WIDTH = 0.5; // nm
    
    // packet center
    public static final double MIN_PACKET_CENTER = 0; // nm
    public static final double MAX_PACKET_CENTER = 20.0; // nm
    public static final double DEFAULT_PACKET_CENTER = 1.5; // nm
    
    //----------------------------------------------------------------------------
    // Energies
    //----------------------------------------------------------------------------
    
    public static final double DEFAULT_TOTAL_ENERGY = .8; // eV
    public static final double DEFAULT_POTENTIAL_ENERGY = .5; // eV
    
    /* used to draw the total energy "band" */
    public static final double TOTAL_ENERGY_DEVIATION = .38; //  eV
    
    //----------------------------------------------------------------------------
    // Charts
    //----------------------------------------------------------------------------
    
    public static final Range POSITION_RANGE = new Range( 0, 20 ); // nm
    public static final Range ENERGY_RANGE = new Range( -1, 1 ); // eV
    public static final Range WAVE_FUNCTION_RANGE = new Range( -2, 2 );
    public static final Range PROBABILITY_DENSITY_RANGE = new Range( 0, 4 );
    
    public static final boolean SHOW_VERTICAL_GRIDLINES = false;
    public static final boolean SHOW_HORIZONTAL_GRIDLINES = true;
    
    public static final boolean USE_INTEGER_ENERGY_TICKS = false;
    
    public static final double POSITION_SAMPLE_STEP = 0.02; // position step between sample points
    
    //----------------------------------------------------------------------------
    // Drag Handles
    //----------------------------------------------------------------------------
    
    public static final boolean SHOW_ENERGY_VALUES = false;
}
