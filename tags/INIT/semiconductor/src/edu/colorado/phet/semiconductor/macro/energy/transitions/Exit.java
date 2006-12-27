/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.ExitRightState;

/**
 * User: Sam Reid
 * Date: Mar 27, 2004
 * Time: 11:02:09 AM
 * Copyright (c) Mar 27, 2004 by Sam Reid
 */
public class Exit extends StateTransition {
    EnergyCell exitCell;
    private PhetVector exitTo;

    public Exit( EnergyCell exitCell, PhetVector exitTo ) {
        this.exitCell = exitCell;
        this.exitTo = exitTo;
    }

    public BandParticleState getState( BandParticle particle, EnergySection section ) {
        if( particle.getEnergyCell() == exitCell && particle.isLocatedAtCell() ) {
            particle.setEnergyCell( null );
            return new ExitRightState( exitTo, section.getSpeed(), section );
        }
        return null;
    }
}
