package edu.colorado.phet.cck.elements.particles;

/**
 * User: Sam Reid
 * Date: Nov 21, 2003
 * Time: 9:57:34 AM
 * Copyright (c) Nov 21, 2003 by Sam Reid
 */
public interface Propagator {
    public void propagate( BranchParticle bp, double dt );

    void stepInTime( double dt );
}
