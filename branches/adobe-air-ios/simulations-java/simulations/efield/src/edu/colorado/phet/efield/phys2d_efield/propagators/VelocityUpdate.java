// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.phys2d_efield.propagators;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.Propagator;

public class VelocityUpdate
        implements Propagator {

    public VelocityUpdate( double d ) {
        max = d;
    }

    public void propagate( double d, Particle particle ) {
        DoublePoint doublepoint = particle.getAcceleration();
        DoublePoint doublepoint1 = particle.getVelocity();
        DoublePoint doublepoint2 = doublepoint.multiply( d );
        DoublePoint doublepoint3 = doublepoint1.add( doublepoint2 );
        double d1 = doublepoint3.getLength();
        if ( d1 > max ) {
            doublepoint3 = doublepoint3.multiply( max / d1 );
        }
        particle.setVelocity( doublepoint3 );
    }

    double max;
}
