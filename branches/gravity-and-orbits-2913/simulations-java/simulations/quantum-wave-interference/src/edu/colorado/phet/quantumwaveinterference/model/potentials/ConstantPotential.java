// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model.potentials;

import edu.colorado.phet.quantumwaveinterference.model.Potential;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:20:04 AM
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
