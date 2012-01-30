// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * A particle that comes out of the shaker.
 * The particle falls towards the surface of the solution, may bounce off the wall
 * of the beaker, and disappears when it hits the surface of the solution (or bottom of the beaker,
 * if the beaker is empty.)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShakerParticle extends SoluteParticle {

    private ImmutableVector2D velocity;
    private ImmutableVector2D acceleration;

    public ShakerParticle( Solute solute, ImmutableVector2D location, double orientation, ImmutableVector2D initialVelocity, ImmutableVector2D acceleration ) {
        super( solute.particleColor, solute.particleSize, location, orientation );
        this.velocity = initialVelocity;
        this.acceleration = acceleration;
    }

    public void stepInTime( double deltaSeconds ) {

        ImmutableVector2D originalLocation = getLocation();

        // Propagate to new location
        velocity = velocity.plus( acceleration.times( deltaSeconds ) );
        setLocation( getLocation().plus( velocity.times( deltaSeconds ) ) );

        //TODO Did the particle hit the beaker walls? If so, change direction.

        //TODO Did the particle hit the surface of the solution or bottom of the beaker? If so, delete the particle and add an appropriate amount of solute to the solution.
    }
}
