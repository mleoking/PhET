/* Copyright 2007, University of Colorado */

package edu.colorado.phet.gravityandorbits;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class GravityAndOrbitsConstants {

    /* Not intended for instantiation. */

    private GravityAndOrbitsConstants() {
    }

    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;

    public static final String PROJECT_NAME = "gravity-and-orbits";

    // Default font properties
    public static final int DEFAULT_FONT_STYLE = Font.PLAIN;
    public static final int DEFAULT_FONT_SIZE = 16;

    public static final Font CONTROL_PANEL_TITLE_FONT = new PhetFont( Font.BOLD, 12 );
    public static final Font CONTROL_PANEL_CONTROL_FONT = new PhetFont( Font.PLAIN, 12 );

    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = Color.BLACK;

    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.BLACK;

    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );
}