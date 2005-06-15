/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.operators;

import edu.colorado.phet.qm.model.Complex;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 2:29:00 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class ProbabilityValue {
    public double compute( Complex[][] wavefunction ) {
        int XMESH = wavefunction.length - 1;
        int YMESH = wavefunction[0].length - 1;
        Complex runningSum = new Complex();
        for( int i = 1; i < XMESH; i++ ) {
            for( int j = 1; j < YMESH; j++ ) {
                Complex psiStar = wavefunction[i][j].complexConjugate();
                Complex psi = wavefunction[i][j];
                Complex term = psiStar.times( psi );
                runningSum = runningSum.plus( term );
            }
        }
        return runningSum.abs();
    }
}
