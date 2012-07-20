// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.tests;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.quantumwaveinterference.model.WaveDebugger;
import edu.colorado.phet.quantumwaveinterference.model.WaveSetup;
import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.colorado.phet.quantumwaveinterference.model.math.Complex;
import edu.colorado.phet.quantumwaveinterference.model.waves.GaussianWave2D;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.GrayscaleColorMap;

/**
 * User: Sam Reid
 * Date: Dec 21, 2005
 * Time: 4:20:17 PM
 */

public class TestUnits {
    public TestUnits() {
        Wavefunction wavefunction = new Wavefunction( 50, 50 );
        double desiredWavelengthY = 10;
        double hbar = 1.0;
        double momentumY = 2 * Math.PI * hbar / desiredWavelengthY;
        System.out.println( "momentumY = " + momentumY );
        GaussianWave2D gaussianWave2D = new GaussianWave2D( new Point2D.Double( 25, 25 ), new MutableVector2D( 0, momentumY ), 3.5 * 2, 1.0 );
        WaveSetup waveSetup = new WaveSetup( gaussianWave2D );
        waveSetup.initialize( wavefunction );

        WaveDebugger waveDebugger = new WaveDebugger( "wave", wavefunction );
        waveDebugger.setComplexColorMap( new GrayscaleColorMap.Real() );
        waveDebugger.setVisible( true );

        DecimalFormat formatter = new DecimalFormat( "0.00" );
        for ( int k = 0; k < wavefunction.getHeight(); k++ ) {
            for ( int i = 0; i < wavefunction.getWidth(); i++ ) {
                Complex val = wavefunction.valueAt( i, k );
                String s = formatter.format( val.getReal() );
                if ( s.equals( formatter.format( -0.000000001 ) ) ) {
                    s = formatter.format( 0 );
                }
                String spaces = "  ";
                if ( s.startsWith( "-" ) ) {
                    spaces = " ";
                }
                System.out.print( spaces + s );

            }
            System.out.println( "" );
        }
    }

    public static void main( String[] args ) {
        new TestUnits().start();
    }

    private void start() {
    }
}
