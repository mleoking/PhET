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
            super( IdealGasStrings.get( "IdealGasApplication.title" ),
                   IdealGasStrings.get( "IdealGasApplication.description" ),
                   IdealGasConfig.VERSION,
                   IdealGasConfig.FRAME_SETUP );                   

            // Create the clock
            setClock( new SwingTimerClock( IdealGasConfig.s_timeStep,
                                           IdealGasConfig.s_waitTime ) );

            // Create the modules
            Module idealGasModule = new IdealGasModule( getClock() );
            Module measurementModule = new MeasurementModule( getClock() );
            Module rigidSphereModule = new RigidHollowSphereModule( getClock() );
            Module heliumBalloonModule = new HeliumBalloonModule( getClock() );
            Module hotAirBalloonModule = new HotAirBalloonModule( getClock() );
            Module[] modules = new Module[]{
                idealGasModule,
                measurementModule,
                rigidSphereModule,
                heliumBalloonModule,
                hotAirBalloonModule
            };
            setModules( modules );
//            setInitialModule( heliumBalloonModule );
//            setInitialModule( rigidSphereModule );
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

//        String test1 = System.getProperty( "java.vm.version" );
//        System.out.println( "test1 = " + test1 );
//        String s = System.getProperty( "jnlp.locale");
//        System.out.println( "s = " + s );
//        String applicationLocale = System.getProperty( "jnlp.locale" );
//        System.out.println( "applicationLocale = " + applicationLocale );
//
//        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
//            Locale.setDefault( new Locale( applicationLocale ) );
//        }

        new IdealGasApplication();
    }
}
