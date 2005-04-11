/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test.util;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.model.ModelElement;

import java.awt.geom.Point2D;

/**
 * Particle
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistentParticle extends SimpleObservable implements ModelElement {
    private PersistentPoint2D position = new PersistentPoint2D( new Point2D.Double());
    private Vector2D velocity = new Vector2D.Double();
    private Vector2D acceleration = new Vector2D.Double();
    private Vector2D prevAcceleration = new Vector2D.Double();

    public PersistentParticle() {
    }

    protected PersistentParticle( Point2D position, Vector2D velocity,
                        Vector2D acceleration ) {
        setPosition( position );
        setVelocity( velocity );
        setAcceleration( acceleration );
    }

    ///////////////////////////////////////////
    // Setter and getters
    //
    public Vector2D getPrevAcceleration() {
        return prevAcceleration;
    }

    public void setPrevAcceleration( Vector2D prevAcceleration ) {
        this.prevAcceleration = prevAcceleration;
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

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity( Vector2D velocity ) {
        setVelocity( velocity.getX(), velocity.getY() );
    }

    public void setVelocity( double vx, double vy ) {
        velocity.setComponents( vx, vy );
        notifyObservers();
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( Vector2D acceleration ) {
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
        //        position.setLocation( xNew, yNew );

        // New velocity
        if( prevAcceleration == null ) {
            prevAcceleration = acceleration;
        }
        double vxNew = velocity.getX() + dt * ( acceleration.getX() + prevAcceleration.getX() ) / 2;
        double vyNew = velocity.getY() + dt * ( acceleration.getY() + prevAcceleration.getY() ) / 2;
        setVelocity( vxNew, vyNew );
        //        velocity.setComponents( vxNew, vyNew );

        // New acceleration
        prevAcceleration.setComponents( acceleration.getX(), acceleration.getY() );

        this.notifyObservers();
    }

    public void translate( double dx, double dy ) {
        position.setLocation( position.getX() + dx, position.getY() + dy );
        notifyObservers();
    }
}

