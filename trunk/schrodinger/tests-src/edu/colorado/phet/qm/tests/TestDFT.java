/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests;

import edu.colorado.phet.qm.model.WaveDebugger;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.propagators.SplitOperatorPropagator;
import edu.colorado.phet.qm.view.complexcolormaps.MagnitudeColorMap;

/**
 * User: Sam Reid
 * Date: Dec 21, 2005
 * Time: 4:20:17 PM
 * Copyright (c) Dec 21, 2005 by Sam Reid
 */

public class TestDFT {

    public TestDFT() {
        Wavefunction wavefunction = new Wavefunction( 200, 200 );
        int squareSize = 10;
        for( int i = wavefunction.getWidth() / 2 - squareSize / 2; i <= wavefunction.getWidth() / 2 + squareSize / 2; i++ )
        {
            for( int k = wavefunction.getHeight() / 2 - squareSize / 2; k <= wavefunction.getHeight() / 2 + squareSize / 2; k++ )
            {
                wavefunction.setValue( i, k, 1.0, 0.0 );
            }
        }
        showWavefunction( "Original", wavefunction );

        wavefunction = new SplitOperatorPropagator().forwardFFT( wavefunction );
//        wavefunction.maximize();
        wavefunction.normalize();
        wavefunction.scale( 2.0 );
        showWavefunction( "FFT2D", wavefunction );
        wavefunction.printWaveToScreen();

        Wavefunction w3 = new SplitOperatorPropagator().inverseFFT( wavefunction );
        w3.normalize();
        showWavefunction( "FFT2D-1", w3 );
        Wavefunction w4 = new SplitOperatorPropagator().forwardFFT( wavefunction );
        w4.normalize();
        showWavefunction( "FFT2D-2forward", w4 );
    }

    private void showWavefunction( String title, Wavefunction wavefunction ) {
        WaveDebugger waveDebugger = new WaveDebugger( title, wavefunction, 2, 2 );
        waveDebugger.setComplexColorMap( new MagnitudeColorMap() );
        waveDebugger.setVisible( true );
    }

    public static void main( String[] args ) {
        new TestDFT().start();
    }

    private void start() {
    }

}
