/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;

/**
 * User: Sam Reid
 * Date: Mar 16, 2004
 * Time: 10:02:43 PM
 * Copyright (c) Mar 16, 2004 by Sam Reid
 */
public class VoltageExcite extends StateTransition {
    private int band;
    private int level;

    public VoltageExcite( int band, int level ) {
        this.band = band;
        this.level = level;
    }

    public BandParticleState getState( BandParticle particle, EnergySection section ) {
        if( section.getVoltage() == 0 ) {
            return null;
        }
        EnergyCell cur = particle.getEnergyCell();
        if( cur == null || !particle.isLocatedAtCell() ) {
            return null;
        }
        int atlevel = cur.getEnergyLevelBandIndex();
        int atband = cur.getBandIndex();

        if( atlevel == level && atband == band ) {
            EnergyCell up = section.getUpperNeighbor( cur );
            if( up.getBand() == cur.getBand() && !section.isClaimed( up ) ) {
                particle.setExcited( true );
                return new MoveToCell( particle, up, section.getSpeed() );
            }
        }
        return null;
    }

}
