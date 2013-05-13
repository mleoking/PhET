// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.balanceandtorque;

import edu.colorado.phet.balanceandtorque.stanfordstudy.BalanceLabGameComboModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application class for a version of the balancing act simulation that
 * has been customized for a study being conducted by Dan Schwartz's group at
 * Stanford.
 *
 * @author John Blanco
 */
public class BalancingActStudyApplication extends PiccoloPhetApplication {

    public BalancingActStudyApplication( PhetApplicationConfig config ) {
        super( config );

        // Create and add the modules
        addModule( new BalanceLabGameComboModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, BalanceAndTorqueResources.PROJECT_NAME, "balancing-act", BalancingActStudyApplication.class );
    }
}