/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;
import edu.colorado.phet.semiconductor.macro.energy.states.Speed;

/**
 * User: Sam Reid
 * Date: Mar 27, 2004
 * Time: 10:54:23 AM
 * Copyright (c) Mar 27, 2004 by Sam Reid
 */
public class Move extends StateTransition {
    EnergyCell src;
    EnergyCell dst;
    private Speed speed;

    public Move( EnergyCell src, EnergyCell dst, Speed speed ) {
        this.src = src;
        this.dst = dst;
        this.speed = speed;
    }

    public BandParticleState getState( BandParticle particle, EnergySection section ) {
        //logic for deciding if a particle should move.
        BandParticle occupant = section.getBandParticle( src );

        if( occupant == particle && particle.isLocatedAtCell() ) {
            if( !section.isClaimed( dst ) ) {
                return new MoveToCell( particle, dst, speed );
            }
        }
        return null;
    }
}
