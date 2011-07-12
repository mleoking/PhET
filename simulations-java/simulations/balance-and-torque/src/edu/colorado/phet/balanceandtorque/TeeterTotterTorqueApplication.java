// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.balanceandtorque.game.TorqueGameModule;
import edu.colorado.phet.balanceandtorque.teetertotter.TeeterTotterTorqueModule;

/**
 * Main application for PhET's torque simulation
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class TeeterTotterTorqueApplication extends PiccoloPhetApplication {
    public static final String NAME = "torque";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    public TeeterTotterTorqueApplication( PhetApplicationConfig config ) {
        super( config );

        //Create the modules
        addModule( new TeeterTotterTorqueModule() );
        addModule( new TorqueGameModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, TeeterTotterTorqueApplication.class );
    }
}