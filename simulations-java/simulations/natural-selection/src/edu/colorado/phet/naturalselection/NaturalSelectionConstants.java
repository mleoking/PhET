/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * TemplateConstants is a collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NaturalSelectionConstants {

    /* Not intended for instantiation. */
    private NaturalSelectionConstants() {
    }

    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------

    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;

    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------

    public static final String PROJECT_NAME = "natural-selection";

    public static final int BUNNIES_DIE_WHEN_THEY_ARE_THIS_OLD = 5;
    public static final int BUNNIES_STERILE_WHEN_THIS_OLD = 2;

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
            new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 3}, 0 );

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------

    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 0x888EC8 );

    // Color of labels placed directly on the play area
    public static final Color CANVAS_LABELS_COLOR = Color.BLACK;

    // Generic transparent color
    public static final Color COLOR_TRANSPARENT = new Color( 0f, 0f, 0f, 0f );

    // control panel background color
    public static final Color COLOR_CONTROL_PANEL = new Color( 0xC9E5C6 );

    public static final Color COLOR_MUTATION_PANEL = new Color( 0xF8AF9F );

    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final double FULL_BUNNY_WIDTH = 212;
    public static final double FULL_BUNNY_HEIGHT = 244;

    public static final double BUNNY_SCALE = 0.25;

    public static final double SCALED_BUNNY_WIDTH = FULL_BUNNY_WIDTH * BUNNY_SCALE;
    public static final double SCALED_BUNNY_HEIGHT = FULL_BUNNY_WIDTH * BUNNY_SCALE;

    public static final String IMAGE_EQUATOR_ENVIRONMENT = "equator-environment.png";
    public static final String IMAGE_ARCTIC_ENVIRONMENT = "arctic-environment.png";


    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------

}
