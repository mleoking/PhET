// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.bands.states;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.bands.EnergyCell;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands.states:
//            Waiting, Speed

public class MoveTo
        implements BandParticleState {

    public MoveTo( EnergyCell energycell, Speed speed1 ) {
        target = energycell;
        speed = speed1;
    }

    public BandParticleState stepInTime( BandParticle bandparticle, double d ) {
        double d1 = 0.0D;
        if( bandparticle.getX() < bandparticle.getEnergyLevel().getLine().getX1() - d1 ) {
            bandparticle.setX( bandparticle.getEnergyLevel().getLine().getX2() + d1 );
        }
        double d2 = speed.getSpeed() * d;
        PhetVector phetvector = target.getPosition();
        PhetVector phetvector1 = bandparticle.getPosition();
        PhetVector phetvector2 = phetvector.getSubtractedInstance( phetvector1 );
        double d3 = phetvector2.getMagnitude();
        if( d3 <= d2 ) {
            bandparticle.setPosition( phetvector );
            return new Waiting();
        }
        else {
            PhetVector phetvector3 = phetvector2.getInstanceForMagnitude( d2 );
            phetvector3 = new PhetVector( -Math.abs( phetvector3.getX() ), phetvector3.getY() );
            PhetVector phetvector4 = phetvector1.getAddedInstance( phetvector3 );
            bandparticle.setPosition( phetvector4 );
            return this;
        }
    }

    private EnergyCell target;
    Speed speed;
}
