/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandSet;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;

/**
 * User: Sam Reid
 * Date: Mar 16, 2004
 * Time: 10:02:43 PM
 * Copyright (c) Mar 16, 2004 by Sam Reid
 */
public class FallExcited extends StateTransition {
    private int srcband;
    private int srclevel;
    private int dstband;
    private int dstlevel;

    public FallExcited( int band, int level, int dstband, int dstlevel ) {
        this.srcband = band;
        this.srclevel = level;
        this.dstband = dstband;
        this.dstlevel = dstlevel;
    }

    public BandParticleState getState( BandParticle particle, EnergySection section ) {
//        if (section.getVoltage() != 0)
//            return null;
        EnergyCell cur = particle.getEnergyCell();
        if( cur == null || !particle.isLocatedAtCell() ) {
            return null;
        }
        int atlevel = cur.getEnergyLevelBandIndex();
        int atband = cur.getBandIndex();

        if( atlevel == srclevel && atband == srcband ) {
            BandSet bs = cur.getBandSet();
            EnergyCell down = bs.bandAt( dstband ).energyLevelAt( dstlevel ).cellAt( cur.getIndex() );
            if( down.getBand() != cur.getBand() && !section.isClaimed( down ) ) {
//                particle.setExcited(false);
                return new MoveToCell( particle, down, section.getFallSpeed() );
            }
        }
        return null;
    }

}
