// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits;

import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.gravityandorbits.model.Body;

/**
 * @author Sam Reid
 */
public class PersistentBodyState implements Serializable {
    private ImmutableVector2D position;
    private ImmutableVector2D velocity;
    private ImmutableVector2D acceleration;
    private ImmutableVector2D force;
    double mass;
    double diameter;
    boolean userControlled;

    public PersistentBodyState( Body body ) {
        this.position = body.getPosition();
        velocity = body.getVelocity();
        acceleration = body.getAcceleration();
        force = body.getForceProperty().getValue();
        mass = body.getMass();
        diameter = body.getDiameter();
        userControlled = body.isUserControlled();
    }

    public void apply( Body body ) {
        body.setPosition( position.getX(), position.getY() );
        body.setVelocity( velocity );
        body.setAcceleration( acceleration );
        body.setForce( force );
        body.setMass( mass );
        body.setDiameter( diameter );
        body.setUserControlled( userControlled );
    }
}
