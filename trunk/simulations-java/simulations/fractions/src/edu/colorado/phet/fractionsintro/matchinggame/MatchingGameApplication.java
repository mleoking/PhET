// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Entry point for launching and testing Matching Game tab
 *
 * @author Sam Reid
 */
public class MatchingGameApplication extends PiccoloPhetApplication {
    public MatchingGameApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new MatchingGameModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", MatchingGameApplication.class );
    }
}