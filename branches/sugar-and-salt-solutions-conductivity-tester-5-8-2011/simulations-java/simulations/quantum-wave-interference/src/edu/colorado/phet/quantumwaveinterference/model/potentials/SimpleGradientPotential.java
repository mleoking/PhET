// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model.potentials;

import edu.colorado.phet.quantumwaveinterference.model.Potential;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 11:07:25 AM
 */

public class SimpleGradientPotential implements Potential {
    private double scale;

    public SimpleGradientPotential( double scale ) {
        this.scale = scale;
    }

    public double getPotential( int x, int y, int timestep ) {
        return x * scale;//+y*scale;
    }
}
