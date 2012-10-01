// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;

/**
 * Colors used throughout this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGColors {

    public static final Color CANVAS = new Color( 255, 255, 225 );
    public static final Color INTERACTIVE_LINE = PhetColorScheme.RED_COLORBLIND;
    public static final Color SAVED_LINE_NORMAL = Color.GRAY;
    public static final Color SAVED_LINE_HIGHLIGHT = new Color( 0, 190, 0 );
    public static final Color Y_EQUALS_X = Color.BLUE;
    public static final Color Y_EQUALS_NEGATIVE_X = new Color( 0, 178, 178 );
    public static final Color SLOPE = new Color( 120, 245, 3 );
    public static final Color INTERCEPT = Color.YELLOW;
    public static final Color POINT_X1_Y1 = new Color( 255, 125, 255 );
    public static final Color POINT_X2_Y2 = POINT_X1_Y1;
    public static final Color SAVE_LINE_BUTTON = Color.WHITE;
    public static final Color ERASE_LINES_BUTTON = Color.WHITE;
    public static final Color RESET_ALL_BUTTON = Color.WHITE;
    public static final Color SPINNER_BUTTON_DISABLED = new Color( 190, 190, 190 );
    public static final Color SPINNER_BACKGROUND_DISABLED = new Color( 245, 245, 245 );
    public static final Color PLOTTED_POINT = Color.LIGHT_GRAY;

    public static final Color POINT_TOOL_FOREGROUND_NORMAL_COLOR = Color.BLACK;
    public static final Color POINT_TOOL_BACKGROUND_NORMAL_COLOR = Color.WHITE;
    public static final Color POINT_TOOL_FOREGROUND_HIGHLIGHT_COLOR = Color.WHITE;

    public static final Color STATIC_EQUATION_ELEMENT = Color.BLACK;

    public static final Color EQUATION_CONTROL_PANEL = new Color( 238, 238, 238 );
    public static final Color GRAPH_CONTROL_PANEL = new Color( 238, 238, 238 );

    public static final Color GAME_INSTRUCTION_COLORS = Color.YELLOW;
}
