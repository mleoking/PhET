// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharingtestsim;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * @author Sam Reid
 */
public class SimSharingTestSim extends PiccoloPhetApplication {
    public SimSharingTestSim( PhetApplicationConfig config ) {
        super( config );
        addModule( new SimSharingTestModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "simsharing-test-sim", SimSharingTestSim.class );
    }
}