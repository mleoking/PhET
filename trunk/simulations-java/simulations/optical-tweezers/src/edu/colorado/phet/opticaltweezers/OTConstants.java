/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import java.awt.*;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * OTConstants is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTConstants {

    /* Not intended for instantiation. */
    private OTConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enabled debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "optical-tweezers";
    
    public static final String FLAVOR_OPTICAL_TWEEZERS = "optical-tweezers";
    public static final String FLAVOR_STRETCHING_DNA = "stretching-dna";
    public static final String FLAVOR_MOLECULAR_MOTORS = "molecular-motors";
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    // Default font properties
    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    public static final int DEFAULT_FONT_SIZE = 16;
    
    public static final Font CONTROL_PANEL_TITLE_FONT = new PhetFont( Font.BOLD, 12 );
    public static final Font CONTROL_PANEL_CONTROL_FONT = new PhetFont( Font.PLAIN, 12 );
    
    public static final Font PLAY_AREA_TITLE_FONT = new PhetFont( Font.BOLD, 16 );
    public static final Font PLAY_AREA_CONTROL_FONT = new PhetFont( Font.PLAIN, 16 );
    
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
    public static final Color SELECTED_TAB_COLOR = new Color( 150, 150, 255 ); // royal blue
    
    // Colors for the control panel & clock control panel backgrounds
    public static final Color CONTROL_PANEL_COLOR = new JPanel().getBackground(); // default panel background
    
    // Ruler color
    public static final Color RULER_COLOR = new Color( 236, 225, 113, 150 ); // transparent yellow
    
    // Color of the origin marker that appears in position histogram and play area
    public static final Color ORIGIN_MARKER_COLOR = Color.BLACK;
    
    // Colors of various vectors
    public static final Color TRAP_FORCE_COLOR = Color.RED;
    public static final Color FLUID_DRAG_FORCE_COLOR = new Color( 76, 255, 252 ); // blue
    public static final Color DNA_FORCE_COLOR = Color.GREEN;
    public static final Color ELECTRIC_FIELD_COLOR = new Color( 230, 57, 5 ); // dark red
    
    // Enzyme colors
    public static final Color ENZYME_A_OUTER_COLOR = new Color( 12, 119, 133, 80 ); // dark green
    public static final Color ENZYME_A_INNER_COLOR = new Color( 12, 119, 133, 200 );
    public static final Color ENZYME_A_TICK_COLOR = Color.WHITE;
    public static final Color ENZYME_B_OUTER_COLOR = new Color( 140, 31, 255, 80 ); // purple
    public static final Color ENZYME_B_INNER_COLOR = new Color( 140, 31, 255, 200 );
    public static final Color ENZYME_B_TICK_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    public static final String IMAGE_BEAM_ICON = "beamIcon.png";
    public static final String IMAGE_CAMERA = "cameraIcon.png";
    public static final String IMAGE_HISTOGRAM_ICON = "histogramIcon.png";
    public static final String IMAGE_LASER_BUTTON_OFF = "laserButtonOff.png";
    public static final String IMAGE_LASER_BUTTON_ON = "laserButtonOn.png";
    public static final String IMAGE_LASER_SIGN = "laserDangerSign.png";
    public static final String IMAGE_PUSHPIN = "pushpin.png";
    public static final String IMAGE_POTENTIAL_ENERGY_CHART_ICON = "potentialEnergyChartIcon.png";
    public static final String IMAGE_RULER = "rulerIcon.png";
    public static final String IMAGE_ZOOM_IN = "zoomIn.png";   
    public static final String IMAGE_ZOOM_OUT = "zoomOut.png";   
    
    //----------------------------------------------------------------------------
    // Icons
    //----------------------------------------------------------------------------
    
    // vector icon properties
    public static final int VECTOR_ICON_LENGTH = 25;
    public static final int VECTOR_ICON_TAIL_WIDTH = 3;
    public static final Dimension VECTOR_ICON_HEAD_SIZE = new Dimension( 10, 10 );
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor LEFT_RIGHT_CURSOR = new Cursor( Cursor.W_RESIZE_CURSOR );
    public static final Cursor UP_DOWN_CURSOR = new Cursor( Cursor.N_RESIZE_CURSOR );
    
}
