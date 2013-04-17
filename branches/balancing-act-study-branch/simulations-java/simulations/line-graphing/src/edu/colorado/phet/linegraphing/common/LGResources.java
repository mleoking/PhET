// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources for this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGResources {

    public static final String PROJECT_NAME = "line-graphing";
    private static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );
    private static final PhetResources COMMON_RESOURCE = PhetCommonResources.getInstance();

    // Localized strings
    public static class Strings {

        public static final String ERASE_LINES = RESOURCES.getLocalizedString( "eraseLines" );
        public static final String HIDE_LINES = RESOURCES.getLocalizedString( "hideLines" );
        public static final String SLOPE = RESOURCES.getLocalizedString( "slope" );
        public static final String SLOPE_UNDEFINED = RESOURCES.getLocalizedString( "slopeUndefined" );
        public static final String SAVE_LINE = RESOURCES.getLocalizedString( "saveLine" );
        public static final String CHECK = COMMON_RESOURCE.getLocalizedString( "Games.check" );
        public static final String TRY_AGAIN = COMMON_RESOURCE.getLocalizedString( "Games.tryAgain" );
        public static final String SHOW_ANSWER = COMMON_RESOURCE.getLocalizedString( "Games.showAnswer" );
        public static final String NEXT = COMMON_RESOURCE.getLocalizedString( "Games.next" );
        public static final String GRAPH_THE_LINE = RESOURCES.getLocalizedString( "graphTheLine" );
        public static final String MAKE_THE_EQUATION = RESOURCES.getLocalizedString( "makeTheEquation" );
        public static final String PUT_POINTS_ON_LINE = RESOURCES.getLocalizedString( "putPointsOnLine" );
        public static final String SET_THE_SLOPE = RESOURCES.getLocalizedString( "setTheSlope" );
        public static final String SET_THE_Y_INTERCEPT = RESOURCES.getLocalizedString( "setTheYIntercept" );
        public static final String SET_THE_POINT = RESOURCES.getLocalizedString( "setThePoint" );
        public static final String NOT_A_LINE = RESOURCES.getLocalizedString( "notALine" );
        public static final String LINE_TO_GRAPH = RESOURCES.getLocalizedString( "lineToGraph" );
        public static final String YOUR_LINE = RESOURCES.getLocalizedString( "yourLine" );
        public static final String YOUR_EQUATION = RESOURCES.getLocalizedString( "yourEquation" );
        public static final String A_CORRECT_EQUATION = RESOURCES.getLocalizedString( "aCorrectEquation" );
        public static final String UNDEFINED = RESOURCES.getLocalizedString( "undefined" );

        public static final String SYMBOL_X = RESOURCES.getLocalizedString( "symbol.x" );
        public static final String SYMBOL_Y = RESOURCES.getLocalizedString( "symbol.y" );
        public static final String SYMBOL_SLOPE = RESOURCES.getLocalizedString( "symbol.slope" );
        public static final String SYMBOL_INTERCEPT = RESOURCES.getLocalizedString( "symbol.intercept" );

        public static final String TAB_SLOPE = RESOURCES.getLocalizedString( "tab.slope" );
        public static final String TAB_SLOPE_INTERCEPT = RESOURCES.getLocalizedString( "tab.slopeIntercept" );
        public static final String TAB_POINT_SLOPE = RESOURCES.getLocalizedString( "tab.pointSlope" );
        public static final String TAB_LINE_GAME = RESOURCES.getLocalizedString( "tab.lineGame" );

        public static final String POINT_XY = RESOURCES.getLocalizedString( "point.xy" );
        public static final String POINT_UNKNOWN = RESOURCES.getLocalizedString( "point.unknown" );
        public static final String POINTS_AWARDED = RESOURCES.getLocalizedString( "pointsAwarded" );
        public static final String SLOPE_IS = RESOURCES.getLocalizedString( "slopeIs" );
    }

    public static class Images {

        public static final Image MINIMIZE_BUTTON = PhetCommonResources.getImage( "buttons/minimizeButton.png" );
        public static final Image MAXIMIZE_BUTTON = PhetCommonResources.getImage( "buttons/maximizeButton.png" );
        public static final Image POINT_TOOL_BODY = RESOURCES.getImage( "point_tool_body.png" );
        public static final Image POINT_TOOL_TIP = RESOURCES.getImage( "point_tool_tip.png" );
        public static final Image CHECK_MARK = RESOURCES.getImage( "Check-Mark-u2713.png" );
        public static final Image X_MARK = RESOURCES.getImage( "Heavy-Ballot-X-u2718.png" );
    }
}
