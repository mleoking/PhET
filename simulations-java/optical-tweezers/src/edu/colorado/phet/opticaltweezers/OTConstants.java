/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

import edu.colorado.phet.common.util.DoubleRange;


/**
 * OTConstants is a collection of constants that configure global properties.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTConstants {

    /* Not intended for instantiation. */
    private OTConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    private static final String PROJECT = "optical-tweezers";
    
    public static final Dimension APP_FRAME_SIZE = new Dimension( 1024, 768 );

    //----------------------------------------------------------------------------
    // Properties files
    //----------------------------------------------------------------------------
    
    public static final String SIM_PROPERTIES_NAME = PROJECT + "/" + PROJECT;
    public static final String SIM_STRINGS_NAME = PROJECT + "/localization/" + PROJECT + "-strings";

    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    // NOTE: font sizes are configurable in the SimStrings file!
    
    // Default font properties
    public static final String DEFAULT_FONT_NAME = new JLabel( "PhET" ).getFont().getName();
    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    public static final int DEFAULT_FONT_SIZE = 16;
    
    public static final Font CONTROL_PANEL_TITLE_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 12 );
    public static final Font CONTROL_PANEL_CONTROL_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.PLAIN, 12 );
    
    public static final Font PLAY_AREA_TITLE_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 );
    public static final Font PLAY_AREA_CONTROL_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.PLAIN, 14 );
    
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

    public static final String IMAGES_DIRECTORY = PROJECT + "/images/";
    
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
    public static final String IMAGE_LASER_SIGN = IMAGES_DIRECTORY + "laserDangerSign.png";
 
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
