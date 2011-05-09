// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.common;


import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

public class Particle extends SimpleObservable
        implements ModelElement {

    public Particle( double d, double d1 ) {
        position = new Vector2D( d, d1 );
        velocity = new Vector2D();
        acceleration = new Vector2D();
    }

    public ImmutableVector2D getPosition() {
        return position;
    }

    public ImmutableVector2D getVelocity() {
        return velocity;
    }

    public void stepInTime( double d ) {
        ImmutableVector2D phetvector = acceleration.getScaledInstance( d );
        velocity = velocity.getAddedInstance( phetvector );
        ImmutableVector2D phetvector1 = velocity.getScaledInstance( d );
        position = position.getAddedInstance( phetvector1 );
        notifyObservers();
    }

    public void setAcceleration( double d, double d1 ) {
        acceleration = new Vector2D( d, d1 );
    }

    public void setVelocity( double d, double d1 ) {
        velocity = new Vector2D( d, d1 );
    }

    public void setPosition( double d, double d1 ) {
        position = new Vector2D( d, d1 );
    }

    ImmutableVector2D position;
    ImmutableVector2D velocity;
    Vector2D acceleration;
}
