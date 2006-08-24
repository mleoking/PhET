/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.waveinterference.model.Oscillator;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 1:37:43 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class TestTwoSourcesWaveRotate extends TestWaveRotateModule {
    private Oscillator oscillator2;

    public TestTwoSourcesWaveRotate() {
        super( "2 Sources" );
        oscillator2 = new Oscillator( getWaveModel() );
        oscillator2.setLocation( 4, 20 );
        oscillator2.setPeriod( 2 );
    }

    protected void step() {
        super.step();
        oscillator2.setTime( getTime() );
    }

    public static void main( String[] args ) {
        PhetApplication a = new PhetApplication( args, "Test 2 Sources", "", "" );
        a.addModule( new TestTwoSourcesWaveRotate() );
        a.startApplication();
    }
}
