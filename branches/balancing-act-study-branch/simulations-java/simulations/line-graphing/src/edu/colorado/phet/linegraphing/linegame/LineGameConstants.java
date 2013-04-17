// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Constants that are specific to the "Line Game" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameConstants {

    // fonts
    public static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 40 );
    public static final Font BUTTON_FONT = new PhetFont( Font.BOLD, 30 );
    public static final PhetFont INTERACTIVE_EQUATION_FONT = new PhetFont( Font.BOLD, 28 );
    public static final PhetFont STATIC_EQUATION_FONT = new PhetFont( Font.PLAIN, INTERACTIVE_EQUATION_FONT.getSize() );
    public static final PhetFont POINTS_AWARDED_FONT = new PhetFont( Font.BOLD, 36 );

    // colors
    public static final Color TITLE_COLOR = Color.BLACK;
    public static final Color ANSWER_COLOR = new Color( 0, 180, 0 ); // color of the correct answer
    public static final Color GUESS_COLOR = Color.BLACK; // color of the user's guess
    public static final Color FACE_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow
    public static final Color POINTS_AWARDED_COLOR = Color.BLACK;
    public static final Color BUTTON_COLOR = Color.YELLOW;

    // sizes
    public static final double FACE_DIAMETER = 120;
    public static final double MANIPULATOR_DIAMETER = 0.85; // diameter of the manipulators, in model units
    public static final double POINT_DIAMETER = 0.5; // diameter of the manipulators, in model units
    public static final double POINT_TOOL_SCALE = 0.80;
}
