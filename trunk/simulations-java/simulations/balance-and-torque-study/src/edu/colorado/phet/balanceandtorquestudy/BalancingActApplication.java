// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy;

import edu.colorado.phet.balanceandtorquestudy.balancelab.BalanceLabModule;
import edu.colorado.phet.balanceandtorquestudy.game.BalanceGameModule;
import edu.colorado.phet.balanceandtorquestudy.intro.IntroModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application class for PhET's balancing act simulation.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class BalancingActApplication extends PiccoloPhetApplication {

    public BalancingActApplication( PhetApplicationConfig config ) {
        super( config );

        // Create the modules
        addModule( new IntroModule() );
        addModule( new BalanceLabModule() );
        addModule( new BalanceGameModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, BalanceAndTorqueStudyResources.PROJECT_NAME, "balancing-act", BalancingActApplication.class );
    }
}