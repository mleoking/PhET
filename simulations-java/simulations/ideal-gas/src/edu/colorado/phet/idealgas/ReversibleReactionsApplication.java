/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

//import edu.colorado.phet.common.application.ApplicationModel;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.idealgas.controller.MovableWallsModule;
import edu.colorado.phet.idealgas.model.SimulationClock;

public class ReversibleReactionsApplication extends PhetApplication {
    private MovableWallsModule wallsModule;


    public ReversibleReactionsApplication( PhetApplicationConfig config ) {
        super( config );

        SimulationClock clock = new SimulationClock( IdealGasConfig.WAIT_TIME, IdealGasConfig.TIME_STEP );

        wallsModule = new MovableWallsModule( clock ) {
            protected void addHelp() {
                addStoveHelp();//stove help only
            }
        };
        setModules( new Module[]{wallsModule} );
//        setModules( new Module[] { new MovableWallsModule( getClock() ) } );

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
                return new ReversibleReactionsApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, IdealGasConfig.PROJECT_NAME, IdealGasConfig.FLAVOR_REVERSIBLE_REACTIONS );
        appConfig.setLookAndFeel( new IdealGasLookAndFeel() );
        appConfig.setFrameSetup( IdealGasConfig.FRAME_SETUP );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
