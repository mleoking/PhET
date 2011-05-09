// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.gravityandorbits.model.Body;

/**
 * Serializable state for simsharing, stores the state of a single body
 *
 * @author Sam Reid
 */
public class PersistentBodyState implements IProguardKeepClass {
    private VectorState position;
    private VectorState velocity;
    private VectorState acceleration;
    private VectorState force;
    private double mass;
    private double diameter;
    private boolean userControlled;
    private boolean collided;

    public PersistentBodyState() {
    }

    public PersistentBodyState( Body body ) {
        this.position = new VectorState( body.getPosition() );
        velocity = new VectorState( body.getVelocity() );
        acceleration = new VectorState( body.getAcceleration() );
        force = new VectorState( body.getForceProperty().getValue() );
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

    public VectorState getPosition() {
        return position;
    }

    public void setPosition( VectorState position ) {
        this.position = position;
    }

    public VectorState getVelocity() {
        return velocity;
    }

    public void setVelocity( VectorState velocity ) {
        this.velocity = velocity;
    }

    public VectorState getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( VectorState acceleration ) {
        this.acceleration = acceleration;
    }

    public VectorState getForce() {
        return force;
    }

    public void setForce( VectorState force ) {
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
