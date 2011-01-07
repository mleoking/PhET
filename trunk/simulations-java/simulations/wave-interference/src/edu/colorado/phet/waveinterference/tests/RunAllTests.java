// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.phetcommon.application.PhetTestApplication;
import edu.colorado.phet.waveinterference.WaveIntereferenceLookAndFeel;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:08:36 AM
 */

public class RunAllTests extends PhetTestApplication {
    public RunAllTests( String[] args ) {
        super( args );
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
