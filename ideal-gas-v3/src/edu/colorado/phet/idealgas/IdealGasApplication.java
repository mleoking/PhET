/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.view.IdealGasLandF;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class IdealGasApplication extends PhetApplication {

    static class IdealGasApplicationModel extends ApplicationModel {
        public IdealGasApplicationModel() {
            super( SimStrings.get( "IdealGasApplication.title" ),
                   SimStrings.get( "IdealGasApplication.description" ),
                   IdealGasConfig.VERSION,
                   IdealGasConfig.FRAME_SETUP );

            // Create the clock
            SwingTimerClock clock = new SwingTimerClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true );
            setClock( clock );

            // Create the modules
            Module idealGasModule = new IdealGasModule( getClock() );
            Module[] modules = new Module[]{
                idealGasModule,
            };
            setModules( modules );
            setInitialModule( idealGasModule );

            // Set the initial size
            setFrameCenteredSize( 920, 700 );
        }
    }

    public IdealGasApplication( String[] args ) {
        super( args,
               SimStrings.get( "IdealGasApplication.title" ),
               SimStrings.get( "IdealGasApplication.description" ),
               IdealGasConfig.VERSION,
               new SwingTimerClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true ),
               true,
               IdealGasConfig.FRAME_SETUP );


        Module idealGasModule = new IdealGasModule( getClock() );
        Module[] modules = new Module[]{
            idealGasModule,
        };
        setModules( modules );

        super.startApplication();
    }


    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-B" ) ) {
                ModuleManager mm = this.getModuleManager();
                for( int j = 0; j < mm.numModules(); j++ ) {
                    ApparatusPanel ap = mm.moduleAt( j ).getApparatusPanel();
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
        new IdealGasApplication( args );
    }
}
