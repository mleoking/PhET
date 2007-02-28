/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jan 5, 2006
 * Time: 1:26:48 AM
 * Copyright (c) Jan 5, 2006 by Sam Reid
 */

public class CollapseComputation {
    private static Random random = new Random();

    public Point getCollapsePoint( Wavefunction wavefunction, Rectangle bounds ) {
        //compute a probability model for each dA
        Wavefunction copy = wavefunction.copy();
        for( int i = 0; i < copy.getWidth(); i++ ) {
            for( int j = 0; j < copy.getHeight(); j++ ) {
                if( !bounds.contains( i, j ) ) {
                    copy.valueAt( i, j ).zero();
                }
            }
        }

        copy.normalize();//in case we care
        Complex runningSum = new Complex();
        double rnd = random.nextDouble();

        for( int i = 0; i < copy.getWidth(); i++ ) {
            for( int j = 0; j < copy.getHeight(); j++ ) {
                Complex psiStar = copy.valueAt( i, j ).complexConjugate();
                Complex psi = copy.valueAt( i, j );
                Complex term = psiStar.times( psi );
                double pre = runningSum.abs();
                runningSum = runningSum.plus( term );
                double post = runningSum.abs();
                if( pre <= rnd && rnd <= post ) {
                    return new Point( i, j );
                }
            }
        }
        new RuntimeException( "No collapse point." ).printStackTrace();
        return new Point( 0, 0 );
    }
}
