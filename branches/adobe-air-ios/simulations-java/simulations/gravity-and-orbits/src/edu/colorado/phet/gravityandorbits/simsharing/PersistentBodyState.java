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

    public PersistentBodyState( Body body ) {
        this.position = new VectorState( body.getPosition() );
        velocity = new VectorState( body.getVelocity() );
        acceleration = new VectorState( body.getAcceleration() );
        force = new VectorState( body.getForceProperty().get() );
        mass = body.getMass();
        diameter = body.getDiameter();
        userControlled = body.isUserControlled();
        collided = body.getCollidedProperty().get();
    }

    public void apply( Body body ) {
        body.setPosition( position.x, position.y );
        body.setVelocity( velocity.toImmutableVector2D() );
        body.setAcceleration( acceleration.toImmutableVector2D() );
        body.setForce( force.toImmutableVector2D() );
        body.setMass( mass );
        body.setDiameter( diameter );
        body.setUserControlled( userControlled );
        body.getCollidedProperty().set( collided );
    }

    public VectorState getPosition() {
        return position;
    }

    @Override public String toString() {
        return "PersistentBodyState{" +
               "position=" + position +
               ", velocity=" + velocity +
               ", acceleration=" + acceleration +
               ", force=" + force +
               ", mass=" + mass +
               ", diameter=" + diameter +
               ", userControlled=" + userControlled +
               ", collided=" + collided +
               '}';
    }
}