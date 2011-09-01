// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkOptions;

/**
 * @author Sam Reid
 */
public class EnergySkateParkBasicsApplication extends PhetApplication {
    public EnergySkateParkBasicsApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new AbstractEnergySkateParkModule( "Module", new ConstantDtClock( 30, EnergySkateParkApplication.SIMULATION_TIME_DT ), getPhetFrame(), new EnergySkateParkOptions() ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "energy-skate-park", "energy-skate-park-basics", EnergySkateParkBasicsApplication.class );
    }
}
