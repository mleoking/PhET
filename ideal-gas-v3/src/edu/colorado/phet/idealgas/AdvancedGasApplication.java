/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

//import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
//import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.Clock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.controller.MovableWallsModule;
import edu.colorado.phet.idealgas.view.IdealGasLandF;
import edu.colorado.phet.idealgas.model.SimulationClock;

import javax.swing.*;
import java.awt.*;

public class AdvancedGasApplication extends PhetApplication {


    public AdvancedGasApplication( String[] args ) {
        super( args, SimStrings.get( "UnimolecularApplication.title" ),
               SimStrings.get( "UnimolecularApplication.description" ),
               IdealGasConfig.VERSION,
//               new SwingClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true ),
//               true,
               IdealGasConfig.FRAME_SETUP );

        SimulationClock clock = new SimulationClock( IdealGasConfig.WAIT_TIME, IdealGasConfig.TIME_STEP );

        setModules( new Module[] { new MovableWallsModule( clock ) } );
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

    public static void main( String[] args ) {

        try {
            UIManager.setLookAndFeel( new IdealGasLandF() );
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }

        SimStrings.setStrings( IdealGasConfig.localizedStringsPath );
        new AdvancedGasApplication( args ).startApplication();
    }
}
