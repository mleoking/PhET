// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common;

import java.awt.Image;

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
        public static final String HIDE_LINES = RESOURCES.getLocalizedString( "hideLines" );
        public static final String SLOPE = RESOURCES.getLocalizedString( "slope" );
        public static final String SLOPE_UNDEFINED = RESOURCES.getLocalizedString( "slopeUndefined" );
        public static final String SAVE_LINE = RESOURCES.getLocalizedString( "saveLine" );
        public static final String CHECK = RESOURCES.getLocalizedString( "check" );
        public static final String TRY_AGAIN = RESOURCES.getLocalizedString( "tryAgain" );
        public static final String SHOW_ANSWER = RESOURCES.getLocalizedString( "showAnswer" );
        public static final String NEXT = RESOURCES.getLocalizedString( "next" );

        public static final String SYMBOL_X = RESOURCES.getLocalizedString( "symbol.x" );
        public static final String SYMBOL_X1 = RESOURCES.getLocalizedString( "symbol.x1" );
        public static final String SYMBOL_Y = RESOURCES.getLocalizedString( "symbol.y" );
        public static final String SYMBOL_Y1 = RESOURCES.getLocalizedString( "symbol.y1" );
        public static final String SYMBOL_SLOPE = RESOURCES.getLocalizedString( "symbol.slope" );
        public static final String SYMBOL_INTERCEPT = RESOURCES.getLocalizedString( "symbol.intercept" );

        public static final String TAB_SLOPE_INTERCEPT = RESOURCES.getLocalizedString( "tab.slopeIntercept" );
        public static final String TAB_POINT_SLOPE = RESOURCES.getLocalizedString( "tab.pointSlope" );
        public static final String TAB_LINE_GAME = RESOURCES.getLocalizedString( "tab.lineGame" );

        public static final String POINT_XY = RESOURCES.getLocalizedString( "point.xy" );
        public static final String POINT_UNKNOWN = RESOURCES.getLocalizedString( "point.unknown" );
    }

    public static class Images {

        public static final Image MINIMIZE_BUTTON = PhetCommonResources.getImage( "buttons/minimizeButton.png" );
        public static final Image MAXIMIZE_BUTTON = PhetCommonResources.getImage( "buttons/maximizeButton.png" );
        public static final Image POINT_TOOL = RESOURCES.getImage( "point_tool.png" );
    }
}
