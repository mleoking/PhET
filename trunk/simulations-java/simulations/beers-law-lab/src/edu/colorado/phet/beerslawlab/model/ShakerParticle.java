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

    private static final double MASS = 1E-6; //kg
    // Force due to gravity near the surface of the earth
    private static final ImmutableVector2D GRAVITY = new ImmutableVector2D( 0, -9.8 ); // m/s^2

    private ImmutableVector2D velocity;
    private ImmutableVector2D acceleration;

    public ShakerParticle( Solute solute, ImmutableVector2D location, double orientation, ImmutableVector2D initialVelocity ) {
        super( solute.particleColor, solute.particleSize, location, orientation );
        this.velocity = initialVelocity;
    }

    public void stepInTime( double deltaSeconds ) {

        ImmutableVector2D originalLocation = getLocation();

        // Propagate to new location
//        ImmutableVector2D appliedForce = GRAVITY.times( MASS );
//        acceleration = appliedForce.times( 1.0 / MASS );
//        velocity = velocity.plus( acceleration.times( deltaSeconds ) );
//        setLocation( getLocation().plus( velocity.times( deltaSeconds ) ) );

        //XXX straight down
        setLocation( new ImmutableVector2D( originalLocation.getX(), originalLocation.getY() + 3 ) );

        //TODO Did the particle hit the beaker walls? If so, change direction.

        //TODO Did the particle hit the surface of the solution or bottom of the beaker? If so, delete the particle and add an appropriate amount of solute to the solution.
    }
}
