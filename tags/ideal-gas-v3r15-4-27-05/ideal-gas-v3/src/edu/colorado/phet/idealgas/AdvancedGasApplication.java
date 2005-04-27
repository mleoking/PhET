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
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.controller.MovableWallsModule;
import edu.colorado.phet.idealgas.controller.menus.OptionsMenu;
import edu.colorado.phet.idealgas.view.IdealGasLandF;

import javax.swing.*;
import java.awt.*;

public class AdvancedGasApplication extends PhetApplication {

    static class IdealGasApplicationModel extends ApplicationModel {
        public IdealGasApplicationModel() {
            super( SimStrings.get( "IdealGasApplication.title" ),
                   SimStrings.get( "IdealGasApplication.description" ),
                   IdealGasConfig.VERSION,
                   IdealGasConfig.FRAME_SETUP );

            // Set the color scheme
            IdealGasConfig.COLOR_SCHEME = IdealGasConfig.WHITE_BACKGROUND_COLOR_SCHEME;

            // Create the clock
            SwingTimerClock clock = new SwingTimerClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true );
            setClock( clock );

            // Create the modules
            Module movableWallsModule = new MovableWallsModule( getClock() );
            setModule( movableWallsModule );

            // Set the initial size
            setFrameCenteredSize( 920, 700 );
        }
    }

    public AdvancedGasApplication( String[] args) {
        super( new IdealGasApplicationModel(), args );

        // Add some menus
        PhetFrame frame = getPhetFrame();
        frame.addMenu( new OptionsMenu( this ) );

        this.startApplication();
    }

    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-B")) {
                ModuleManager mm = this.getModuleManager();
                for( int j = 0; j < mm.numModules(); j++ ) {
                    ApparatusPanel ap = mm.moduleAt( j ).getApparatusPanel();
                    ap.setBackground( Color.black);
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
        new AdvancedGasApplication( args );
    }
}
