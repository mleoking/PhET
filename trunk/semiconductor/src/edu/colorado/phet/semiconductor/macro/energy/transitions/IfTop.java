/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyLevel;

/**
 * User: Sam Reid
 * Date: Mar 28, 2004
 * Time: 9:27:43 AM
 * Copyright (c) Mar 28, 2004 by Sam Reid
 */
public class IfTop extends StateTransition {

    StateTransition st;

    public IfTop( StateTransition st ) {
        this.st = st;
    }

    public BandParticleState getState( BandParticle particle, EnergySection section ) {
        if( isTop( particle, section ) ) {
            return st.getState( particle, section );
        }
        else {
            return null;
        }


    }

    public static boolean isTop( BandParticle particle, EnergySection section ) {
        EnergyCell at = particle.getEnergyCell();
        if( at == null ) {
            return false;
        }
        EnergyCell upper = section.getUpperNeighbor( at );
        if( upper == null ) {
            return true;
        }
        EnergyLevel above = upper.getEnergyLevel();
        int occupancy = section.getNumParticlesAtCells( above );
        if( occupancy == 0 ) {
            return true;
        }
        return false;
    }


}
