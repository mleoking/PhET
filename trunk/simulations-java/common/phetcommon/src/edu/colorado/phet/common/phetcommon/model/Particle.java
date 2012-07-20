// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * Default newtonian particle implementation (with Euler update).
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Particle extends SimpleObservable implements ModelElement {
    private Point2D position = new Point2D.Double();
    private MutableVector2D velocity = new MutableVector2D();
    private MutableVector2D acceleration = new MutableVector2D();
    private MutableVector2D prevAcceleration = new MutableVector2D();

    public Particle() {
    }

    public Object clone() {
        Particle clone = (Particle) super.clone();

        clone.position = new Point2D.Double( position.getX(), position.getY() );
        clone.velocity = new MutableVector2D( velocity );
        clone.acceleration = new MutableVector2D( acceleration );
        clone.prevAcceleration = new MutableVector2D( prevAcceleration );

        return clone;
    }

    protected Particle( Point2D position, MutableVector2D velocity,
                        MutableVector2D acceleration ) {
        setPosition( position );
        setVelocity( velocity );
        setAcceleration( acceleration );
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition( double x, double y ) {
        position.setLocation( x, y );
        notifyObservers();
    }

    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }

    public MutableVector2D getVelocity() {
        return velocity;
    }

    public void setVelocity( MutableVector2D velocity ) {
        setVelocity( velocity.getX(), velocity.getY() );
    }

    public void setVelocity( double vx, double vy ) {
        velocity.setComponents( vx, vy );
        notifyObservers();
    }

    public double getSpeed() {
        return velocity.getMagnitude();
    }

    public MutableVector2D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( MutableVector2D acceleration ) {
        setAcceleration( acceleration.getX(), acceleration.getY() );
    }

    public void setAcceleration( double ax, double ay ) {
        setAccelerationNoUpdate( ax, ay );
        notifyObservers();
    }

    public void setAccelerationNoUpdate( double ax, double ay ) {
        this.prevAcceleration.setComponents( acceleration.getX(), acceleration.getY() );
        this.acceleration.setComponents( ax, ay );
    }

    /**
     * Determines the new state of the body using the Verlet method
     *
     * @param dt
     */
    public void stepInTime( double dt ) {

        // New position
        double xNew = position.getX()
                      + dt * velocity.getX()
                      + dt * dt * acceleration.getX() / 2;
        double yNew = position.getY()
                      + dt * velocity.getY()
                      + dt * dt * acceleration.getY() / 2;
        this.setPosition( xNew, yNew );

        // New velocity
        if ( prevAcceleration == null ) {
            prevAcceleration = acceleration;
        }
        double vxNew = velocity.getX() + dt * ( acceleration.getX() + prevAcceleration.getX() ) / 2;
        double vyNew = velocity.getY() + dt * ( acceleration.getY() + prevAcceleration.getY() ) / 2;
        setVelocity( vxNew, vyNew );

        // New acceleration
        prevAcceleration.setComponents( acceleration.getX(), acceleration.getY() );

        this.notifyObservers();
    }

    public void translate( double dx, double dy ) {
        setPosition( position.getX() + dx, position.getY() + dy );
        notifyObservers();
    }
}

