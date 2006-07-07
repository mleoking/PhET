package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.common.ColorDialog;
import edu.colorado.phet.cck3.phetcommon.PhetLookAndFeel;
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
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:17:52 AM
 * Copyright (c) Jul 7, 2006 by Sam Reid
 */

public class CCKApplication {
    public static final String localizedStringsPath = "localization/CCKStrings";

    public static void main( String[] args ) throws IOException, UnsupportedLookAndFeelException {

        SimStrings.init( args, localizedStringsPath );
        PhetLookAndFeel.setLookAndFeel();
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.setFont( new Font( "Lucida Sans", Font.BOLD, 13 ) );
        phetLookAndFeel.apply();

//        CCKLookAndFeel cckLookAndFeel = new CCKLookAndFeel();
//        UIManager.installLookAndFeel( "CCK Default", cckLookAndFeel.getClass().getName() );
//        UIManager.setLookAndFeel( cckLookAndFeel );
        UIManager.getLookAndFeelDefaults().put( "ClassLoader", CCK3Module.class.getClassLoader() );
        //        SwingTimerClock clock = new SwingTimerClock( 1, 30, false );
        final SwingTimerClock clock = new SwingTimerClock( 1, 30, false );

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
        }
        System.out.println( "debugMode = " + debugMode );
//        boolean virtualLab = false;

//        if( Arrays.asList( args ).contains( "-grabbag" ) ) {
//            grabBag = true;
//        }

        final CCK3Module cck = new CCK3Module( args );
        CCK3Module.module = cck;

        RepaintDebugGraphic colorG = new RepaintDebugGraphic( cck.getApparatusPanel(), clock );

        FrameSetup fs = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100, 100 ) );
        if( debugMode ) {
            fs = new FrameSetup.CenteredWithInsets( 0, 200 );
        }
        String version = readVersion();
        ApplicationModel model = new ApplicationModel( SimStrings.get( "CCK3Application.title" ) + " (" + version + ")",
                                                       SimStrings.get( "CCK3Application.description" ),
                                                       SimStrings.get( "CCK3Application.version" ), fs, cck, clock );
        model.setName( "cck" );
        model.setUseClockControlPanel( false );
        final PhetApplication app = new PhetApplication( model );


        JMenu laf = new JMenu( SimStrings.get( "ViewMenu.Title" ) );
        laf.setMnemonic( SimStrings.get( "ViewMenu.TitleMnemonic" ).charAt( 0 ) );
        JMenuItem[] jmi = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < jmi.length; i++ ) {
            JMenuItem jMenuItem = jmi[i];
            laf.add( jMenuItem );
        }
        app.getApplicationView().getPhetFrame().addMenu( laf );

        JMenu dev = new JMenu( SimStrings.get( "OptionsMenu.Title" ) );
        dev.setMnemonic( SimStrings.get( "OptionsMenu.TitleMnemonic" ).charAt( 0 ) );
        JMenuItem changeBackgroundColor = new JMenuItem( SimStrings.get( "OptionsMenu.BackgroundColorMenuItem" ) );
        changeBackgroundColor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorDialog.Listener listy = new ColorDialog.Listener() {
                    public void colorChanged( Color color ) {
                        cck.getApparatusPanel().setBackground( color );
                    }

                    public void cancelled( Color orig ) {
                        cck.getApparatusPanel().setBackground( orig );
                    }

                    public void ok( Color color ) {
                        cck.getApparatusPanel().setBackground( color );
                    }
                };
                ColorDialog.showDialog( SimStrings.get( "OptionsMenu.BackgroundColorDialogTitle" ),
                                        app.getApplicationView().getPhetFrame(), cck.getApparatusPanel().getBackground(), listy );
            }
        } );
        cck.setFrame( app.getApplicationView().getPhetFrame() );
        JMenuItem toolboxColor = new JMenuItem( SimStrings.get( "OptionsMenu.ToolboxcolorMenuItem" ) );
        toolboxColor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorDialog.Listener listy = new ColorDialog.Listener() {
                    public void colorChanged( Color color ) {
                        cck.getToolbox().setBackgroundColor( color );
                    }

                    public void cancelled( Color orig ) {
                        cck.getToolbox().setBackgroundColor( orig );
                    }

                    public void ok( Color color ) {
                        cck.getToolbox().setBackgroundColor( color );
                    }
                };
                ColorDialog.showDialog( SimStrings.get( "OptionsMenu.ToolboxColorDialogTitle" ),
                                        app.getApplicationView().getPhetFrame(), cck.getToolbox().getBackgroundColor(), listy );
            }
        } );

        dev.add( changeBackgroundColor );
        dev.add( toolboxColor );
        app.getApplicationView().getPhetFrame().addMenu( dev );

//        UIManager.setLookAndFeel( cckLookAndFeel );
        updateFrames();
        app.startApplication();
        updateFrames();
        app.getApplicationView().getPhetFrame().doLayout();
        app.getApplicationView().getPhetFrame().repaint();
        cck.getApparatusPanel().addKeyListener( new CCKKeyListener( cck, colorG ) );
        cck.getApparatusPanel().addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_C ) {
//                    cck.clearCapacitors();
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
        cck.getApparatusPanel().requestFocus();
        //        PlafUtil.updateFrames();
        if( debugMode ) {
            app.getApplicationView().getPhetFrame().setLocation( 0, 0 );
        }
        final JFrame frame = app.getApplicationView().getPhetFrame();
        final Runnable repainter = new Runnable() {
            public void run() {
                Component c = frame.getContentPane();
                c.invalidate();
                c.validate();
                c.repaint();
            }
        };
        frame.addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                repainter.run();
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
        frame.addWindowListener( new WindowAdapter() {
            public void windowActivated( WindowEvent e ) {
            }

            public void windowStateChanged( WindowEvent e ) {
                repainter.run();
            }

            public void windowGainedFocus( WindowEvent e ) {
                repainter.run();
            }
        } );
        frame.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                repainter.run();
            }

            public void componentResized( ComponentEvent e ) {
                repainter.run();
            }

            public void componentMoved( ComponentEvent e ) {
                repainter.run();
            }
        } );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                cck.clockTickFinished();
            }
        } );
        frame.addWindowListener( new WindowAdapter() {
            public void windowIconified( WindowEvent e ) {
                clock.setPaused( true );
            }

            public void windowDeiconified( WindowEvent e ) {
                clock.setPaused( false );
            }
        } );
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

    public static void updateFrames() {
        Frame[] frames = JFrame.getFrames();
        ArrayList alreadyChecked = new ArrayList();
        for( int i = 0; i < frames.length; i++ ) {
            Frame frame = frames[i];
            testUpdate( frame );
            if( !alreadyChecked.contains( frame ) ) {
                Window[] owned = frames[i].getOwnedWindows();
                for( int j = 0; j < owned.length; j++ ) {
                    Window window = owned[j];
                    testUpdate( window );
                }
            }
            alreadyChecked.add( frames[i] );
        }
    }

    public static void testUpdate( Window window ) {
        String title = window.getName();
        if( window instanceof Frame ) {
            Frame f = (Frame)window;
            title = f.getTitle();
            if( title == null ) {
                title = "";
            }
            title = title.trim().toLowerCase();
            if( title.indexOf( "Java Web Start Console".toLowerCase() ) >= 0 ) {
                //ignore
            }
            else if( title.indexOf( "Java Console".toLowerCase() ) >= 0 ) {
                //ignore.
            }
            else {
                SwingUtilities.updateComponentTreeUI( window );
            }
        }

    }
}
