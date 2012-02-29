// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * For testing Fractions Intro tab by itself
 *
 * @author Sam Reid
 */
public class FractionsIntroTabApplication extends PiccoloPhetApplication {
    public FractionsIntroTabApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new FractionsIntroModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", FractionsIntroTabApplication.class );
    }
}