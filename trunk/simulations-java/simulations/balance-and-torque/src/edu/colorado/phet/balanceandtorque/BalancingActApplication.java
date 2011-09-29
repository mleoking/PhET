// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque;

import edu.colorado.phet.balanceandtorque.game.BalanceGameModule;
import edu.colorado.phet.balanceandtorque.teetertotter.BalancingActModule;
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
    public static final String NAME = "balance-and-torque";

    public BalancingActApplication( PhetApplicationConfig config ) {
        super( config );

        //Create the modules
        addModule( new BalancingActModule() );
        addModule( new BalanceGameModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, "balancing-act", BalancingActApplication.class );
    }
}