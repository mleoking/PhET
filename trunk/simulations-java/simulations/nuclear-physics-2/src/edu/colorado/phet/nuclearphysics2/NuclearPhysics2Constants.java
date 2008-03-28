/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;


/**
 * This class is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author John Blanco
 */
public class NuclearPhysics2Constants {

    /* Not intended for instantiation. */
    private NuclearPhysics2Constants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    // Default font properties
    public static final String DEFAULT_FONT_NAME = PhetDefaultFont.getDefaultFontName();
    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    public static final int DEFAULT_FONT_SIZE = 16;
    
    public static final Font CONTROL_PANEL_TITLE_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.BOLD, 12 );
    public static final Font CONTROL_PANEL_CONTROL_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.PLAIN, 12 );
    
    public static final Font PLAY_AREA_TITLE_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    public static final Font PLAY_AREA_CONTROL_FONT = new Font( NuclearPhysics2Constants.DEFAULT_FONT_NAME, Font.PLAIN, 16 );
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    public static final Stroke DASHED_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 0xffffcc );
    
    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.BLACK;
    
    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    
    // Default color for module tabs
    public static final Color SELECTED_TAB_COLOR = Color.ORANGE;
    
    // Color for the label used for the Polonium nucleus.
    public static final Color POLONIUM_LABEL_COLOR = Color.MAGENTA;
    
    // Color for label used for the Lead nucleus.
    public static final Color LEAD_LABEL_COLOR = new Color(0xc0ffff);
    
    // Color for label used for the Uranium 235 nucleus.
    public static final Color URANIUM_LABEL_COLOR = Color.GREEN;
    
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
}
