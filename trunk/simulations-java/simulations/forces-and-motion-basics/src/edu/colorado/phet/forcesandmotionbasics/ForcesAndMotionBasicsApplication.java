// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * @author Sam Reid
 */
public class ForcesAndMotionBasicsApplication extends PiccoloPhetApplication {
    public ForcesAndMotionBasicsApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new ForcesAndMotionBasicsModule( MyMode.mode1 ) );
        addModule( new ForcesAndMotionBasicsModule( MyMode.mode2 ) );
        addModule( new ForcesAndMotionBasicsModule( MyMode.mode3 ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "forces-and-motion-basics", ForcesAndMotionBasicsApplication.class );
    }
}