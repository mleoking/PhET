// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity.common;


import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

public class Particle extends SimpleObservable
        implements ModelElement {

    public Particle( double d, double d1 ) {
        position = new MutableVector2D( d, d1 );
        velocity = new MutableVector2D();
        acceleration = new MutableVector2D();
    }

    public AbstractVector2D getPosition() {
        return position;
    }

    public AbstractVector2D getVelocity() {
        return velocity;
    }

    public void stepInTime( double d ) {
        Vector2D phetvector = acceleration.times( d );
        velocity = velocity.plus( phetvector );
        Vector2D phetvector1 = velocity.times( d );
        position = position.plus( phetvector1 );
        notifyObservers();
    }

    public void setAcceleration( double d, double d1 ) {
        acceleration = new MutableVector2D( d, d1 );
    }

    public void setVelocity( double d, double d1 ) {
        velocity = new MutableVector2D( d, d1 );
    }

    public void setPosition( double d, double d1 ) {
        position = new MutableVector2D( d, d1 );
    }

    AbstractVector2D position;
    AbstractVector2D velocity;
    MutableVector2D acceleration;
}
