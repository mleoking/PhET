// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionmatcher;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractionsintro.matchinggame.MatchingGameModule;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class FractionMatcherApplication extends PiccoloPhetApplication {

    public FractionMatcherApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new MatchingGameModule( config.isDev() ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fraction-matcher", FractionMatcherApplication.class );
    }
}