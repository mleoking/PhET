// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common;

import java.awt.Color;

/**
 * Colors used throughout this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGColors {

    public static final Color CANVAS = new Color( 255, 255, 225 );
    public static final Color INTERACTIVE_LINE = Color.BLACK;
    public static final Color SAVED_LINE_NORMAL = new Color( 160, 160, 160 );
    public static final Color SAVED_LINE_HIGHLIGHT = new Color( 0, 0, 255 );
    public static final Color Y_EQUALS_X = new Color( 16, 178, 15 );
    public static final Color Y_EQUALS_NEGATIVE_X = Y_EQUALS_X;
    public static final Color SLOPE = new Color( 117, 217, 255 );
    public static final Color POINT_X1_Y1 = new Color( 255, 125, 255 );
    public static final Color POINT_X2_Y2 = Color.ORANGE;
    public static final Color POINT_1 = Color.RED; //TODO use similar but different colors for the 3 points?
    public static final Color POINT_2 = Color.GREEN;
    public static final Color POINT_3 = Color.BLUE;
    public static final Color INTERCEPT = POINT_X1_Y1;
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

}
