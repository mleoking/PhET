/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.model.SimulationClock;
import edu.colorado.phet.idealgas.view.WiggleMeGraphic;

public class GasPropertiesApplication extends PhetApplication {

    public GasPropertiesApplication( PhetApplicationConfig config) {
        super(config);
//        super( args,
//               IdealGasResources.getString( "gas-properties.name" ),
//               IdealGasResources.getString( "gas-properties.description" ),
//               IdealGasConfig.getVersion().formatForTitleBar(),
//               IdealGasConfig.FRAME_SETUP );

        SimulationClock clock = new SimulationClock( IdealGasConfig.WAIT_TIME, IdealGasConfig.TIME_STEP );

        final IdealGasModule idealGasModule = new IdealGasModule( clock );
        Module[] modules = new Module[]{
                idealGasModule,
        };
        setModules( modules );

        final WiggleMeGraphic wiggleMeGraphic;
        wiggleMeGraphic = new WiggleMeGraphic( idealGasModule.getApparatusPanel(),
                                               new Point2D.Double( IdealGasConfig.X_BASE_OFFSET + 480, IdealGasConfig.Y_BASE_OFFSET + 170 ),
                                               idealGasModule.getModel() );
        wiggleMeGraphic.start();
        idealGasModule.addGraphic( wiggleMeGraphic, 40 );
        idealGasModule.getPump().addObserver( new SimpleObserver() {
            public void update() {
                if ( wiggleMeGraphic != null ) {
                    wiggleMeGraphic.kill();
                    idealGasModule.getApparatusPanel().removeGraphic( wiggleMeGraphic );
                    idealGasModule.getPump().removeObserver( this );
                }
            }
        } );

        super.startApplication();
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
        new GasPropertiesConfig( args, IdealGasConfig.FRAME_SETUP, new PhetResources( "ideal-gas" ), "gas-properties" ).launchSim();
    }

    private static class GasPropertiesConfig extends PhetApplicationConfig {
        public GasPropertiesConfig( String[] commandLineArgs, FrameSetup frameSetup, PhetResources resourceLoader, String flavor ) {
            super( commandLineArgs, frameSetup, resourceLoader, flavor );
            setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    new IdealGasLookAndFeel().initLookAndFeel();
                    return new GasPropertiesApplication( config);
                }
            } );
        }
    }
}
