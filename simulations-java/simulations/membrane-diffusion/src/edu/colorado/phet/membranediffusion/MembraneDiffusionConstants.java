/* Copyright 2007, University of Colorado */

package edu.colorado.phet.membranediffusion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class MembraneDiffusionConstants {

    /* Not intended for instantiation. */
    private MembraneDiffusionConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "membrane-diffusion";

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

    public static final Stroke DASHED_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.BLACK;
    
    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.BLACK;
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Et Cetera
    //----------------------------------------------------------------------------

    // Colors to use when representing various atoms.
    public static final Color SODIUM_COLOR = new Color( 240, 0, 0);
    public static final Color POTASSIUM_COLOR = new Color(0, 0, 255);
}
