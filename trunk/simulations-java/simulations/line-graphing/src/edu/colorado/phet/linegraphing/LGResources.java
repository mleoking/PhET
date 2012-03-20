// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

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

        public static final String AXIS_PLUS_X = RESOURCES.getLocalizedString( "axis.+x" );
        public static final String AXIS_MINUS_X = RESOURCES.getLocalizedString( "axis.-x" );
        public static final String AXIS_PLUS_Y = RESOURCES.getLocalizedString( "axis.+y" );
        public static final String AXIS_MINUS_Y = RESOURCES.getLocalizedString( "axis.-y" );

        public static final String EQUATION_Y_EQUALS_MX_PLUS_B = RESOURCES.getLocalizedString( "equation.y=mx+b" );
        public static final String EQUATION_X_EQUALS_MY_PLUS_B = RESOURCES.getLocalizedString( "equation.x=my+b" );
        public static final String EQUATION_Y_EQUALS_X = RESOURCES.getLocalizedString( "equation.y=+x" );
        public static final String EQUATION_Y_EQUALS_MINUS_X = RESOURCES.getLocalizedString( "equation.y=-x" );

        public static final String TAB_INTRO = RESOURCES.getLocalizedString( "tab.intro" );
        public static final String TAB_GAME = RESOURCES.getLocalizedString( "tab.game" );
    }
}
