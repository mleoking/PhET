/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.expectations;

import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 2:29:00 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class XValue {
    public double compute( Wavefunction w ) {
        w = w.copy();
        w.normalize();
        Complex sum = new Complex();
        for( int i = 0; i < w.getWidth(); i++ ) {
            for( int j = 0; j < w.getHeight(); j++ ) {
                Complex psiStar = w.valueAt( i, j ).complexConjugate();
                Complex observable = new Complex( ( (double)i ) / w.getWidth(), 0 );
                Complex psi = w.valueAt( i, j );
                Complex term = psiStar.times( observable ).times( psi );
                sum = sum.plus( term );
            }
        }
        return sum.abs();
    }
}
