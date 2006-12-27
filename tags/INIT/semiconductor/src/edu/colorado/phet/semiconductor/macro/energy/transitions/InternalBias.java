/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;

/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 12:08:10 PM
 * Copyright (c) Mar 17, 2004 by Sam Reid
 */
public class InternalBias extends StateTransition {
    public BandParticleState getState( BandParticle particle, EnergySection section ) {
        return null;
//        if (section.getVoltage()!=0){
//            return null;
//        }
//        EnergyCell cell=particle.getEnergyCell();
//        BandSet bandSet=particle.getBandSet();
//        Band band=particle.getBand();
//        if (band.getIndex()==2 && particle.getEnergyLevel().getDistanceFromBottomLevelInBand()==1){
//            //look for an adjacent band.
//            int bandIndex=section.indexOf(bandSet);
//            EnergyCell target=section.getLeftNeighbor(cell);
//            target=section.getLeftNeighbor(target);
//            target=section.getLowerNeighbor(target);
//        }
    }
}
