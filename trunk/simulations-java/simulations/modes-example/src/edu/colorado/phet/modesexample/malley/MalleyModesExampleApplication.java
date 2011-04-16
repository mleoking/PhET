// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Top-level class to demonstrate my mode architecture.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MalleyModesExampleApplication extends PiccoloPhetApplication {

    public MalleyModesExampleApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new RectanglesModule( getPhetFrame() ) );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "modes-example", MalleyModesExampleApplication.class );
    }
}
