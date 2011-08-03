// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

/**
 * Motion strategy that doesn't move the particle at all.  Used in composites such as sucrose crystals, since the individual atoms shouldn't move independently
 *
 * @author Sam Reid
 */
public class Motionless implements IUpdateStrategy {
    public void stepInTime( Particle particle, double dt ) {
    }
}
