/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Dec 31, 2003
 * Time: 2:48:10 PM
 * Copyright (c) Dec 31, 2003 by Sam Reid
 */
public class Particle extends SimpleObservable implements ModelElement {
    PhetVector position;
    PhetVector velocity;
    PhetVector acceleration;
    double mass;

    public Particle( double x, double y ) {
        this.position = new PhetVector( x, y );
        this.velocity = new PhetVector();
        this.acceleration = new PhetVector();
    }

    public Particle( PhetVector position ) {
        this( position.getX(), position.getY() );
    }

    public PhetVector getPosition() {
        return position;
    }

    public PhetVector getVelocity() {
        return velocity;
    }

    public PhetVector getAcceleration() {
        return acceleration;
    }

    public double getMass() {
        return mass;
    }

    public void stepInTime( double dt ) {
        //acceleration doesn't change here.
        PhetVector dv = acceleration.getScaledInstance( dt );
        this.velocity = velocity.getAddedInstance( dv );
        PhetVector dx = velocity.getScaledInstance( dt );
        this.position = position.getAddedInstance( dx );
        updateObservers();
    }

    public void setAcceleration( double ax, double ay ) {
        this.acceleration = new PhetVector( ax, ay );
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public void setVelocity( double vx, double vy ) {
        this.velocity = new PhetVector( vx, vy );
    }

    public void setPosition( double x, double y ) {
        this.position = new PhetVector( x, y );
    }

    public void setPosition( PhetVector position ) {
        this.position = new PhetVector( position );
    }

    public void translate( double dx, double dy ) {
        setPosition( getX() + dx, getY() + dy );
    }

}
