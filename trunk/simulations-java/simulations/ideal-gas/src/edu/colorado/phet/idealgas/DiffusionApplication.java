/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.idealgas.controller.DiffusionModule;
import edu.colorado.phet.idealgas.model.SimulationClock;

public class DiffusionApplication extends PhetApplication {

    public DiffusionApplication( PhetApplicationConfig config) {
        super( config);

        SimulationClock clock = new SimulationClock( IdealGasConfig.WAIT_TIME, IdealGasConfig.TIME_STEP );

        setModules( new Module[]{new DiffusionModule( clock )} );
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

    public static class DiffusionConfig extends PhetApplicationConfig {

        public DiffusionConfig( String[] commandLineArgs ) {
            super( commandLineArgs, IdealGasConfig.FRAME_SETUP, new PhetResources( "ideal-gas" ), "diffusion" );
            setApplicationConstructor( new ApplicationConstructor() {
                public PhetApplication getApplication( PhetApplicationConfig config ) {
                    new IdealGasLookAndFeel().initLookAndFeel();
                return new DiffusionApplication( config);
                }
            } );
        }
    }

    public static void main( final String[] args ) {
        new DiffusionConfig( args ).launchSim();
    }
}
