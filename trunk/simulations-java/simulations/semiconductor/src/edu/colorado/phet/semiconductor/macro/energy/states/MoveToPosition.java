/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.util.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:52:53 PM
 */
public class MoveToPosition implements BandParticleState {
    private PhetVector target;
    Speed speed;

    public MoveToPosition( PhetVector target, Speed speed ) {
        this.target = target;
        this.speed = speed;
    }

    public MoveToPosition( PhetVector target, final double speed ) {
        this( target, new Speed() {
            public double getSpeed() {
                return speed;
            }
        } );
    }

    public boolean stepInTime( BandParticle particle, double dt ) {
        particle.setEnergyCell( null );
        double distPerStep = speed.getSpeed() * dt;
        PhetVector targetLoc = target;
        PhetVector myLoc = particle.getPosition();
        PhetVector dx = targetLoc.getSubtractedInstance( myLoc );
//        particle.setVelocity(dx.getScaledInstance(1.0/dt));
        double dist = dx.getMagnitude();
        if ( dist <= distPerStep ) {
            //got there
            particle.setPosition( targetLoc );
            return true;
        }
        else {
            PhetVector dir = dx.getInstanceForMagnitude( distPerStep );
//            dir = new PhetVector(-Math.abs(dir.getX()), dir.getEnergy());
            PhetVector newLoc = myLoc.getAddedInstance( dir );
            particle.setPosition( newLoc );
            return false;
        }
    }

}
