// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.semiconductor.macro;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToPosition;


/**
 * User: Sam Reid
 * Date: Apr 27, 2004
 * Time: 10:40:11 PM
 */
public class GoToMagnet implements BandParticleState {
    Magnet magnet;
    static final Random random = new Random( 0 );
    private double dx;
    private double dy;
    EnergyCell from;

    public GoToMagnet( Magnet magnet, EnergyCell from ) {
        this.magnet = magnet;
        this.from = from;
        double delta = magnet.getBounds().getWidth() * .3;
        dx = random.nextDouble() * delta - delta / 2;
        dy = random.nextDouble() * delta - delta / 2;
    }

    public boolean stepInTime( BandParticle particle, double dt ) {
        MutableVector2D p = magnet.getPlusSide();

        Vector2D dest = p.plus( dx, dy );

        MoveToPosition mtp = new MoveToPosition( dest, .2 );
        mtp.stepInTime( particle, dt );
        return false;
    }

    public EnergyCell getFrom() {
        return from;
    }

}
