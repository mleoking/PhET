/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.waveinterference.WaveIntereferenceLookAndFeel;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:08:36 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class RunAllTests extends PhetApplication {
    public RunAllTests( String[] args ) {
        super( args, "All Tests", "", "" );
//        addModule( new TestTopView() );
//        addModule( new TestSideViewModule() );
        addModule( new TestWaveRotateModule() );
        addModule( new TestFaucetModule() );
        addModule( new TestPressureWaveModule() );
        addModule( new TestStripChartModule() );
        addModule( new TestTwoSourcesWaveRotate() );
        addModule( new TestSlits() );
        addModule( new TestWaveColor() );
        addModule( new TestScreenGraphic() );
        addModule( new TestSlitsAndScreen() );
    }

    public static void main( String[] args ) {
        new WaveIntereferenceLookAndFeel().initLookAndFeel();
        new RunAllTests( args ).startApplication();
    }
}
