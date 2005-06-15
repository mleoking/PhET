/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.operators;

import edu.colorado.phet.qm.model.Complex;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 2:29:00 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class PxValue {
    public double compute( Complex[][] w ) {
        int XMESH = w.length - 1;
        int YMESH = w[0].length - 1;
        Complex sum = new Complex();
        for( int i = 1; i < XMESH; i++ ) {
            for( int j = 1; j < YMESH; j++ ) {
                Complex psiStar = w[i][j].complexConjugate();
                Complex opPsi = getOpPsi( w, i, j );
                Complex term = psiStar.times( opPsi ).times( new Complex( 0, -1 ) );
                sum = sum.plus( term );
            }
        }
//        System.out.println( "sum = " + sum );
        if( Math.abs( sum.getImaginary() ) > 10E-8 ) {
            new RuntimeException( "imaginary part was substantial: c=" + sum ).printStackTrace();
        }
        return sum.getReal();
    }

    private Complex getOpPsi( Complex[][] w, int i, int j ) {
        Complex num = w[i + 1][j].minus( w[i - 1][j] );//.plus( w[i][j].times( -2 ) );
        num.scale( 1 / 2.0 );
        return num;
    }
}
