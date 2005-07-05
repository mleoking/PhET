/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.potentials;

import edu.colorado.phet.qm.model.Potential;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 11:07:25 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
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
