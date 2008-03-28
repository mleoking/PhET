/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.idealgas.controller.DiffusionModule;
import edu.colorado.phet.idealgas.model.SimulationClock;
import edu.colorado.phet.idealgas.view.IdealGasLandF;

import javax.swing.*;
import java.awt.*;

public class DiffusionApplication extends PhetApplication {

    public DiffusionApplication( String[] args ) {
        super( args,
               IdealGasResources.getString( "diffusion.name" ),
               IdealGasResources.getString( "diffusion.description" ),
               IdealGasConfig.getVersion().formatForTitleBar(),
//               new SwingClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true ),
//               true,
IdealGasConfig.FRAME_SETUP );

        SimulationClock clock = new SimulationClock( IdealGasConfig.WAIT_TIME, IdealGasConfig.TIME_STEP );

        setModules( new Module[]{new DiffusionModule( clock )} );
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
                try {
                    UIManager.setLookAndFeel( new IdealGasLandF() );
                }
                catch( UnsupportedLookAndFeelException e ) {
                    e.printStackTrace();
                }

                new DiffusionApplication( args ).startApplication();
            }
        } );
    }
}
