/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.text.DecimalFormat;

import org.jfree.data.Range;

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.color.BlackColorScheme;


/**
 * BSConstants is a collection of constants.
 * Modify these at your peril.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSConstants {

    /* Not intended for instantiation. */
    private BSConstants() {}
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    
    //----------------------------------------------------------------------------
    // Localization
    //----------------------------------------------------------------------------
    
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/BSStrings";

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
    public static final Font AXIS_LABEL_FONT = new Font( BSConstants.FONT_NAME, Font.PLAIN, 16 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------
    
    public static final Stroke EIGENSTATE_NORMAL_STROKE = new BasicStroke( 1f );
    public static final Stroke EIGENSTATE_HILITE_STROKE = new BasicStroke( 2f );
    public static final Stroke EIGENSTATE_SELECTION_STROKE = new BasicStroke( 2f );
    public static final Stroke POTENTIAL_ENERGY_STROKE = new BasicStroke( 2f );
    public static final Stroke REAL_STROKE = new BasicStroke( 1f );
    public static final Stroke IMAGINARY_STROKE = new BasicStroke( 1f );
    public static final Stroke MAGNITUDE_STROKE = new BasicStroke( 1f );
    public static final Stroke PROBABILITY_DENSITY_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    public static final Color CANVAS_BACKGROUND = new Color( 240, 240, 240 );
    
    // The default color scheme
    public static final BSColorScheme COLOR_SCHEME = new BlackColorScheme();
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
    public static final String IMAGE_PAUSE = IMAGES_DIRECTORY + "pauseButton.gif";
    public static final String IMAGE_PLAY = IMAGES_DIRECTORY + "playButton.gif";
    public static final String IMAGE_STEP = IMAGES_DIRECTORY + "stepButton.gif";
    public static final String IMAGE_RESTART = IMAGES_DIRECTORY + "restartButton.gif";
    public static final String IMAGE_ZOOM_IN = IMAGES_DIRECTORY + "zoomIn.gif";
    public static final String IMAGE_ZOOM_OUT = IMAGES_DIRECTORY + "zoomOut.gif";

    //----------------------------------------------------------------------------
    // Ranges
    //----------------------------------------------------------------------------
    
    // Energy
    public static final Range ENERGY_RANGE = new Range( -15, 5 ); // eV
    public static final double ENERGY_TICK_SPACING = 2.5;
    public static final DecimalFormat ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    
    // Wave Function
    public static final Range WAVE_FUNCTION_RANGE = new Range( -2, 2 );
    
    // Position
    public static final Range POSITION_RANGE = new Range( -4, 4 ); // nm
    public static final double POSITION_TICK_SPACING = 1; // nm
    public static final DecimalFormat POSITION_TICK_FORMAT = new DecimalFormat( "0" );
    
    // Number of wells
    public static final int MIN_NUMBER_OF_WELLS = 1;
    public static final int MAX_NUMBER_OF_WELLS = 10;
    public static final int DEFAULT_NUMBER_OF_WELLS = 2;
    
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
    public static final boolean SHOW_HORIZONTAL_GRIDLINES = false;
    
    public static final double PIXELS_PER_SAMPLE_POINT = 1;
    
    //----------------------------------------------------------------------------
    // Particle
    //----------------------------------------------------------------------------
    
    // Mass
    public static final double ELECTRON_MASS = 5.68;
    public static final double MIN_MASS = 1; // x ELECTRON_MASS
    public static final double MAX_MASS = 10; // x ELECTRON_MASS
    public static final double DEFAULT_MASS = 1; // x ELECTRON_MASS
    
    //----------------------------------------------------------------------------
    // Potentials
    //----------------------------------------------------------------------------

    // Ranges...
    
    public static final double MIN_WELL_SEPARATION = 0.05; // nm
    
    public static final double MIN_WELL_WIDTH = 0.1;
    public static final double MAX_WELL_WIDTH = 8;
    
    public static final double MIN_WELL_SPACING = 0.3;
    public static final double MAX_WELL_SPACING = 4;
    
    public static final double MIN_WELL_DEPTH = 0;
    public static final double MAX_WELL_DEPTH = 20;

    public static final double MIN_WELL_OFFSET = -15;
    public static final double MAX_WELL_OFFSET = 5;
    
    public static final double MIN_WELL_ANGULAR_FREQUENCY = 1; // x 10^15
    public static final double MAX_WELL_ANGULAR_FREQUENCY = 15; // x 10^15
    
    // Defaults...
    
    // Common to all well types...
    public static final double DEFAULT_WELL_CENTER = 0;
    
    // Coulomb well...
    public static final double DEFAULT_COULOMB_OFFSET = 0;
    public static final double DEFAULT_COULOMB_SPACING = 1.2;
    
    // Harmonic Oscillator...
    public static final double DEFAULT_OSCILLATOR_OFFSET = 0;
    public static final double DEFAULT_OSCILLATOR_ANGULAR_FREQUENCY = 2; // x 10^15;
    
    // Square well...
    public static final double DEFAULT_SQUARE_OFFSET = 0;
    public static final double DEFAULT_SQUARE_WIDTH = 0.5;
    public static final double DEFAULT_SQUARE_DEPTH = 10;
    public static final double DEFAULT_SQUARE_SPACING = 1.2;
    
    // Asymmetric well...
    public static final double DEFAULT_ASYMMETRIC_OFFSET = 0;
    public static final double DEFAULT_ASYMMETRIC_WIDTH = 0.5;
    public static final double DEFAULT_ASYMMETRIC_DEPTH = 10;
    
    //----------------------------------------------------------------------------
    // Miscellaneous
    //----------------------------------------------------------------------------
    
    public static final double KEE = 1.44; // ke^2, eV nm
}
