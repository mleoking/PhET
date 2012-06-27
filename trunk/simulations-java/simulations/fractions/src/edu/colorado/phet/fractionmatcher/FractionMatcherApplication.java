// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionmatcher;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * "Fraction Matcher" simulation.
 *
 * @author Sam Reid
 */
public class FractionMatcherApplication extends PiccoloPhetApplication {

    public FractionMatcherApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new MatchingGameModule( config.isDev(), true ) );
    }

    //REVIEW replace "fractions" literal with FractionResources.PROJECT_NAME
    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fraction-matcher", FractionMatcherApplication.class );
    }
}