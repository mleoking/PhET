// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

import java.awt.Image;

import javax.annotation.Resources;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

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
        public static final String SHOW = RESOURCES.getLocalizedString( "show" );

        public static final String SYMBOL_HORIZONTAL_AXIS = RESOURCES.getLocalizedString( "symbol.horizontalAxis" );
        public static final String SYMBOL_VERTICAL_AXIS = RESOURCES.getLocalizedString( "symbol.verticalAxis" );
        public static final String SYMBOL_SLOPE = RESOURCES.getLocalizedString( "symbol.slope" );
        public static final String SYMBOL_INTERCEPT = RESOURCES.getLocalizedString( "symbol.intercept" );

        public static final String TAB_INTRO = RESOURCES.getLocalizedString( "tab.intro" );
        public static final String TAB_GAME = RESOURCES.getLocalizedString( "tab.game" );
    }

    public static class Images {

        public static final Image Y_EQUALS_X_ICON = RESOURCES.getImage( "y_equals_x_icon.png" );
        public static final Image Y_EQUALS_NEGATIVE_X_ICON = RESOURCES.getImage( "y_equals_negative_x_icon.png" );
        public static final Image RISE_OVER_RUN_ICON = RESOURCES.getImage( "under-construction.png" );
        public static final Image LINES_ICON = RESOURCES.getImage( "lines_icon.png" );
        public static final Image POINT_TOOL = RESOURCES.getImage( "point-tool.png" );
    }
}
