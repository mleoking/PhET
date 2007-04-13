/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:54:29 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
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
