/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.FaucetGraphic;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 11:33:59 PM
 * Copyright (c) Apr 12, 2006 by Sam Reid
 */

public class TestFaucetDropTiming {

    public static void main( String[] args ) {
        WaveModel waveModel = new WaveModel( 50, 50 );
        WaveModelGraphic waveModelGraphic = new WaveModelGraphic( waveModel );
        Oscillator oscillator = new Oscillator( waveModel );
        oscillator.setPeriod( 2 );
        FaucetGraphic faucetGraphic = new FaucetGraphic( new PSwingCanvas(), waveModel, oscillator, waveModelGraphic.getLatticeScreenCoordinates() );
        FaucetGraphic.debugNearestTime( faucetGraphic, 31.2 );
    }
}
