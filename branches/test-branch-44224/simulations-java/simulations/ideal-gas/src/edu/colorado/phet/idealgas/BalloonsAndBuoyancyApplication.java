/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.idealgas.controller.HeliumBalloonModule;
import edu.colorado.phet.idealgas.controller.HotAirBalloonModule;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.controller.RigidHollowSphereModule;
import edu.colorado.phet.idealgas.model.IdealGasClock;
import edu.colorado.phet.idealgas.view.WiggleMeGraphic;

public class BalloonsAndBuoyancyApplication extends PhetApplication {

    private static class BalloonsAndBuoyancyClock extends IdealGasClock {
        public BalloonsAndBuoyancyClock() {
            super( IdealGasConfig.WAIT_TIME, IdealGasConfig.TIME_STEP );
        }
    }
    
    public BalloonsAndBuoyancyApplication( PhetApplicationConfig config ) {
        super( config );

        // Create the modules
        Module idealgasModule = new IdealGasModule( new BalloonsAndBuoyancyClock() );
        Module rigidSphereModule = new RigidHollowSphereModule( new BalloonsAndBuoyancyClock() );
        Module heliumBalloonModule = new HeliumBalloonModule( new BalloonsAndBuoyancyClock() );
        final HotAirBalloonModule hotAirBalloonModule = new HotAirBalloonModule( new BalloonsAndBuoyancyClock() );
        Module[] modules = new Module[]{
                hotAirBalloonModule,
                rigidSphereModule,
                heliumBalloonModule,
                idealgasModule,
        };
        setModules( modules );


        final WiggleMeGraphic wiggleMeGraphic;
        wiggleMeGraphic = new WiggleMeGraphic( hotAirBalloonModule.getApparatusPanel(),
                                               new Point2D.Double( IdealGasConfig.X_BASE_OFFSET + 480, IdealGasConfig.Y_BASE_OFFSET + 170 ),
                                               hotAirBalloonModule.getModel() );
        wiggleMeGraphic.start();
        hotAirBalloonModule.addGraphic( wiggleMeGraphic, 40 );
        hotAirBalloonModule.getPump().addObserver( new SimpleObserver() {
            public void update() {
                if ( wiggleMeGraphic != null ) {
                    wiggleMeGraphic.kill();
                    hotAirBalloonModule.getApparatusPanel().removeGraphic( wiggleMeGraphic );
                    hotAirBalloonModule.getPump().removeObserver( this );
                }
            }
        } );
    }

    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for ( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if ( arg.startsWith( "-B" ) ) {
                PhetGraphicsModule[] modules = (PhetGraphicsModule[]) this.getModules();
                for ( int j = 0; j < modules.length; j++ ) {
                    ApparatusPanel ap = modules[j].getApparatusPanel();
                    ap.setBackground( Color.black );
                    ap.paintImmediately( ap.getBounds() );
                }
            }
        }
    }
    
    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new BalloonsAndBuoyancyApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, IdealGasConfig.PROJECT_NAME, IdealGasConfig.FLAVOR_BALLOONS_AND_BUOYANCY );
        appConfig.setLookAndFeel( new IdealGasLookAndFeel() );
        appConfig.setFrameSetup( IdealGasConfig.FRAME_SETUP );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
