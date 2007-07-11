/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import java.awt.*;

import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;


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
    
    // Command line argument to enable developer-only features.
    public static final String DEVELOPER_ARG = "-dev";
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    // Default font properties
    public static final String DEFAULT_FONT_NAME = new JLabel( "PhET" ).getFont().getName();
    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    public static final int DEFAULT_FONT_SIZE = 16;
    
    public static final Font CONTROL_PANEL_TITLE_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 12 );
    public static final Font CONTROL_PANEL_CONTROL_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.PLAIN, 12 );
    
    public static final Font PLAY_AREA_TITLE_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    public static final Font PLAY_AREA_CONTROL_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.PLAIN, 16 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    // Vertical dashed line that appears in position histogram and play area to denote laser origin
    public static final Stroke ORIGIN_MARKER_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,6}, 0 ); // dashed

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.WHITE;
    
    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.BLACK;
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    // Default color for module tabs
    public static final Color SELECTED_TAB_COLOR = new Color( 215, 229, 255 );
    
    // Colors for the control panel & clock control panel backgrounds
    public static final Color CONTROL_PANEL_COLOR = new Color( 215, 229, 255 );
    
    // Ruler color
    public static final Color RULER_COLOR = new Color( 236, 225, 113, 150 ); // transparent yellow
    
    // Color of the origin marker that appears in position histogram and play area
    public static final Color ORIGIN_MARKER_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    public static final String IMAGE_CAMERA = "cameraIcon.png";
    public static final String IMAGE_LASER_SIGN = "laserDangerSign.png";
    public static final String IMAGE_PUSHPIN = "pushpin.png";
    public static final String IMAGE_ON_BUTTON = "onButton.png";
    public static final String IMAGE_OFF_BUTTON = "offButton.png";
    public static final String IMAGE_RULER = "rulerIcon.png";
    public static final String IMAGE_ZOOM_IN = "zoomIn.png";   
    public static final String IMAGE_ZOOM_OUT = "zoomOut.png";   
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );
    public static final Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );
    public static final Cursor LEFT_RIGHT_CURSOR = new Cursor( Cursor.W_RESIZE_CURSOR );
    public static final Cursor UP_DOWN_CURSOR = new Cursor( Cursor.N_RESIZE_CURSOR );
    
}
