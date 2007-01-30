/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;


/**
 * OTConstants is a collection of constants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OTConstants {

    /* Not intended for instantiation. */
    private OTConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Dimensions
    //----------------------------------------------------------------------------
    
    public static final Dimension APP_FRAME_SIZE = new Dimension( 1024, 768 );

    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 750, 750 );
   
    //----------------------------------------------------------------------------
    // Localization
    //----------------------------------------------------------------------------
    
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/OTStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    
    // The clock control area has a slider for choosing a clock "speed".
    // These are the clock steps that correspond to each speed setting.
    public static final double[] CLOCK_STEPS = {
        1, 2, 4
    };
       
    // Defaults
    public static final double DEFAULT_CLOCK_STEP = CLOCK_STEPS[ OTDefaults.CLOCK_INDEX ];
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    // NOTE: font sizes are configurable in the SimStrings file!
    
    // Default font properties
    public static final String DEFAULT_FONT_NAME = new JLabel( "PhET" ).getFont().getName();
    public static final int DEFAULT_FONT_STYLE = Font.BOLD;
    public static final int DEFAULT_FONT_SIZE = 16;
    
    public static final Font TIME_DISPLAY_FONT = new Font( DEFAULT_FONT_NAME, Font.BOLD, 16 );
    public static final Font TIME_UNITS_FONT = new Font( DEFAULT_FONT_NAME, Font.PLAIN, 16 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.WHITE;
    
    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.BLACK;
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
 
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );
    public static final Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );
    
    //----------------------------------------------------------------------------
    // Model
    //----------------------------------------------------------------------------
        
    //----------------------------------------------------------------------------
    // Ranges
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Features
    //----------------------------------------------------------------------------    
}
