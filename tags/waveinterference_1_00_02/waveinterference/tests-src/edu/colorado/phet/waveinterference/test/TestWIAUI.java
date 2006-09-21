/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.test;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.waveinterference.*;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 10:52:38 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class TestWIAUI extends PhetApplication {
    private static String VERSION = "0.07";

    public TestWIAUI( String[] args ) {
        super( args, "Wave Interference", "Wave Interference simulation", TestWIAUI.VERSION );

        addModule( new WaterModule() );
        addModule( new SoundModule() );
        addModule( new LightModule() );
        getPhetFrame().addMenu( new WaveInterferenceMenu() );
        if( getModules().length > 1 ) {
            for( int i = 0; i < getModules().length; i++ ) {
                getModule( i ).setLogoPanelVisible( false );
            }
        }

    }

    public static void main( String[] args ) {
        new WaveIntereferenceLookAndFeel().initLookAndFeel();
        try {
            final String systemLookAndFeelClassName = smooth.SmoothLookAndFeelFactory.getSystemLookAndFeelClassName();
            System.out.println( "systemLookAndFeelClassName = " + systemLookAndFeelClassName );
            UIManager.setLookAndFeel( systemLookAndFeelClassName );
            System.out.println( "UIManager.getLookAndFeel() = " + UIManager.getLookAndFeel() );
            System.out.println( "new JLabel( \"Hello\").getUI() = " + new JLabel( "Hello" ).getUI() );
            Class cx = Class.forName( systemLookAndFeelClassName, true, Thread.currentThread().
                    getContextClassLoader() );
            System.out.println( "cx = " + cx );
            Object inst = cx.newInstance();
            System.out.println( "inst = " + inst );

//            ComponentUI ui = null;
//            LookAndFeel multiLAF = UIManager.getLAFState().multiLookAndFeel;
//            if( multiLAF != null ) {
//                // This can return null if the multiplexing look and feel
//                // doesn't support a particular UI.
//                ui = multiLAF.getDefaults().getUI( target );
//            }
//            if( ui == null ) {
//                ui = getDefaults().getUI( target );
//            }
            JLabel target = new JLabel( "B" );

            ClassLoader cl = systemLookAndFeelClassName.getClass().getClassLoader();
            ClassLoader uiClassLoader =
                    ( cl != null ) ? (ClassLoader)cl : target.getClass().getClassLoader();
            Class uiClass = UIManager.getDefaults().getUIClass( target.getUIClassID(), uiClassLoader );
            System.out.println( "uiClass = " + uiClass );

            ComponentUI ui = UIManager.getUI( target );
            UIDefaults def = UIManager.getDefaults();
            System.out.println( "def = " + def.getClass().getName() );
            System.out.println( "ui = " + ui );

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
        new TestWIAUI( args ).startApplication();
    }
}
