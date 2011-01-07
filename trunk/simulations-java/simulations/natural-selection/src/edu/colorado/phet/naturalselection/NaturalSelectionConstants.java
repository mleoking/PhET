// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * Global configuration constants for Natural Selection
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionConstants {

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
    public static final Color COLOR_ACCESSIBLE_CONTROL_PANEL = NaturalSelectionApplication.accessibleColor( COLOR_CONTROL_PANEL );

    // pedigree chart background color
    public static final Color COLOR_GENERATION_CHART = new Color( 0xBBD5C7 );


    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    // image size properties
    public static final double FULL_BUNNY_WIDTH = 212;
    public static final double FULL_BUNNY_HEIGHT = 244;
    public static final double WOLF_WIDTH = 490;
    public static final double BUNNY_SCALE = 0.25;

    public static final double SCALED_BUNNY_WIDTH = FULL_BUNNY_WIDTH * BUNNY_SCALE;

    public static final double SCALED_BUNNY_HEIGHT = FULL_BUNNY_WIDTH * BUNNY_SCALE;

    public static final String IMAGE_EQUATOR_ENVIRONMENT = "equator-environment.png";
    public static final String IMAGE_ARCTIC_ENVIRONMENT = "arctic-environment.png";

    public static final String IMAGE_SELECTION_WOLVES = "selection_wolves.png";
    public static final String IMAGE_SELECTION_FOOD = "selection_food.png";

    public static final String IMAGE_BIG_VANILLA_BUNNY = "big_bunny.png";
    public static final String IMAGE_BUNNY_COLOR_WHITE = "bunny_color_white.png";
    public static final String IMAGE_BUNNY_COLOR_BROWN = "bunny_color_brown.png";
    public static final String IMAGE_BUNNY_TEETH_SHORT = "bunny_teeth_short.png";
    public static final String IMAGE_BUNNY_TEETH_LONG = "bunny_teeth_long.png";
    public static final String IMAGE_BUNNY_TAIL_SHORT = "bunny_tail_short.png";
    public static final String IMAGE_BUNNY_TAIL_LONG = "bunny_tail_long.png";

    public static final String IMAGE_DISPLAY_BUNNY_WHITE = "bunny_white.png";
    public static final String IMAGE_DISPLAY_BUNNY_WHITE_BIG_TAIL = "bunny_white_big_tail.png";
    public static final String IMAGE_DISPLAY_BUNNY_WHITE_LONG_TEETH = "bunny_white_long_teeth.png";
    public static final String IMAGE_DISPLAY_BUNNY_WHITE_BIG_TAIL_LONG_TEETH = "bunny_white_big_tail_long_teeth.png";
    public static final String IMAGE_DISPLAY_BUNNY_BROWN = "bunny_brown.png";
    public static final String IMAGE_DISPLAY_BUNNY_BROWN_BIG_TAIL = "bunny_brown_big_tail.png";
    public static final String IMAGE_DISPLAY_BUNNY_BROWN_LONG_TEETH = "bunny_brown_long_teeth.png";
    public static final String IMAGE_DISPLAY_BUNNY_BROWN_BIG_TAIL_LONG_TEETH = "bunny_brown_big_tail_long_teeth.png";

    public static final String IMAGE_BACKGROUND_EQUATOR = "natural_selection_background_equator_2.png";
    public static final String IMAGE_BACKGROUND_ARCTIC = "natural_selection_background_arctic_2.png";
    public static final String IMAGE_SHRUB = "shrub.png";
    public static final String IMAGE_TREE = "tree.png";

    public static final String IMAGE_MUTATION_PANEL_LARGE = "mutation_large.png";
    public static final String IMAGE_MUTATION_PANEL_SMALL = "mutation_small.png";
    public static final String IMAGE_MUTATION_BUNNY = "mutation_very_large.png";

    public static final String IMAGE_WOLF = "wolf_2.png";

    public static final String IMAGE_TARGET = "target.png";
    public static final String IMAGE_CROSSHAIR = "crosshair.png";

    public static final String IMAGE_ZOOM_IN = "zoomIn.gif";
    public static final String IMAGE_ZOOM_OUT = "zoomOut.gif";

    // offsets to where the "base" of the tree or shrub should be in the image
    public static final Point2D IMAGE_SHRUB_OFFSET = new Point2D.Double( 0, 15 );
    public static final Point2D IMAGE_TREE_OFFSET = new Point2D.Double( 0, 20 );

    public static final String IMAGE_EARTH = "earth-v1.png";

    public static final String IMAGE_DETACH_ICON = "tear-20.png";
    public static final String IMAGE_CLOSE_ICON = "x-20.png";


    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Settings
    //----------------------------------------------------------------------------

    private static NaturalSelectionSettings instance = null;

    public static NaturalSelectionSettings getSettings() {
        if ( instance == null ) {
            instance = new NaturalSelectionSettings();
        }
        return instance;
    }

    public static void setSettings( NaturalSelectionSettings settings ) {
        instance = settings;
    }

}
