// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.linegraphing.game.GameModule;
import edu.colorado.phet.linegraphing.intro.IntroModule;

/**
 * The "Line Graphing" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGraphingApplication extends PiccoloPhetApplication {

    public LineGraphingApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new IntroModule() );
        addModule( new GameModule() );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, LGResources.PROJECT_NAME, LineGraphingApplication.class );
    }
}
