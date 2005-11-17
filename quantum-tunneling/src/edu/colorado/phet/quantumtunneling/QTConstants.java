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
    public static final boolean CLOCK_TIME_STEP_IS_CONSTANT = true;
    public static final boolean CLOCK_ENABLE_CONTROLS = true;
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final String FONT_NAME = "Lucida Sans";
    
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------
    
    public static final Stroke TOTAL_ENERGY_STROKE = new BasicStroke( 5f );
    public static final Stroke POTENTIAL_ENERGY_STROKE = new BasicStroke( 2f );
    public static final Stroke INCIDENT_WAVE_STROKE = new BasicStroke( 2f );
    public static final Stroke REFLECTED_WAVE_STROKE = new BasicStroke( 2f );
    public static final Stroke TRANSMITTED_WAVE_STROKE = new BasicStroke( 2f );
    public static final Stroke PROBABILITY_DENSITY_STROKE = new BasicStroke( 2f );
    
    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    public static final Color CANVAS_BACKGROUND = Color.WHITE;
    public static final Paint CHART_BACKGROUND = Color.WHITE;
    public static final Paint PLOT_BACKGROUND = Color.WHITE;
    public static final Paint TOTAL_ENERGY_PAINT = Color.GREEN;
    public static final Paint POTENTIAL_ENERGY_PAINT = new Color( 178, 25, 205 ); // purple
    public static final Paint INCIDENT_WAVE_PAINT = Color.RED;
    public static final Paint REFLECTED_WAVE_PAINT = Color.RED;
    public static final Paint TRANSMITTED_WAVE_PAINT = Color.RED;
    public static final Paint PROBABILITY_DENSITY_PAINT = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    public static final String IMAGE_ARROW_L2R = IMAGES_DIRECTORY + "arrowL2R.png";
    public static final String IMAGE_ARROW_R2L = IMAGES_DIRECTORY + "arrowR2L.png";
    
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
    // Charts
    //----------------------------------------------------------------------------
    
    public static final Range POSITION_RANGE = new Range( 0, 20 ); // nm
    public static final Range ENERGY_RANGE = new Range( -1, 10 ); // eV
    public static final Range WAVE_FUNCTION_RANGE = new Range( -2, 2 );
    public static final Range PROBABILITY_DENSITY_RANGE = new Range( 0, 4 );
    
}
