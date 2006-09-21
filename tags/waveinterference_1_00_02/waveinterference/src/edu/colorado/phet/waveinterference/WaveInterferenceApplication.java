/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;
import edu.colorado.phet.waveinterference.util.WIStrings;
import smooth.SmoothLookAndFeelFactory;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 10:52:38 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class WaveInterferenceApplication extends PiccoloPhetApplication {
    private static String VERSION = "1.00.02";
    private static final String LOCALIZATION_BUNDLE_BASENAME = "localization/wi";

    public WaveInterferenceApplication( String[] args ) {
        super( args, WIStrings.getString( "wave.interference" ), WIStrings.getString( "wave.interference.simulation" ), VERSION, new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 50, 50 ) ) );
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    WaveInterferenceMenu menu = new WaveInterferenceMenu();
                    addModule( new WaterModule() );
                    addModule( new SoundModule() );
                    LightModule lightModule = new LightModule();
                    addModule( lightModule );
                    menu.add( new ColorizeCheckBoxMenuItem( lightModule ) );
                    getPhetFrame().addMenu( menu );
                    if( getModules().length > 1 ) {
                        for( int i = 0; i < getModules().length; i++ ) {
                            getModule( i ).setLogoPanelVisible( false );
                        }
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
    }

//    protected PhetFrame createPhetFrame() {
//        return new PhetFrameWorkaround( this );
//    }

    public static void main( final String[] args ) {
        SimStrings.init( args, LOCALIZATION_BUNDLE_BASENAME );
        new WaveIntereferenceLookAndFeel().initLookAndFeel();
        if( Arrays.asList( args ).contains( "-smooth" ) ) {
            doSmooth();
        }
        new WaveInterferenceApplication( args ).startApplication();
    }

    private static void doSmooth() {
        try {
            final String systemLookAndFeelClassName = SmoothLookAndFeelFactory.getSystemLookAndFeelClassName();
            System.out.println( "systemLookAndFeelClassName = " + systemLookAndFeelClassName );
            UIManager.setLookAndFeel( systemLookAndFeelClassName );
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
    }
}
