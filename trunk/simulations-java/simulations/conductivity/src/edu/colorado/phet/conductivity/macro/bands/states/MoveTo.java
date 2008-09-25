// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.bands.states;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.conductivity.macro.bands.BandParticle;
import edu.colorado.phet.conductivity.macro.bands.BandParticleState;
import edu.colorado.phet.conductivity.macro.bands.EnergyCell;

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
        if ( bandparticle.getX() < bandparticle.getEnergyLevel().getLine().getX1() - d1 ) {
            bandparticle.setX( bandparticle.getEnergyLevel().getLine().getX2() + d1 );
        }
        double d2 = speed.getSpeed() * d;
        Vector2D.Double phetvector = target.getPosition();
        Vector2D.Double phetvector1 = bandparticle.getPosition();
        AbstractVector2D phetvector2 = phetvector.getSubtractedInstance( phetvector1 );
        double d3 = phetvector2.getMagnitude();
        if ( d3 <= d2 ) {
            bandparticle.setPosition( phetvector );
            return new Waiting();
        }
        else {
            AbstractVector2D phetvector3 = phetvector2.getInstanceOfMagnitude( d2 );
            phetvector3 = new Vector2D.Double( -Math.abs( phetvector3.getX() ), phetvector3.getY() );
            AbstractVector2D phetvector4 = phetvector1.getAddedInstance( phetvector3 );
            bandparticle.setPosition( new Vector2D.Double( phetvector4.getX(), phetvector4.getY() ) );
            return this;
        }
    }

    private EnergyCell target;
    Speed speed;
}
