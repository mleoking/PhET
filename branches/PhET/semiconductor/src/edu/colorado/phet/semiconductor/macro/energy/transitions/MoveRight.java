/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.semiconductor.macro.energy.ElectricFieldSection;
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
public class MoveRight extends StateTransition {

    public BandParticleState getState( BandParticle particle, EnergySection section ) {
        if( section.getVoltage() <= 0 ) {
            return null;
        }
        EnergyCell cur = particle.getEnergyCell();
        if( cur == null || !particle.isLocatedAtCell() ) {
            return null;
        }
        EnergyCell right = section.getNeighbor( cur, 0, 1 );

        if( right == null ) {
            return null;
        }
        BandSet curBS = cur.getBandSet();
        BandSet dstBS = right.getBandSet();
        if( curBS != dstBS ) {
            ElectricFieldSection field = section.getElectricFieldSection( curBS, dstBS );
            double net = field.getNetField();
            if( net <= 0 ) {
                return null;//Objection to moving right.
            }
        }

        if( right != null && !section.isClaimed( right ) && particle.isExcited() ) {

//            BandSet dstBand = right.getBandSet();
//            BandSet srcBand = cur.getBandSet();
//            int destNetCharge = section.getExcessCharge(dstBand);
//            int srcNetCharge = section.getExcessCharge(srcBand);

//            int diff = destNetCharge - srcNetCharge;
//            if (diff >= 4) {
//                return null;
//            }

            return new MoveToCell( particle, right, section.getSpeed() );
        }
        return null;
    }

}
