/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:52:53 PM
 * Copyright (c) Jan 18, 2004 by Sam Reid
 */
public class MoveToCell implements BandParticleState {
//    private BandParticle bp;
    private BandParticle bp;
    private EnergyCell target;
    Speed speed;

    public MoveToCell( BandParticle bp, EnergyCell target, Speed speed ) {
        this.bp = bp;
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
//        target.setOwner(particle);
        particle.setEnergyCell( target );
//        target.setOwner(particle);
        double distPerStep = speed.getSpeed() * dt;
        PhetVector targetLoc = target.getPosition();
        PhetVector myLoc = particle.getPosition();
        PhetVector dx = targetLoc.getSubtractedInstance( myLoc );
//        particle.setVelocity(dx.getScaledInstance(1.0/dt));
        double dist = dx.getMagnitude();
        if( dist <= distPerStep ) {
            //got there
            particle.setPosition( targetLoc );
            return true;
        }
        else {
            PhetVector dir = dx.getInstanceForMagnitude( distPerStep );
            PhetVector newLoc = myLoc.getAddedInstance( dir );
            particle.setPosition( newLoc );
            return false;
        }
    }

    public boolean isMoving() {
        return true;
    }

}
