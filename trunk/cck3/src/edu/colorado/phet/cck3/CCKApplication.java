package edu.colorado.phet.cck3;

import edu.colorado.phet.common_cck.application.ApplicationModel;
import edu.colorado.phet.common_cck.application.PhetApplication;
import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockTickListener;
import edu.colorado.phet.common_cck.model.clock.SwingTimerClock;
import edu.colorado.phet.common_cck.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common_cck.view.util.FrameSetup;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:17:52 AM
 * Copyright (c) Jul 7, 2006 by Sam Reid
 */

public class CCKApplication {
    //version is generated automatically (with ant)
    public static final String localizedStringsPath = "localization/CCKStrings";
    private PhetApplication application;
    private CCKModule cckModule;

    public CCKApplication( String[]args ) throws IOException {
        final SwingTimerClock clock = new SwingTimerClock( 1, 30, false );

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        cckModule = new CCKModule( args );

        FrameSetup frameSetup = debugMode ? new FrameSetup.CenteredWithInsets( 0, 200 ) : (FrameSetup)new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 75, 100 ) );
        ApplicationModel model = new ApplicationModel( SimStrings.get( "CCK3Application.title" ) + " (" + readVersion() + ")",
                                                       SimStrings.get( "CCK3Application.description" ),
                                                       SimStrings.get( "CCK3Application.version" ), frameSetup, cckModule, clock );
        model.setName( "cck" );

        application = new PhetApplication( model );
        application.getApplicationView().getPhetFrame().addMenu( new LookAndFeelMenu() );
        application.getApplicationView().getPhetFrame().addMenu( new OptionsMenu( application, cckModule ) );

        this.cckModule.getApparatusPanel().addKeyListener( new CCKKeyListener( cckModule, new RepaintDebugGraphic( cckModule.getApparatusPanel(), clock ) ) );
        cckModule.getApparatusPanel().addKeyListener( new SimpleKeyEvent( KeyEvent.VK_D ) {
            public void invoke() {
                cckModule.debugListeners();
            }
        } );
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

    public static void main( String[] args ) throws IOException {
        SimStrings.init( args, localizedStringsPath );
        new CCKPhetLookAndFeel().initLookAndFeel();
        new CCKApplication( args ).startApplication();
    }

}
