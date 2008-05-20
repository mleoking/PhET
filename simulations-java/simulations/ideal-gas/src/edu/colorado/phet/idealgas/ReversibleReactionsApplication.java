/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

//import edu.colorado.phet.common.application.ApplicationModel;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.idealgas.controller.MovableWallsModule;
import edu.colorado.phet.idealgas.model.SimulationClock;

import javax.swing.*;
import java.awt.*;

public class ReversibleReactionsApplication extends PhetApplication {
    private MovableWallsModule wallsModule;


    public ReversibleReactionsApplication( String[] args ) {
        super( args, IdealGasResources.getString( "reversible-reactions.name" ),
               IdealGasResources.getString( "reversible-reactions.description" ),
               IdealGasConfig.getVersion().formatForTitleBar(),
//               new SwingClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true ),
//               true,
IdealGasConfig.FRAME_SETUP );

        SimulationClock clock = new SimulationClock( IdealGasConfig.WAIT_TIME, IdealGasConfig.TIME_STEP );

        wallsModule = new MovableWallsModule( clock );
        setModules( new Module[]{wallsModule} );
//        setModules( new Module[] { new MovableWallsModule( getClock() ) } );

    }

    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-B" ) ) {
                PhetGraphicsModule[] modules = (PhetGraphicsModule[])this.getModules();
                for( int j = 0; j < modules.length; j++ ) {
                    ApparatusPanel ap = modules[j].getApparatusPanel();
                    ap.setBackground( Color.black );
                    ap.paintImmediately( ap.getBounds() );
                }
            }
        }
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new IdealGasLookAndFeel().initLookAndFeel();
                ReversibleReactionsApplication reversibleReactionsApplication = new ReversibleReactionsApplication( args );
                reversibleReactionsApplication.startApplication();
            }
        } );
    }
}
