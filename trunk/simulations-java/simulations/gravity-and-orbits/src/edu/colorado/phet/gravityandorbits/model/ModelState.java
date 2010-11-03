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

    public ModelState getNextState( double dt, int numSteps ) {
        ModelState state = this;
        for ( int i = 0; i < numSteps; i++ ) {
            state = state.getNextState( dt / numSteps );
        }
        return state;
    }

    public ModelState getNextState( double dt ) {
        //See http://www.fisica.uniud.it/~ercolessi/md/md/node21.html
        ArrayList<BodyState> newState = new ArrayList<BodyState>();
        for ( BodyState bodyState : bodyStates ) {
            ImmutableVector2D newPosition = bodyState.position.getAddedInstance( bodyState.velocity.getScaledInstance( dt ) ).getAddedInstance( bodyState.acceleration.getScaledInstance( dt * dt / 2 ) );
            ImmutableVector2D newVelocityHalfStep = bodyState.velocity.getAddedInstance( bodyState.acceleration.getScaledInstance( dt / 2 ) );
            ImmutableVector2D newAceleration = getForce( bodyState, newPosition ).getScaledInstance( -1.0 / bodyState.mass );
            ImmutableVector2D newVelocity = newVelocityHalfStep.getAddedInstance( newAceleration.getScaledInstance( dt / 2.0 ) );
            newState.add( new BodyState( newPosition, newVelocity, newAceleration, bodyState.mass ) );
        }
        return new ModelState( newState );
    }

    private ImmutableVector2D getForce( BodyState source, BodyState target, ImmutableVector2D newTargetPosition ) {
        return getUnitVector( source, newTargetPosition ).getScaledInstance( GravityAndOrbitsModel.G * source.mass * target.mass / source.distanceSquared( newTargetPosition ) );
    }

    private ImmutableVector2D getUnitVector( BodyState source, ImmutableVector2D newPosition ) {
        return newPosition.getSubtractedInstance( source.position ).getNormalizedInstance();
    }

    /**
     * Get the force on body at its proposed new position
     *
     * @param target
     * @param newTargetPosition
     * @return
     */
    public ImmutableVector2D getForce( BodyState target, ImmutableVector2D newTargetPosition ) {
        ImmutableVector2D sum = new ImmutableVector2D();
        for ( BodyState source : bodyStates ) {
            if ( source != target ) {
                sum = sum.getAddedInstance( getForce( source, target, newTargetPosition ) );
            }
        }
        return sum;
    }

    public BodyState getBodyState( int index ) {
        return bodyStates.get( index );
    }
}
