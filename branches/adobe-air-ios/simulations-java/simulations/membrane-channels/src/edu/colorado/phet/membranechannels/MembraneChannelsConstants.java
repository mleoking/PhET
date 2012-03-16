// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class MembraneChannelsConstants {

    /* Not intended for instantiation. */
    private MembraneChannelsConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "membrane-channels";

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
    public static final Color SODIUM_COLOR = new Color( 0, 220, 0);
    public static final Color POTASSIUM_COLOR = new Color(0, 0, 240);

    // Colors to use when representing various membrane channels.
    public static final Color SODIUM_GATED_EDGE_COLOR = MembraneChannelsConstants.SODIUM_COLOR;
    public static final Color SODIUM_GATED_CHANNEL_COLOR = ColorUtils.darkerColor(SODIUM_GATED_EDGE_COLOR, 0.15);
    public static final Color SODIUM_LEAKAGE_EDGE_COLOR = ColorUtils.interpolateRBGA(MembraneChannelsConstants.SODIUM_COLOR, Color.YELLOW, 0.7);
    public static final Color SODIUM_LEAKAGE_CHANNEL_COLOR = ColorUtils.darkerColor(SODIUM_LEAKAGE_EDGE_COLOR, 0.15);
    public static final Color POTASSIUM_GATED_EDGE_COLOR = MembraneChannelsConstants.POTASSIUM_COLOR;
    public static final Color POTASSIUM_GATED_CHANNEL_COLOR = ColorUtils.brighterColor(POTASSIUM_GATED_EDGE_COLOR, 0.3);
    public static final Color POTASSIUM_LEAKAGE_EDGE_COLOR = ColorUtils.interpolateRBGA(MembraneChannelsConstants.POTASSIUM_COLOR, new Color(00, 200, 255), 0.7);
    public static final Color POTASSIUM_LEAKAGE_CHANNEL_COLOR = ColorUtils.darkerColor(POTASSIUM_LEAKAGE_EDGE_COLOR, 0.15);
}
