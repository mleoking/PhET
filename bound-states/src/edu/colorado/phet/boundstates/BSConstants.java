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
    
    public static final Stroke TOTAL_ENERGY_STROKE = new BasicStroke( 2f );
    public static final Stroke EIGENSTATE_UNSELECTED_STROKE = new BasicStroke( 1f );
    public static final Stroke EIGENSTATE_SELECTED_STROKE = new BasicStroke( 2f );
    public static final Stroke EIGENSTATE_HIGHLIGHT_STROKE = new BasicStroke( 2f );
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
    
    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    public static final Color CANVAS_BACKGROUND = new Color( 240, 240, 240 );
    public static final Color CHART_BACKGROUND = Color.BLACK;
    public static final Color PLOT_BACKGROUND = Color.BLACK;
    public static final Color TOTAL_ENERGY_COLOR = new Color( 23, 172, 18 ); // dark green
    public static final Color EIGENSTATE_UNSELECTED_COLOR = TOTAL_ENERGY_COLOR;
    public static final Color EIGENSTATE_SELECTED_COLOR = Color.RED;
    public static final Color EIGENSTATE_HIGHLIGHT_COLOR = Color.YELLOW;
    public static final Color POTENTIAL_ENERGY_COLOR = new Color( 178, 25, 205 ); // purple
    public static final Color REAL_WAVE_COLOR = Color.RED;
    public static final Color IMAGINARY_WAVE_COLOR = new Color( 26, 135, 255 ); // bright blue
    public static final Color MAGNITUDE_WAVE_COLOR = Color.WHITE;
    public static final Color PROBABILITY_DENSITY_COLOR = Color.WHITE;
    public static final Color TICK_LABEL_COLOR = Color.BLACK;
    public static final Color TICK_MARK_COLOR = Color.BLACK;
    public static final Color GRIDLINES_COLOR = Color.DARK_GRAY;
    
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
    // Electron
    //----------------------------------------------------------------------------
    
    public static final double ELECTRON_MASS = 5.7;
    
    //----------------------------------------------------------------------------
    // Ranges
    //----------------------------------------------------------------------------
    
    // Energy
    public static final Range ENERGY_RANGE = new Range( 0, 5 ); // eV
    public static final double ENERGY_TICK_SPACING = 0.5;
    public static final DecimalFormat ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    
    // Wave Function
    public static final Range WAVE_FUNCTION_RANGE = new Range( 0, 1.2 );
    
    // Position
    public static final Range POSITION_RANGE = new Range( -5, 5 ); // nm
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
    public static final boolean SHOW_HORIZONTAL_GRIDLINES = false;
    
    public static final double PIXELS_PER_SAMPLE_POINT = 1;
}
