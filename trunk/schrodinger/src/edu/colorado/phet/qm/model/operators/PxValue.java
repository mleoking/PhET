/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.operators;

import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.model.Wavefunction;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 2:29:00 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class PxValue {
    public double compute( Wavefunction w ) {
        Complex sum = new Complex();
        for( int i = 0; i < w.getWidth(); i++ ) {
            for( int j = 0; j < w.getHeight(); j++ ) {
                Complex psiStar = w.valueAt( i, j ).complexConjugate();
                Complex opPsi = getOpPsi( w, i, j );
                Complex term = psiStar.times( opPsi ).times( new Complex( 0, -1 ) );
                sum = sum.plus( term );
            }
        }
        if( Math.abs( sum.getImaginary() ) > 10E-8 ) {
            new RuntimeException( "imaginary part was substantial: c=" + sum ).printStackTrace();
        }
        return sum.getReal();
    }

    private Complex getOpPsi( Wavefunction w, int i, int j ) {
        Complex left = w.valueAt( i + 1, j );
        Complex right = w.valueAt( i - 1, j );

        Complex num = left.minus( right );//.plus( w[i][j].times( -2 ) );
        num.scale( 1 / 2.0 );
        return num;
    }
}
