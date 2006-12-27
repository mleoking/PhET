/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:20:04 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class ConstantPotential implements Potential {
    private double potential;

    public ConstantPotential() {
        this( 0.0 );
    }

    public ConstantPotential( double potential ) {
        this.potential = potential;
    }

    public double getPotential( int x, int y, int timestep ) {
        return potential;
    }
}
