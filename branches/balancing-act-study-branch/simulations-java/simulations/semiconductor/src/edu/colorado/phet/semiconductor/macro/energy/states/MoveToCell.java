// Copyright 2002-2012, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:52:53 PM
 */
public class MoveToCell implements BandParticleState {
    //    private BandParticle bp;
    private EnergyCell target;
    Speed speed;

    public MoveToCell( BandParticle bp, EnergyCell target, Speed speed ) {
//        this.bp = bp;
        this.target = target;
        this.speed = speed;
//        bp.setEnergyCell(target);
        bp.setEnergyCell( target );
        bp.setExcited( true );
    }

    public MoveToCell( BandParticle bp, EnergyCell target, final double speed ) {
        this( bp, target, new Speed() {
            public double getSpeed() {
                return speed;
            }
        } );
    }

    public boolean stepInTime( BandParticle particle, double dt ) {
        particle.setEnergyCell( target );
        double distPerStep = speed.getSpeed() * dt;
        MutableVector2D targetLoc = target.getPosition();
        MutableVector2D myLoc = particle.getPosition();
        Vector2D dx = targetLoc.minus( myLoc );
//        particle.setVelocity(dx.getScaledInstance(1.0/dt));
        double dist = dx.magnitude();
        if ( dist <= distPerStep ) {
            //got there
            particle.setPosition( targetLoc );
            return true;
        }
        else {
            Vector2D dir = dx.getInstanceOfMagnitude( distPerStep );
            Vector2D newLoc = myLoc.plus( dir );
            particle.setPosition( new MutableVector2D( newLoc.getX(), newLoc.getY() ) );
            return false;
        }
    }

}
