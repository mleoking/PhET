/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;

/**
 * User: Sam Reid
 * Date: Mar 28, 2004
 * Time: 12:31:42 AM
 * Copyright (c) Mar 28, 2004 by Sam Reid
 */
public class Fall extends StateTransition {
    EnergyCell fallFrom;

    public Fall( EnergyCell fallFrom ) {
        this.fallFrom = fallFrom;
    }

    public BandParticleState getState( BandParticle particle, EnergySection section ) {
        EnergyCell cell = particle.getEnergyCell();

        if( cell != null && cell == fallFrom && particle.isLocatedAtCell() ) {
            EnergyCell target = getTarget( cell, section );
            return new MoveToCell( particle, target, section.getFallSpeed() );
        }
        return null;
    }

    private EnergyCell getTarget( EnergyCell cell, EnergySection section ) {
        EnergyCell lower = section.getLowerNeighbor( cell );
        boolean owned = section.isClaimed( lower );
        if( owned ) {
            return cell;
        }
        return getTarget( lower, section );
    }
}
