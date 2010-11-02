package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * see http://www.fisica.uniud.it/~ercolessi/md/md/node21.html
 *
 * @author Sam Reid
 */
public class VelocityVerlet {
    public static class BodyState {
        public final ImmutableVector2D position;
        public final ImmutableVector2D velocity;
        public final ImmutableVector2D acceleration;
        public final double mass;

        public BodyState( ImmutableVector2D position, ImmutableVector2D velocity, ImmutableVector2D acceleration, double mass ) {
            this.position = position;
            this.velocity = velocity;
            this.acceleration = acceleration;
            this.mass = mass;
        }

        public double distanceSquared( BodyState planet ) {
            return position.getSubtractedInstance( planet.position ).getMagnitudeSq();
        }

        public ImmutableVector2D getUnitDirectionVector( BodyState planetState ) {
            return position.getSubtractedInstance( planetState.position ).getNormalizedInstance();
        }
    }

    public ArrayList<BodyState> getNextState( ArrayList<BodyState> state, double dt, PotentialField potentialField ) {
        ArrayList<BodyState> newState = new ArrayList<BodyState>();
        for ( BodyState bodyState : state ) {
            newState.add( getNextState( bodyState, dt, potentialField ) );
        }
        return newState;
    }

    private BodyState getNextState( BodyState body, double dt, PotentialField potentialField ) {//See http://www.fisica.uniud.it/~ercolessi/md/md/node21.html
        ImmutableVector2D newPosition = body.position.getAddedInstance( body.velocity.getScaledInstance( dt ) ).getAddedInstance( body.acceleration.getScaledInstance( dt * dt / 2 ) );
        ImmutableVector2D newVelocityHalfStep = body.velocity.getAddedInstance( body.acceleration.getScaledInstance( dt / 2 ) );
        ImmutableVector2D newAceleration = potentialField.getGradient( body, newPosition ).getScaledInstance( -1.0 / body.mass );
        ImmutableVector2D newVelocity = newVelocityHalfStep.getAddedInstance( newAceleration.getScaledInstance( dt / 2.0 ) );
        return new BodyState( newPosition, newVelocity, newAceleration, body.mass );
    }

    public static interface PotentialField {
        ImmutableVector2D getGradient( BodyState body, ImmutableVector2D newPosition );
    }
}
