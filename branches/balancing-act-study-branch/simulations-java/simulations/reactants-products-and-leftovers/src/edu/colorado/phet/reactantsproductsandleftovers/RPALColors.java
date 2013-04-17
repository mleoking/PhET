// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Common colors used in this simulation.
 */
public class RPALColors {

    // Common colors
    private static final Color DARK_BLUE = new Color( 46, 107, 178 );
    private static final Color LIGHT_PURPLE = new Color( 210, 210, 255 );

    // Static colors
    public static final Color PLUS_SIGN_COLOR = DARK_BLUE;
    public static final Color ARROW_COLOR = DARK_BLUE;
    public static final Color HISTOGRAM_BAR_COLOR = DARK_BLUE;

    // Colors that we want to change dynamically.
    public static class ColorScheme {

        public final Color canvasBackground;
        public final Color beforeAfterBoxColor;
        public final Color gameInstructionsColor;
        public final Color bracketColor;

        public ColorScheme( Color canvasBackground, Color beforeAfterBoxColor, Color gameInstructionsColor, Color bracketColor ) {
            this.canvasBackground = canvasBackground;
            this.beforeAfterBoxColor = beforeAfterBoxColor;
            this.gameInstructionsColor = gameInstructionsColor;
            this.bracketColor = bracketColor;
        }
    }

    // Colors normally used in the simulation.
    public static final ColorScheme NORMAL_COLOR_SCHEME = new ColorScheme( LIGHT_PURPLE, Color.BLACK, Color.YELLOW, DARK_BLUE );
    // Colors used for producing photocopy-able worksheets.
    public static final ColorScheme WORKSHEET_COLOR_SCHEME = new ColorScheme( Color.WHITE, Color.WHITE, Color.BLACK, Color.BLACK );

    public static final Property<ColorScheme> COLOR_SCHEME = new Property<ColorScheme>( NORMAL_COLOR_SCHEME );
}