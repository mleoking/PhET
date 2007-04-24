
package edu.colorado.phet.semiconductor_semi.macro;

import edu.colorado.phet.common_semiconductor.math.PhetVector;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor_semi.macro.energy.states.MoveToPosition;

import java.util.Random;

/**
 * User: Sam Reid
 * Date: Apr 27, 2004
 * Time: 10:40:11 PM
 * Copyright (c) Apr 27, 2004 by Sam Reid
 */
public class GoToMagnet implements BandParticleState {
    Magnet magnet;
    static final Random random = new Random( 0 );
    private double dx;
    private double dy;
    EnergyCell from;

    public GoToMagnet( Magnet magnet,EnergyCell from ) {
        this.magnet = magnet;
        this.from = from;
        double delta = magnet.getBounds().getWidth() * .3;
        dx = random.nextDouble() * delta - delta / 2;
        dy = random.nextDouble() * delta - delta / 2;
    }

    public boolean stepInTime( BandParticle particle, double dt ) {
        PhetVector p = magnet.getPlusSide();

        PhetVector dest = p.getAddedInstance( dx, dy );

        MoveToPosition mtp = new MoveToPosition( dest, .2 );
        mtp.stepInTime( particle, dt );
        return false;
    }

    public EnergyCell getFrom() {
        return from;
    }

}
