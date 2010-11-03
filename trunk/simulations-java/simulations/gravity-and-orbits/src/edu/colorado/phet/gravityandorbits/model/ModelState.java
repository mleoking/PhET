package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * see http://www.fisica.uniud.it/~ercolessi/md/md/node21.html
 *
 * @author Sam Reid
 */
public class ModelState {
    ArrayList<BodyState> bodyStates;

    public ModelState( ArrayList<BodyState> bodyStates ) {
        this.bodyStates = bodyStates;
    }

    public ModelState getNextState( double dt ) {
        //See http://www.fisica.uniud.it/~ercolessi/md/md/node21.html
        ArrayList<BodyState> newState = new ArrayList<BodyState>();
        for ( BodyState bodyState : bodyStates ) {
            ImmutableVector2D newPosition = bodyState.position.getAddedInstance( bodyState.velocity.getScaledInstance( dt ) ).getAddedInstance( bodyState.acceleration.getScaledInstance( dt * dt / 2 ) );
            ImmutableVector2D newVelocityHalfStep = bodyState.velocity.getAddedInstance( bodyState.acceleration.getScaledInstance( dt / 2 ) );
            ImmutableVector2D newAceleration = getGradient( bodyState, newPosition ).getScaledInstance( -1.0 / bodyState.mass );
            ImmutableVector2D newVelocity = newVelocityHalfStep.getAddedInstance( newAceleration.getScaledInstance( dt / 2.0 ) );
            newState.add( new BodyState( newPosition, newVelocity, newAceleration, bodyState.mass ) );
        }
        return new ModelState( newState );
    }

    private ImmutableVector2D getForce( BodyState source, BodyState target, ImmutableVector2D newPosition ) {
        return target.getUnitDirectionVector( source ).getScaledInstance( GravityAndOrbitsModel.G * source.mass * target.mass / source.distanceSquared( newPosition ) );
    }

    public ImmutableVector2D getGradient( BodyState body, ImmutableVector2D newPosition ) {
        ImmutableVector2D sum = new ImmutableVector2D();
        for ( BodyState bodyState : bodyStates ) {
            if ( bodyState != body ) {
                sum = sum.getAddedInstance( getForce( bodyState, body, newPosition ) );
            }
        }
        return sum;
    }
}
