// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.gravityandorbits.model.Body;

/**
 * @author Sam Reid
 */
public class PersistentBodyState implements Serializable {
    private final ImmutableVector2D position;
    private final ImmutableVector2D velocity;
    private final ImmutableVector2D acceleration;
    private final ImmutableVector2D force;
    private final double mass;
    private final double diameter;
    private final boolean userControlled;
    private final boolean collided;

    public PersistentBodyState( Body body ) {
        this.position = body.getPosition();
        velocity = body.getVelocity();
        acceleration = body.getAcceleration();
        force = body.getForceProperty().getValue();
        mass = body.getMass();
        diameter = body.getDiameter();
        userControlled = body.isUserControlled();
        collided = body.getCollidedProperty().getValue();
    }

    public void apply( Body body ) {
        body.setPosition( position.getX(), position.getY() );
        body.setVelocity( velocity );
        body.setAcceleration( acceleration );
        body.setForce( force );
        body.setMass( mass );
        body.setDiameter( diameter );
        body.setUserControlled( userControlled );
        body.getCollidedProperty().setValue( collided );
    }
}
