package edu.colorado.phet.movingman;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.QuickTimer;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;

/**
 * User: Sam Reid
 * Date: Sep 3, 2006
 * Time: 12:25:52 PM
 * Copyright (c) Sep 3, 2006 by Sam Reid
 */

public class MovingManApplication {

    private static final String MOVING_MAN_VERSION = "1.15.02";
    private static boolean addJEP = true;

    // Localization
    public static final String localizedStringsPath = "localization/MovingManStrings";

    public static void main( final String[] args ) throws Exception {
        QuickTimer initTime = new QuickTimer();
//        boolean same = 0 == -0;
//        System.out.println( "same = " + same );
//        UIManager.setLookAndFeel( MetalLookAndFeel.class.getName());//to initialize for later.

        PhetLookAndFeel plaf = new PhetLookAndFeel();
        plaf.setInsets( new Insets( 1, 1, 1, 1 ) );
        plaf.apply();
        PhetLookAndFeel.setLookAndFeel();

        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            Locale.setDefault( new Locale( applicationLocale ) );
        }
        String argsKey = "user.language=";
        if( args.length > 0 && args[0].startsWith( argsKey ) ) {
            String locale = args[0].substring( argsKey.length(), args[0].length() );
            Locale.setDefault( new Locale( locale ) );
        }

        SimStrings.setStrings( localizedStringsPath );

//        // Initialize localized strings
//        SimStrings.init( args, localizedStringsPath );

        //Putting in swing thread avoids concurrentmodification exception in GraphicLayerSet.
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                try {
                    runMain( args );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
//        System.out.println( "initTime = " + initTime );
    }

    private static void runMain( String[] args ) throws IOException {
        SwingTimerClock clock = new SwingTimerClock( 1, 30, false );
        FrameSetup setup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 800 ) );
        String version = MOVING_MAN_VERSION;
        ApplicationModel desc = new ApplicationModel( SimStrings.get( "MovingManApplication.title" ) + " (" + version + ")",
                                                      SimStrings.get( "MovingManApplication.description" ),
                                                      version, setup );
        desc.setName( "movingman" );
        PhetApplication tpa = new PhetApplication( args, desc.getWindowTitle(), desc.getDescription(), desc.getVersion(), clock, false, setup );
        PhetFrame frame = tpa.getPhetFrame();
        final MovingManModule m = new MovingManModule( clock );

        tpa.setModules( new Module[]{m} );

//        final PhetFrame frame = tpa.getPhetFrame();
        m.setFrame( frame );
        if( m.getControlPanel() != null ) {
        }
        if( addJEP ) {
            m.addMiscMenu( m );
        }

        tpa.startApplication();
        m.getTimeModel().getRecordMode().initialize();

        m.setInited( true );
        m.relayout();
        m.setSmoothingSmooth();
//        RepaintDebugGraphic repaintDebugGraphic=new RepaintDebugGraphic( m.getApparatusPanel(), clock);
        RepaintDebugGraphic.enable( m.getApparatusPanel(), clock );
    }
}
