// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

import java.awt.Image;

import javax.annotation.Resources;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

/**
 * Resources for the line-graphing project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGResources {

    public static final String PROJECT_NAME = "line-graphing";
    private static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    // Localized strings
    public static class Strings {

        public static final String ERASE_LINES = RESOURCES.getLocalizedString( "eraseLines" );
        public static final String LINES = RESOURCES.getLocalizedString( "lines" );
        public static final String POINT_TOOL = RESOURCES.getLocalizedString( "pointTool" );
        public static final String RISE_OVER_RUN = RESOURCES.getLocalizedString( "riseOverRun" );
        public static final String SAVE_LINE = RESOURCES.getLocalizedString( "saveLine" );
        public static final String GRAPH = RESOURCES.getLocalizedString( "graph" );

        public static final String SYMBOL_X = RESOURCES.getLocalizedString( "symbol.x" );
        public static final String SYMBOL_Y = RESOURCES.getLocalizedString( "symbol.y" );
        public static final String SYMBOL_SLOPE = RESOURCES.getLocalizedString( "symbol.slope" );
        public static final String SYMBOL_INTERCEPT = RESOURCES.getLocalizedString( "symbol.intercept" );

        public static final String TAB_INTRO = RESOURCES.getLocalizedString( "tab.intro" );
        public static final String TAB_GAME = RESOURCES.getLocalizedString( "tab.game" );
    }

    public static class Images {

        public static final Image Y_EQUALS_X_ICON = RESOURCES.getImage( "y_equals_x_icon.png" );
        public static final Image Y_EQUALS_NEGATIVE_X_ICON = RESOURCES.getImage( "y_equals_negative_x_icon.png" );
        public static final Image LINES_ICON = RESOURCES.getImage( "lines_icon.png" );
        public static final Image POINT_TOOL = RESOURCES.getImage( "point-tool.png" );

        public static final Image MINIMIZE_BUTTON = PhetCommonResources.getImage( "buttons/minimizeButton.png" );
        public static final Image MAXIMIZE_BUTTON = PhetCommonResources.getImage( "buttons/maximizeButton.png" );

        private static final int SPINNER_BUTTON_HEIGHT = 22;
        public static final Image SPINNER_DOWN_GRAY = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_down_gray.png" ), SPINNER_BUTTON_HEIGHT );
        public static final Image SPINNER_DOWN_PRESSED_GREEN = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_down_pressed_green.png" ), SPINNER_BUTTON_HEIGHT );
        public static final Image SPINNER_DOWN_PRESSED_YELLOW = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_down_pressed_yellow.png" ), SPINNER_BUTTON_HEIGHT );
        public static final Image SPINNER_DOWN_UNPRESSED_GREEN = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_down_unpressed_green.png" ), SPINNER_BUTTON_HEIGHT );
        public static final Image SPINNER_DOWN_UNPRESSED_YELLOW = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_down_unpressed_yellow.png" ), SPINNER_BUTTON_HEIGHT );
        public static final Image SPINNER_UP_GRAY = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_up_gray.png" ), SPINNER_BUTTON_HEIGHT );
        public static final Image SPINNER_UP_PRESSED_GREEN = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_up_pressed_green.png" ), SPINNER_BUTTON_HEIGHT );
        public static final Image SPINNER_UP_PRESSED_YELLOW = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_up_pressed_yellow.png" ), SPINNER_BUTTON_HEIGHT );
        public static final Image SPINNER_UP_UNPRESSED_GREEN = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_up_unpressed_green.png" ), SPINNER_BUTTON_HEIGHT );
        public static final Image SPINNER_UP_UNPRESSED_YELLOW = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "spinner_up_unpressed_yellow.png" ), SPINNER_BUTTON_HEIGHT );
    }
}
