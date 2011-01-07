// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.workenergy.module.EnergyModule;

/**
 * @author Sam Reid
 */
public class WorkEnergyApplication extends PiccoloPhetApplication {
    public WorkEnergyApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new EnergyModule( getPhetFrame() ) );
//        addModule( new WorkModule( getPhetFrame() ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "work-energy", WorkEnergyApplication.class );
    }
}
