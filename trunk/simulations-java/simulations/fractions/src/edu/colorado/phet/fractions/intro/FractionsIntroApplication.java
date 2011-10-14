// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * @author Sam Reid
 */
public class FractionsIntroApplication extends PiccoloPhetApplication {
    public FractionsIntroApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new FractionsIntroModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", FractionsIntroApplication.class );
    }
}