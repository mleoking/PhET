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
import edu.colorado.phet.idealgas.controller.HeliumBalloonModule;
import edu.colorado.phet.idealgas.controller.HotAirBalloonModule;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.controller.RigidHollowSphereModule;
import edu.colorado.phet.idealgas.controller.menus.OptionsMenu;
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

//            PhetGraphic.ignoreRectangles = true;
            
            // Create the clock
            SwingTimerClock clock = new SwingTimerClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true );
            setClock( clock );

            // Create the modules
            Module idealGasModule = new IdealGasModule( getClock() );
            Module rigidSphereModule = new RigidHollowSphereModule( getClock() );
            Module heliumBalloonModule = new HeliumBalloonModule( getClock() );
            Module hotAirBalloonModule = new HotAirBalloonModule( getClock() );
//            Module movableWallsModule = new MovableWallsModule( getClock() );
//            Module diffusionModule = new DiffusionModule( getClock() );
            Module[] modules = new Module[]{
                idealGasModule,
                rigidSphereModule,
                heliumBalloonModule,
                hotAirBalloonModule,
//                movableWallsModule,
//                diffusionModule
            };
            setModules( modules );
//            setInitialModule( movableWallsModule );
//            setInitialModule( diffusionModule );
            setInitialModule( idealGasModule );

            // Set the initial size
            setFrameCenteredSize( 920, 700 );
//            setFrameCenteredSize( 1020, 700 );
        }
    }

    public IdealGasApplication( String[] args ) {
        super( new IdealGasApplicationModel(), args );
//        this.getApplicationView().getPhetFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Add some menus
        PhetFrame frame = getPhetFrame();
        frame.addMenu( new OptionsMenu( this ) );
//
//        frame.getClockControlPanel().add( new StopwatchPanel( getApplicationModel().getClock()), BorderLayout.WEST );

        this.startApplication();
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

        String test1 = System.getProperty( "java.vm.version" );
        System.out.println( "test1 = " + test1 );
        String s = System.getProperty( "javaws.locale" );
        System.out.println( "s = " + s );
        String applicationLocale = System.getProperty( "javaws.locale" );
        System.out.println( "applicationLocale = " + applicationLocale );

        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            Locale.setDefault( new Locale( applicationLocale ) );
        }
        String argsKey = "user.language=";
        System.out.println( "args.length = " + args.length );
        if( args.length > 0 && args[0].startsWith( argsKey ) ) {
            String locale = args[0].substring( argsKey.length(), args[0].length() );
            SimStrings.setLocale( new Locale( locale ) );
//            Locale.setDefault( new Locale( locale ) );
            System.out.println( "locale = " + locale );
        }

        SimStrings.setStrings( IdealGasConfig.localizedStringsPath );
        new IdealGasApplication( args );
    }
}
