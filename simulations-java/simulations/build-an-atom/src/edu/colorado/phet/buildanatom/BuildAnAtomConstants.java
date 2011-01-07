/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class BuildAnAtomConstants {
    public static final Font WINDOW_TITLE_FONT = new PhetFont( 20, true );
    public static final Font ITEM_FONT = new PhetFont( 16, true );

    /* Not intended for instantiation. */
    private BuildAnAtomConstants() {}

    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------

    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;

    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------

    public static final String PROJECT_NAME = "build-an-atom";
    public static final String FLAVOR_NAME_BUILD_AN_ATOM = "build-an-atom";
    public static final String FLAVOR_NAME_ISOTOPES_AND_ATOMIC_MASS = "isotopes-and-atomic-mass";

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
    public static final Color CANVAS_BACKGROUND = new Color(255, 255, 153);

    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.BLACK;

    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
    public static final Color READOUT_BACKGROUND_COLOR = new Color( 222, 222, 222 );//color for the scale and charge-o-meter

    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------

}
