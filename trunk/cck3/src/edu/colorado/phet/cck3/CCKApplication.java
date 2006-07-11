package edu.colorado.phet.cck3;

import edu.colorado.phet.common_cck.application.ApplicationModel;
import edu.colorado.phet.common_cck.application.PhetApplication;
import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockTickListener;
import edu.colorado.phet.common_cck.model.clock.SwingTimerClock;
import edu.colorado.phet.common_cck.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common_cck.view.plaf.PlafUtil;
import edu.colorado.phet.common_cck.view.util.FrameSetup;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
    private CCK3Module cck;

    public CCKApplication( String[]args ) throws IOException {
        final SwingTimerClock clock = new SwingTimerClock( 1, 30, false );

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
            System.out.println( "debugMode = " + debugMode );
        }

        cck = new CCK3Module( args );
        RepaintDebugGraphic colorG = new RepaintDebugGraphic( cck.getApparatusPanel(), clock );

        FrameSetup fs = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 75, 100 ) );
        if( debugMode ) {
            fs = new FrameSetup.CenteredWithInsets( 0, 200 );
        }
        String version = readVersion();
        ApplicationModel model = new ApplicationModel( SimStrings.get( "CCK3Application.title" ) + " (" + version + ")",
                                                       SimStrings.get( "CCK3Application.description" ),
                                                       SimStrings.get( "CCK3Application.version" ), fs, cck, clock );
        model.setName( "cck" );
        model.setUseClockControlPanel( true );

        application = new PhetApplication( model );

        JMenu laf = new JMenu( SimStrings.get( "ViewMenu.Title" ) );
        laf.setMnemonic( SimStrings.get( "ViewMenu.TitleMnemonic" ).charAt( 0 ) );
        JMenuItem[] jmi = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < jmi.length; i++ ) {
            JMenuItem jMenuItem = jmi[i];
            laf.add( jMenuItem );
        }
        application.getApplicationView().getPhetFrame().addMenu( laf );

        JMenu dev = new JMenu( SimStrings.get( "OptionsMenu.Title" ) );
        dev.setMnemonic( SimStrings.get( "OptionsMenu.TitleMnemonic" ).charAt( 0 ) );

        cck.setFrame( application.getApplicationView().getPhetFrame() );
        dev.add( new BackgroundColorMenuItem( application, cck ) );
        dev.add( new ToolboxColorMenuItem( application, cck ) );
        application.getApplicationView().getPhetFrame().addMenu( dev );

        this.cck.getApparatusPanel().addKeyListener( new CCKKeyListener( cck, colorG ) );
        cck.getApparatusPanel().addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_C ) {
                    cck.resetDynamics();
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        cck.getApparatusPanel().addKeyListener( new SimpleKeyEvent( KeyEvent.VK_D ) {
            public void invoke() {
                cck.debugListeners();
            }
        } );

        if( debugMode ) {
            application.getApplicationView().getPhetFrame().setLocation( 0, 0 );
        }
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                cck.clockTickFinished();
            }
        } );
        application.getApplicationView().getPhetFrame().addWindowListener( new WindowAdapter() {
            public void windowIconified( WindowEvent e ) {
                clock.setPaused( true );
            }

            public void windowDeiconified( WindowEvent e ) {
                clock.setPaused( false );
            }
        } );
    }

    public static void main( String[] args ) throws IOException {
        SimStrings.init( args, localizedStringsPath );
        new CCKPhetLookAndFeel().initLookAndFeel();
        new CCKApplication( args ).startApplication();
    }

    private void startApplication() {
        application.startApplication();
        cck.getApparatusPanel().requestFocus();
    }

    private static String readVersion() {
        URL url = Thread.currentThread().getContextClassLoader().getResource( "cck.version" );
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            String line = br.readLine();
            return line;
        }
        catch( IOException e ) {
            e.printStackTrace();
            return "Version Not Found";
        }
    }

}
