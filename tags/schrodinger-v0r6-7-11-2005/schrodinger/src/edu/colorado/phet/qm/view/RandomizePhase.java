/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.model.Wavefunction;

import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jul 6, 2005
 * Time: 9:08:05 AM
 * Copyright (c) Jul 6, 2005 by Sam Reid
 */

public class RandomizePhase {
    static final Random random = new Random();

    public void randomizePhase( Wavefunction init ) {
        for( int i = 0; i < init.getWidth(); i++ ) {
            for( int j = 0; j < init.getHeight(); j++ ) {
                double theta = random.nextDouble() * 2 * Math.PI;
                Complex phaseTerm = Complex.exponentiateImaginary( theta );
                Complex val = init.valueAt( i, j );
                init.setValue( i, j, val.times( phaseTerm ) );
            }
        }
    }
}
