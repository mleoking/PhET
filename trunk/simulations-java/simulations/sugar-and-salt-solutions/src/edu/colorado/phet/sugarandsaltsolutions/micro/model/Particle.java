package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A particle is an indivisible object with a position such as Na+ or a sugar molecule.
 *
 * @author Sam Reid
 */
public abstract class Particle {
    //Interface for setting and observing the position
    public final Property<ImmutableVector2D> position;

    //Interface for setting and observing the velocity
    public final Property<ImmutableVector2D> velocity;

    public Particle( ImmutableVector2D position ) {
        this.position = new Property<ImmutableVector2D>( position );
        this.velocity = new Property<ImmutableVector2D>( new ImmutableVector2D() );
    }

    //Given the specified acceleration from external forces (such as gravity), perform an Euler integration step to move the particle forward in time
    public void stepInTime( ImmutableVector2D acceleration, double dt ) {
        velocity.set( velocity.get().plus( acceleration.times( dt ) ) );
        position.set( position.get().plus( velocity.get().times( dt ) ) );
    }

    //Get a shape for the particle for purposes of collision detection with beaker solution and beaker walls
    public abstract Shape getShape();
}