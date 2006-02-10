/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests;

import edu.colorado.phet.qm.model.WaveDebugger;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;
import edu.colorado.phet.qm.model.propagators.SplitOperatorPropagator;
import edu.colorado.phet.qm.view.complexcolormaps.MagnitudeColorMap;

import java.text.DecimalFormat;

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
        printWaveToScreen( wavefunction );
    }

    private void showWavefunction( String title, Wavefunction wavefunction ) {
        WaveDebugger waveDebugger = new WaveDebugger( title, wavefunction, 2, 2 );
        waveDebugger.setComplexColorMap( new MagnitudeColorMap() );
        waveDebugger.setVisible( true );
    }

    private void printWaveToScreen( Wavefunction wavefunction ) {
        DecimalFormat formatter = new DecimalFormat( "0.00" );
        for( int k = 0; k < wavefunction.getHeight(); k++ ) {
            for( int i = 0; i < wavefunction.getWidth(); i++ ) {
                Complex val = wavefunction.valueAt( i, k );
                String s = formatter.format( val.getReal() );
                if( s.equals( formatter.format( -0.000000001 ) ) ) {
                    s = formatter.format( 0 );
                }
                String spaces = "  ";
                if( s.startsWith( "-" ) ) {
                    spaces = " ";
                }
                System.out.print( spaces + s );

            }
            System.out.println( "" );
        }
    }

    public static void main( String[] args ) {
        new TestDFT().start();
    }

    private void start() {
    }

}
