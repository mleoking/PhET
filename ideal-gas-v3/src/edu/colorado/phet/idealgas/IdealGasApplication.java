/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.idealgas.controller.*;

public class IdealGasApplication extends PhetApplication {

    static class IdealGasApplicationModel extends ApplicationModel {
        public IdealGasApplicationModel() {
            super( Strings.title,
                   Strings.description,
                   IdealGasConfig.VERSION,
                   IdealGasConfig.FRAME_SETUP );                   

            // Create the clock
            setClock( new SwingTimerClock( IdealGasConfig.s_timeStep,
                                           IdealGasConfig.s_waitTime ) );

            // Create the modules
            Module idealGasModule = new IdealGasModule( getClock() );
            Module measurementModule = new MeasurementModule( getClock() );
            Module rigidSphereModuleI = new RigidSphereModuleI( getClock() );
            Module rigidSphereModuleII = new RigidHollowSphereModuleII( getClock() );
            Module heliumBalloonModule = new HeliumBalloonModule( getClock() );
            Module hotAirBalloonModule = new HotAirBalloonModule( getClock() );
            Module[] modules = new Module[]{
                idealGasModule,
                measurementModule,
                rigidSphereModuleI,
                rigidSphereModuleII,
                heliumBalloonModule,
                hotAirBalloonModule
            };
            setModules( modules );
//            setInitialModule( heliumBalloonModule );
//            setInitialModule( rigidSphereModuleII );
//            setInitialModule( rigidSphereModuleI );
//            setInitialModule( measurementModule );
//            setInitialModule( hotAirBalloonModule );
            setInitialModule( idealGasModule );
        }
    }

    public IdealGasApplication() {
        super( new IdealGasApplicationModel() );
        this.startApplication();
    }

    public static void main( String[] args ) {
        new IdealGasApplication();
    }
}
