// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionmatcher;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.FractionsResources;

/**
 * "Fraction Matcher" simulation.
 *
 * @author Sam Reid
 */
public class FractionMatcherApplication extends PiccoloPhetApplication {

    public FractionMatcherApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new MatchingGameModule( config.isDev(), true ) );
        addModule( new MixedNumbersMatchingGameModule( config.isDev(), true ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, FractionsResources.PROJECT_NAME, "fraction-matcher", FractionMatcherApplication.class );
    }
}