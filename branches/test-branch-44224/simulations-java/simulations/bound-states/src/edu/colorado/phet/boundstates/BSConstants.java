/* Copyright 2006-2008, University of Colorado */

package edu.colorado.phet.boundstates;

import java.awt.*;
import java.text.DecimalFormat;

import org.jfree.data.Range;

import edu.colorado.phet.boundstates.color.BSBlackColorScheme;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * BSConstants is a collection of constants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BSConstants {

    /* Not intended for instantiation. */
    private BSConstants() {}
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "bound-states";
    
    public static final String FLAVOR_BOUND_STATES = "bound-states";
    public static final String FLAVOR_COVALENT_BONDS = "covalent-bonds";
    public static final String FLAVOR_BAND_STRUCTURE = "band-structure";
    
    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    
    // The clock control area has a slider for choosing a clock "speed".
    // These are the clock steps (model time in fs) that correspond to each speed setting.
    public static final double[] CLOCK_STEPS = {
        0.01, 0.1, 1, 10
    };
    
    // One entry for each entry in CLOCK_STEPS
    public static final DecimalFormat[] CLOCK_FORMATS = {
        new DecimalFormat( "0.00" ), new DecimalFormat( "0.0" ), new DecimalFormat( "0" ), new DecimalFormat( "0" )
    };
    
    // Defaults
    public static final int DEFAULT_CLOCK_INDEX = 0;
    public static final double DEFAULT_CLOCK_STEP = CLOCK_STEPS[ DEFAULT_CLOCK_INDEX ];
    public static final DecimalFormat DEFAULT_CLOCK_FORMAT = CLOCK_FORMATS[ DEFAULT_CLOCK_INDEX ];
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final Font AXIS_LABEL_FONT = new PhetFont( Font.PLAIN, 20 );
    public static final Font AXIS_TICK_LABEL_FONT = new PhetFont( Font.PLAIN, 14 );
    public static final Font HILITE_ENERGY_FONT = new PhetFont( Font.PLAIN, 16 );
    public static final Font WAVE_FUNCTION_EQUATION_FONT = new PhetFont( Font.PLAIN, 22 );
    public static final Font TIME_DISPLAY_FONT = new PhetFont( Font.BOLD, 22 );
    public static final Font TIME_UNITS_FONT = new PhetFont( Font.PLAIN, 22 );
    public static final Font DRAG_HANDLE_FONT = new PhetFont( Font.PLAIN, 16 );
    
    //----------------------------------------------------------------------------
    // Special characters
    //----------------------------------------------------------------------------
    
    public static final char LOWERCASE_PSI = '\u03c8';
    public static final char UPPERCASE_PSI = '\u03a8';
    public static final char LOWERCASE_PI = '\u03c0';
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------
    
    public static final Stroke EIGENSTATE_NORMAL_STROKE = new BasicStroke( 1f );
    public static final Stroke EIGENSTATE_HILITE_STROKE = new BasicStroke( 2f );
    public static final Stroke EIGENSTATE_SELECTION_STROKE = new BasicStroke( 2f );
    public static final Stroke POTENTIAL_ENERGY_STROKE = new BasicStroke( 3f );
    public static final Stroke REAL_STROKE = new BasicStroke( 2f );
    public static final Stroke IMAGINARY_STROKE = new BasicStroke( 2f );
    public static final Stroke MAGNITUDE_STROKE = new BasicStroke( 2f );
    public static final Stroke PROBABILITY_DENSITY_STROKE = new BasicStroke( 2f );
    public static final Stroke HILITE_STROKE = new BasicStroke( 1f );
    public static final Stroke DRAG_HANDLE_STROKE = new BasicStroke( 1f );
    public static final Stroke DRAG_HANDLE_MARKERS_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5,5}, 0 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 220, 220, 220 );
    
    // Color of the stopwatch background
    public static final Color STOPWATCH_BACKGROUND = Color.LIGHT_GRAY;
    
    // The default color scheme
    public static final BSColorScheme COLOR_SCHEME = new BSBlackColorScheme();
    
    // Transparency (alpha) of the background for the hilited eigenstate's value
    public static final int HILITE_VALUE_BACKGROUND_ALPHA = 175;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGE_ZOOM_IN = "zoomIn.gif";
    public static final String IMAGE_ZOOM_OUT = "zoomOut.gif";

    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );
    public static final Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );
    
    //----------------------------------------------------------------------------
    // Ranges
    //----------------------------------------------------------------------------
    
    // Energy range is set per potential type in subclasses of BSAbstractModuleSpec.
    
    // Wave Function
    public static final Range WAVE_FUNCTION_RANGE = new Range( -1.55, +1.55 );
    public static final double WAVE_FUNCTION_TICK_SPACING = 0.5;
    public static final DecimalFormat WAVE_FUNCTION_TICK_FORMAT = new DecimalFormat( "0.0" );
    
    // Probability Density
    public static final Range PROBABILITY_DENSITY_RANGE = new Range( 0, 1.55 );
    public static final double PROBABILITY_DENSITY_TICK_SPACING = 0.5;
    public static final DecimalFormat PROBABILITY_DENSITY_TICK_FORMAT = new DecimalFormat( "0.0" );
    
    // Average Probability Density
    public static final Range AVERAGE_PROBABILITY_DENSITY_RANGE = new Range( 0, 1.55 );
    public static final double AVERAGE_PROBABILITY_DENSITY_TICK_SPACING = 0.5;
    public static final DecimalFormat AVERAGE_PROBABILITY_DENSITY_TICK_FORMAT = new DecimalFormat( "0.0" );
    
    // Position
    // NOTE! If you change the position range, you may need to change SCHMIDT_LEE_SAMPLE_POINTS.
    public static final Range POSITION_MODEL_RANGE = new Range( -3.5, 3.5 ); // nm
    public static final Range POSITION_VIEW_RANGE = POSITION_MODEL_RANGE;
    public static final double POSITION_TICK_SPACING = 1; // nm
    public static final DecimalFormat POSITION_TICK_FORMAT = new DecimalFormat( "0" );
    
    // Superposition coefficients
    public static final double COEFFICIENT_MIN = 0.00;
    public static final double COEFFICIENT_MAX = 1.00;
    public static final double COEFFICIENT_STEP = 0.01;
    public static final String COEFFICIENT_PATTERN = "0.00";
    public static final DecimalFormat COEFFICIENT_FORMAT = new DecimalFormat( COEFFICIENT_PATTERN );
    
    //----------------------------------------------------------------------------
    // Charts
    //----------------------------------------------------------------------------
    
    /**
     * Controls when and how JFreeChart is used.
     * true = use JFreeChart to do all chart elements (static and dynamic).
     * false = use JFreeChart to draw static elements, custom code for dynamic elements.
     */
    public static final boolean JFREECHART_DYNAMIC = false;
    
    public static final boolean SHOW_VERTICAL_GRIDLINES = true;
    public static final boolean SHOW_HORIZONTAL_GRIDLINES = false;
    
    //----------------------------------------------------------------------------
    // Lattice size controls
    //----------------------------------------------------------------------------
    
    /**
     * Determines the number of sample points used to draw the potentials. 
     * The actual number of sample points will vary with the width (in pixels) 
     * of the charts.  This guarantees that the rendering quality will be the
     * same resolution for all simulation window sizes.
     */
    public static final double PIXELS_PER_POTENTIAL_SAMPLE_POINT = 1;
    
    /**
     * Determines the number of sample points used to calculate eigenstates
     * and wave functions. Because of how the Schmidt-Lee algorithm behaves,
     * we don't want this to vary with the size of the simulation window.
     * Schmidt-Lee will fail if dx gets too small, so if you change the 
     * position range, you may need to change this value.
     */
    public static final int SCHMIDT_LEE_SAMPLE_POINTS = 1350;
    
    /**
     * Determines the number of samples points used to calculate the 
     * wave function when using the analytic solver for Coulomb wells.
     */
    public static final int COULOMB_ANALYTIC_SAMPLE_POINTS = SCHMIDT_LEE_SAMPLE_POINTS;
    
    //----------------------------------------------------------------------------
    // Miscellaneous
    //----------------------------------------------------------------------------
    
    // Energy must be at least this close to eigenstate to be hilited.
    public static final double HILITE_ENERGY_THRESHOLD = 1; // eV
    
    public static final DecimalFormat MAGNIFICATION_FORMAT = new DecimalFormat( "0.#" );
    
    public static final double ELECTRON_MASS = 5.68; // eV/c^2
    public static final double HBAR = 0.658;
    public static final double KE2 = 1.44; // ke^2, eV nm
}
