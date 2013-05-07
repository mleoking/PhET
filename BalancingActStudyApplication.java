// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.balanceandtorque;

import edu.colorado.phet.balanceandtorque.balancelab.BalanceLabModule;
import edu.colorado.phet.balanceandtorque.game.BalanceGameModule;
import edu.colorado.phet.balanceandtorque.intro.IntroModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application class for a custom version of PhET's balancing act
 * simulation in which a single tab is presented to the user at first, and
 * at a later time they enable the second tab.
 *
 * @author John Blanco
 */
public class BalancingActStudyApplication extends PiccoloPhetApplication {

    public BalancingActStudyApplication( PhetApplicationConfig config ) {
        super( config );

        // Create the modules
        addModule( new BalanceLabModule() );
        addModule( new BalanceGameModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, BalanceAndTorqueResources.PROJECT_NAME, "balancing-act", BalancingActStudyApplication.class );
    }

    @Override public void addModule( Module module ) {
        super.addModule( module );
    }

    @Override public void setActiveModule( Module module ) {
        super.setActiveModule( module );
    }

    @Override public void setActiveModule( int i ) {
        super.setActiveModule( i );
    }
}