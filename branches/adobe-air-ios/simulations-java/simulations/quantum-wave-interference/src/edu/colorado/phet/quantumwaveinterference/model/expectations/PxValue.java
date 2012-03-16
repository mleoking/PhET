// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model.expectations;

import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.colorado.phet.quantumwaveinterference.model.math.Complex;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 2:29:00 PM
 */

public class PxValue {
    public double compute( Wavefunction w ) {
//        double mag = w.getMagnitude();
        Complex sum = new Complex();
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {
                Complex psiStar = w.valueAt( i, j ).complexConjugate();
                Complex opPsi = getOpPsi( w, i, j );
                Complex term = psiStar.times( opPsi ).times( new Complex( 0, -1 ) );
                sum = sum.plus( term );
            }
        }
        if( Math.abs( sum.getImaginary() ) > 10E-3 ) {
            new RuntimeException( "imaginary part was substantial: c=" + sum ).printStackTrace();
        }
        return sum.getReal();
    }

    private Complex getOpPsi( Wavefunction w, int i, int j ) {
        Complex left = w.valueAt( i + 1, j );
        Complex right = w.valueAt( i - 1, j );

        return left.minus( right ).times( 0.5 );
    }
}
