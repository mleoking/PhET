// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

//TODO should these be moved to LGColors, LGConstants, etc?
/**
 * Constants related to the game.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameConstants {

    public static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 40 );
    public static final Color TITLE_COLOR = Color.BLACK;
    public static final Font BUTTON_FONT = new PhetFont( Font.BOLD, 22 );
    public static final PhetFont EQUATION_FONT = new PhetFont( Font.BOLD, 40 );
    public static final double FACE_DIAMETER = 240;
    public static final Color GIVEN_COLOR = Color.BLACK;
    public static final Color GUESS_COLOR = PhetColorScheme.RED_COLORBLIND;
    public static final Color CORRECT_ANSWER_COLOR = new Color( 0, 200, 0 ); // green
    public static final Color FACE_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow
    public static final double MANIPULATOR_DIAMETER = 0.85; // diameter of the manipulators, in model units
    public static final Color POINTS_COLOR = Color.BLACK;
    public static final PhetFont POINTS_FONT = new PhetFont( Font.BOLD, 36 );
}
