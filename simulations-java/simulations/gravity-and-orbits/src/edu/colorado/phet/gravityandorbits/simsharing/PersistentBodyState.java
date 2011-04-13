// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.gravityandorbits.model.Body;

//REVIEW class doc

/**
 * Serializable state for simsharing
 *
 * @author Sam Reid
 */
public class PersistentBodyState implements IProguardKeepClass {
    private VectorBean position;
    private VectorBean velocity;
    private VectorBean acceleration;
    private VectorBean force;
    private double mass;
    private double diameter;
    private boolean userControlled;
    private boolean collided;

    public PersistentBodyState() {
    }

    public PersistentBodyState( Body body ) {
        this.position = new VectorBean( body.getPosition() );
        velocity = new VectorBean( body.getVelocity() );
        acceleration = new VectorBean( body.getAcceleration() );
        force = new VectorBean( body.getForceProperty().getValue() );
        mass = body.getMass();
        diameter = body.getDiameter();
        userControlled = body.isUserControlled();
        collided = body.getCollidedProperty().getValue();
    }

    public void apply( Body body ) {
        body.setPosition( position.getX(), position.getY() );
        body.setVelocity( velocity.toImmutableVector2D() );
        body.setAcceleration( acceleration.toImmutableVector2D() );
        body.setForce( force.toImmutableVector2D() );
        body.setMass( mass );
        body.setDiameter( diameter );
        body.setUserControlled( userControlled );
        body.getCollidedProperty().setValue( collided );
    }

    public VectorBean getPosition() {
        return position;
    }

    public void setPosition( VectorBean position ) {
        this.position = position;
    }

    public VectorBean getVelocity() {
        return velocity;
    }

    public void setVelocity( VectorBean velocity ) {
        this.velocity = velocity;
    }

    public VectorBean getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( VectorBean acceleration ) {
        this.acceleration = acceleration;
    }

    public VectorBean getForce() {
        return force;
    }

    public void setForce( VectorBean force ) {
        this.force = force;
    }

    public double getMass() {
        return mass;
    }

    public void setMass( double mass ) {
        this.mass = mass;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter( double diameter ) {
        this.diameter = diameter;
    }

    public boolean isUserControlled() {
        return userControlled;
    }

    public void setUserControlled( boolean userControlled ) {
        this.userControlled = userControlled;
    }

    public boolean isCollided() {
        return collided;
    }

    public void setCollided( boolean collided ) {
        this.collided = collided;
    }
}
