/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 2:29:00 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class YValue {
    public double compute( Complex[][] w ) {
        int XMESH = w.length - 1;
        int YMESH = w[0].length - 1;
        Complex sum = new Complex();
        for( int i = 1; i < XMESH; i++ ) {
            for( int j = 1; j < YMESH; j++ ) {
                Complex psiStar = w[i][j].complexConjugate();
                Complex observable = new Complex( ( (double)j / YMESH ), 0 );
                Complex psi = w[i][j];
                Complex term = psiStar.times( observable ).times( psi );
                sum = sum.plus( term );
            }
        }
        return sum.abs();
    }
}
