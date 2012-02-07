// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import edu.colorado.phet.beerslawlab.common.model.Solute;
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

    private Solute solute;
    private ImmutableVector2D velocity;
    private ImmutableVector2D acceleration;

    public ShakerParticle( Solute solute, ImmutableVector2D location, double orientation, ImmutableVector2D initialVelocity, ImmutableVector2D acceleration ) {
        super( solute.particleColor, solute.particleSize, location, orientation );
        this.solute = solute;
        this.velocity = initialVelocity;
        this.acceleration = acceleration;
    }

    // Propagates the particle to a new location
    public void stepInTime( double deltaSeconds, Beaker beaker ) {

        velocity = velocity.plus( acceleration.times( deltaSeconds ) );
        ImmutableVector2D newLocation = getLocation().plus( velocity.times( deltaSeconds ) );

        /*
         * Did the particle hit the left wall of the beaker? If so, change direction.
         * Note that this is a very simplified model, and only deals with the left wall of the beaker,
         * which is the only wall that the particles can hit in practice.
         */
        final double minX = beaker.getMinX() + solute.particleSize;
        if ( newLocation.getX() <= minX ) {
            newLocation = new ImmutableVector2D( minX, newLocation.getY() );
            velocity = new ImmutableVector2D( Math.abs( velocity.getX() ), velocity.getY() );
        }

        setLocation( newLocation );
    }
}
