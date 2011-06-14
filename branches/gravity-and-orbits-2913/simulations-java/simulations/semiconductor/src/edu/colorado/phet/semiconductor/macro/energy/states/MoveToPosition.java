// Copyright 2002-2011, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:52:53 PM
 */
public class MoveToPosition implements BandParticleState {
    private ImmutableVector2D target;
    Speed speed;

    public MoveToPosition( ImmutableVector2D target, Speed speed ) {
        this.target = target;
        this.speed = speed;
    }

    public MoveToPosition( ImmutableVector2D target, final double speed ) {
        this( target, new Speed() {
            public double getSpeed() {
                return speed;
            }
        } );
    }

    public boolean stepInTime( BandParticle particle, double dt ) {
        particle.setEnergyCell( null );
        double distPerStep = speed.getSpeed() * dt;
        ImmutableVector2D targetLoc = target;
        ImmutableVector2D myLoc = particle.getPosition();
        ImmutableVector2D dx = targetLoc.getSubtractedInstance( myLoc );
//        particle.setVelocity(dx.getScaledInstance(1.0/dt));
        double dist = dx.getMagnitude();
        if ( dist <= distPerStep ) {
            //got there
            particle.setPosition( new Vector2D( targetLoc.getX(), targetLoc.getY() ) );
            return true;
        }
        else {
            ImmutableVector2D dir = dx.getInstanceOfMagnitude( distPerStep );
//            dir = new AbstractVector2D(-Math.abs(dir.getX()), dir.getEnergy());
            ImmutableVector2D newLoc = myLoc.getAddedInstance( dir );
            particle.setPosition( new Vector2D( newLoc.getX(), newLoc.getY() ) );
            return false;
        }
    }

}
