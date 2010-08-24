/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:52:53 PM
 */
public class MoveToPosition implements BandParticleState {
    private AbstractVector2DInterface target;
    Speed speed;

    public MoveToPosition( AbstractVector2DInterface target, Speed speed ) {
        this.target = target;
        this.speed = speed;
    }

    public MoveToPosition( AbstractVector2DInterface target, final double speed ) {
        this( target, new Speed() {
            public double getSpeed() {
                return speed;
            }
        } );
    }

    public boolean stepInTime( BandParticle particle, double dt ) {
        particle.setEnergyCell( null );
        double distPerStep = speed.getSpeed() * dt;
        AbstractVector2DInterface targetLoc = target;
        AbstractVector2DInterface myLoc = particle.getPosition();
        AbstractVector2DInterface dx = targetLoc.getSubtractedInstance( myLoc );
//        particle.setVelocity(dx.getScaledInstance(1.0/dt));
        double dist = dx.getMagnitude();
        if ( dist <= distPerStep ) {
            //got there
            particle.setPosition( new Vector2D( targetLoc.getX(), targetLoc.getY() ) );
            return true;
        }
        else {
            AbstractVector2DInterface dir = dx.getInstanceOfMagnitude( distPerStep );
//            dir = new AbstractVector2D(-Math.abs(dir.getX()), dir.getEnergy());
            AbstractVector2DInterface newLoc = myLoc.getAddedInstance( dir );
            particle.setPosition( new Vector2D( newLoc.getX(), newLoc.getY() ) );
            return false;
        }
    }

}
