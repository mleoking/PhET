// Copyright 2002-2012, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.common;


import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Dec 31, 2003
 * Time: 2:48:10 PM
 */
public class Particle extends SimpleObservable implements ModelElement {
    ImmutableVector2D position;
    ImmutableVector2D velocity;
    Vector2D acceleration;

    public Particle( double x, double y ) {
        this.position = new ImmutableVector2D( x, y );
        this.velocity = new ImmutableVector2D();
        this.acceleration = new Vector2D();
    }

    public Particle( ImmutableVector2D position ) {
        this( position.getX(), position.getY() );
    }

    public ImmutableVector2D getPosition() {
        return position;
    }

    public void stepInTime( double dt ) {
        //acceleration doesn't change here.
        ImmutableVector2D dv = acceleration.getScaledInstance( dt );
        this.velocity = velocity.getAddedInstance( dv );
        ImmutableVector2D dx = velocity.getScaledInstance( dt );
        this.position = position.getAddedInstance( dx );
        notifyObservers();
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public void setPosition( double x, double y ) {
        this.position = new ImmutableVector2D( x, y );
    }

    public void translate( double dx, double dy ) {
        setPosition( getX() + dx, getY() + dy );
    }

}
