// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.model;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:54:29 AM
 */

public class ConstantPotential implements Potential {
    double value;

    public ConstantPotential() {
        this( 0.0 );
    }

    public ConstantPotential( double value ) {
        this.value = value;
    }

    public double getPotential( int i, int j, int time ) {
        return value;
    }
}
