// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity.macro.bands.states;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
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
        MutableVector2D phetvector = target.getPosition();
        MutableVector2D phetvector1 = bandparticle.getPosition();
        Vector2D phetvector2 = phetvector.getSubtractedInstance( phetvector1 );
        double d3 = phetvector2.getMagnitude();
        if ( d3 <= d2 ) {
            bandparticle.setPosition( phetvector );
            return new Waiting();
        }
        else {
            Vector2D phetvector3 = phetvector2.getInstanceOfMagnitude( d2 );
            phetvector3 = new Vector2D( -Math.abs( phetvector3.getX() ), phetvector3.getY() );
            Vector2D phetvector4 = phetvector1.plus( phetvector3 );
            bandparticle.setPosition( new MutableVector2D( phetvector4.getX(), phetvector4.getY() ) );
            return this;
        }
    }

    private EnergyCell target;
    Speed speed;
}
