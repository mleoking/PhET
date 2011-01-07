// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.tests;

import edu.colorado.phet.quantumwaveinterference.model.WaveDebugger;
import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.colorado.phet.quantumwaveinterference.model.propagators.QWIFFT2D;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.MagnitudeColorMap;

/**
 * User: Sam Reid
 * Date: Dec 21, 2005
 * Time: 4:20:17 PM
 */

public class TestDFT {

    public TestDFT() {
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
        Wavefunction wavefunction = new Wavefunction( 200, 200 );
        SquareWavefunction.setToSquare( wavefunction, 10 );
        showWavefunction( "Original", wavefunction );

        wavefunction = QWIFFT2D.forwardFFT( wavefunction );
        wavefunction.normalize();
        wavefunction.scale( 2.0 );
        showWavefunction( "FFT2D", wavefunction );
        wavefunction.printWaveToScreen();

        Wavefunction w3 = QWIFFT2D.inverseFFT( wavefunction );
        w3.normalize();
        showWavefunction( "FFT2D-1", w3 );
        Wavefunction w4 = QWIFFT2D.forwardFFT( wavefunction );
        w4.normalize();
        showWavefunction( "FFT2D-2forward", w4 );
    }


}
