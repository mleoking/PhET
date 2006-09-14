package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.controls.LookAndFeelMenu;
import edu.colorado.phet.cck3.controls.OptionsMenu;
import edu.colorado.phet.cck3.phetgraphics_cck.CCKModule;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.application.ApplicationModel;
import edu.colorado.phet.common_cck.application.Module;
import edu.colorado.phet.common_cck.application.PhetApplication;
import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockTickListener;
import edu.colorado.phet.common_cck.model.clock.SwingTimerClock;
import edu.colorado.phet.common_cck.view.util.FrameSetup;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

public class CCKApplication_orig {
    //version is generated automatically (with ant)
    public static final String localizedStringsPath = "localization/CCKStrings";
    private PhetApplication application;
    private CCKModule cckModule;

    public CCKApplication_orig( String[]args ) throws IOException {
        final SwingTimerClock clock = new SwingTimerClock( 1, 30, false );

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        cckModule = new CCKModule( args );
        Module cckModule2 = new CCKModule( args );
        cckModule2.setName( "mod 2" );

        Module[] modules = new Module[]{cckModule, cckModule2};


        String subTitle = Arrays.asList( args ).contains( "-dynamics" ) ? ": DC + AC" : ": DC Only";
        FrameSetup frameSetup = debugMode ? new FrameSetup.CenteredWithInsets( 0, 200 ) : (FrameSetup)new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 75, 100 ) );
        ApplicationModel model = new ApplicationModel( SimStrings.get( "CCKApplication.title" ) + subTitle + " (" + CCKApplication_orig.readVersion() + ")",
                                                       SimStrings.get( "CCKApplication.description" ),
                                                       SimStrings.get( "CCKApplication.version" ), frameSetup, modules, clock );
//                                                       SimStrings.get( "CCK3Application.version" ), frameSetup, cckModule, clock );
        model.setName( "cck" );
        model.setInitialModule( cckModule );

        application = new PhetApplication( model );
        application.getApplicationView().getPhetFrame().addMenu( new LookAndFeelMenu() );
        application.getApplicationView().getPhetFrame().addMenu( new OptionsMenu( application, cckModule ) );

        this.cckModule.getApparatusPanel().addKeyListener( new CCKKeyListener( cckModule ) );
        if( debugMode ) {
            application.getApplicationView().getPhetFrame().setLocation( 0, 0 );
        }
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                cckModule.clockTickFinished();
            }
        } );
        //todo this is buggy with user-set pause & play
        application.getApplicationView().getPhetFrame().addWindowListener( new WindowAdapter() {
            public void windowIconified( WindowEvent e ) {
                clock.setPaused( true );
            }

            public void windowDeiconified( WindowEvent e ) {
                clock.setPaused( false );
            }
        } );
    }

    private void startApplication() {
        application.startApplication();
        cckModule.getApparatusPanel().requestFocus();
        for( int i = 0; i < application.numModules(); i++ ) {
            if( application.moduleAt( i )instanceof CCKModule ) {
                CCKModule module = (CCKModule)application.moduleAt( i );
                module.applicationStarted();
            }
        }
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

    public static void main( final String[] args ) {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    SimStrings.init( args, CCKApplication_orig.localizedStringsPath );
                    new CCKPhetLookAndFeel().initLookAndFeel();
                    try {
                        new CCKApplication_orig( args ).startApplication();
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                }
            } );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }
        System.out.println( "args = " + args );
    }
}
