/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 10:52:38 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class WaveInterferenceApplication extends PhetApplication {
    private static String VERSION = "0.07";

    public WaveInterferenceApplication( String[] args ) {
        super( args, "Wave Interference", "Wave Interference simulation", VERSION );

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

    public static void main( String[] args ) throws IllegalAccessException, UnsupportedLookAndFeelException, InstantiationException, ClassNotFoundException {
        WaveIntereferenceLookAndFeel.initLookAndFeel();
        UIManager.setLookAndFeel( smooth.SmoothLookAndFeelFactory.getSystemLookAndFeelClassName() );
        new WaveInterferenceApplication( args ).startApplication();
    }
}
