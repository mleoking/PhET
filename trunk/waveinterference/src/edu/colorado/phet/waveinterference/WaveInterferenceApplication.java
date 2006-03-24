/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.waveinterference.tests.TestTopView;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 10:52:38 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class WaveInterferenceApplication extends PhetApplication {
    private static String VERSION = "0.02";

    public WaveInterferenceApplication( String[] args ) {
        super( args, "Wave Interference " + VERSION, "Wave Interference simulation", VERSION );

        addModule( new WaterModule() );
        addModule( new SoundModule() );
        addModule( new LightModule() );
    }

    public static void main( String[] args ) {
        TestTopView.main( args );
    }
}
