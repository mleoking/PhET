// Copyright 2002-2012, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.common;


import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Dec 31, 2003
 * Time: 2:48:10 PM
 */
public class Particle extends SimpleObservable implements ModelElement {
    Vector2D position;
    Vector2D velocity;
    MutableVector2D acceleration;

    public Particle( double x, double y ) {
        this.position = new Vector2D( x, y );
        this.velocity = new Vector2D();
        this.acceleration = new MutableVector2D();
    }

    public Particle( Vector2D position ) {
        this( position.getX(), position.getY() );
    }

    public Vector2D getPosition() {
        return position;
    }

    public void stepInTime( double dt ) {
        //acceleration doesn't change here.
        Vector2D dv = acceleration.times( dt );
        this.velocity = velocity.plus( dv );
        Vector2D dx = velocity.times( dt );
        this.position = position.plus( dx );
        notifyObservers();
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public void setPosition( double x, double y ) {
        this.position = new Vector2D( x, y );
    }

    public void translate( double dx, double dy ) {
        setPosition( getX() + dx, getY() + dy );
    }

}
