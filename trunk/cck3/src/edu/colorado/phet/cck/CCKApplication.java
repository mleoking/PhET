package edu.colorado.phet.cck;

import edu.colorado.phet.cck.controls.LookAndFeelMenu;
import edu.colorado.phet.cck.phetgraphics_cck.CCKModule;
import edu.colorado.phet.cck.piccolo_cck.CCKPiccoloModule;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:17:52 AM
 * Copyright (c) Jul 7, 2006 by Sam Reid
 */

public class CCKApplication extends PiccoloPhetApplication {
    //version is generated automatically (with ant)
    public static final String localizedStringsPath = "localization/CCKStrings";

    static class CCKPhetGraphicModuleAdapter extends Module {
        private CCKModule cckModule;

        public CCKPhetGraphicModuleAdapter( String[]args ) throws IOException {
            super( "CCK-phetgraphics", new SwingClock( 30, 1 ) );
            cckModule = new CCKModule( args );
            cckModule.initControlPanel( this );
            setSimulationPanel( cckModule.getCCKApparatusPanel() );
            setControlPanel( cckModule.getControlPanel() );
            addModelElement( new ModelElement() {
                public void stepInTime( double dt ) {
                    cckModule.getModel().stepInTime( dt );
                    cckModule.getCCKApparatusPanel().synchronizeImmediately();
                }
            } );
        }
    }

    public static String getSubTitle( String[]args ) {
        return Arrays.asList( args ).contains( "-dynamics" ) ? ": DC + AC" : ": DC Only";
    }

    public CCKApplication( String[]args ) throws IOException {
        super( args, SimStrings.get( "CCKApplication.title" ) + getSubTitle( args ) + " (" + readVersion() + ")",
               SimStrings.get( "CCKApplication.description" ),
               SimStrings.get( "CCKApplication.version" ) );

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        CCKPhetGraphicModuleAdapter phetGraphicsCCKModule = new CCKPhetGraphicModuleAdapter( args );
        CCKPiccoloModule cckPiccoloModule = new CCKPiccoloModule( args );
        Module[] modules = new Module[]{phetGraphicsCCKModule, cckPiccoloModule};
        setModules( modules );

        getPhetFrame().addMenu( new LookAndFeelMenu() );
//        getPhetFrame().addMenu( new OptionsMenu( th, cckModule ) );//todo options menu

//        this.cckModule.getApparatusPanel().addKeyListener( new CCKKeyListener( cckModule, new RepaintDebugGraphic( cckModule.getApparatusPanel(), clock ) ) );
//        if( debugMode ) {
//            application.getApplicationView().getPhetFrame().setLocation( 0, 0 );
//        }
//        clock.addClockTickListener( new ClockListener() {
//            public void clockTicked( IClock c, double dt ) {
//                cckModule.clockTickFinished();
//            }
//        } );
//        //todo this is buggy with user-set pause & play
//        application.getApplicationView().getPhetFrame().addWindowListener( new WindowAdapter() {
//            public void windowIconified( WindowEvent e ) {
//                clock.setPaused( true );
//            }
//
//            public void windowDeiconified( WindowEvent e ) {
//                clock.setPaused( false );
//            }
//        } );
    }

    private static String readVersion() {
        URL url = Thread.currentThread().getContextClassLoader().getResource( "cck.version" );
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            return br.readLine();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return "Version Not Found";
        }
    }

    public static void main( final String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
//                edu.colorado.phet.common_cck.view.util.SimStrings.init( args, CCKApplication_orig.localizedStringsPath );
                SimStrings.init( args, CCKApplication.localizedStringsPath );
                new CCKPhetLookAndFeel().initLookAndFeel();
                try {
                    new CCKApplication( args ).startApplication();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }
}
