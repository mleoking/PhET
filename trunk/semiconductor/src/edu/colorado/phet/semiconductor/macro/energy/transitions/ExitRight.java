/*Copyright, Sam Reid, 2003.*/
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
 * Date: Mar 17, 2004
 * Time: 8:27:41 AM
 * Copyright (c) Mar 17, 2004 by Sam Reid
 */
public class ExitRight extends StateTransition {

    public BandParticleState getState( BandParticle particle, EnergySection energySection ) {
        if( energySection.getVoltage() <= 0 ) {
            return null;
        }
        EnergyCell cell = particle.getEnergyCell();
        if( cell == null ) {
            return null;
        }
        EnergyCell right = energySection.getNeighbor( cell, 0, 1 );
//        if (cell.getIndex() == 0) {//move left.
//            StateChain sc = new StateChain();
//            double targetX = energySection.bandSetAt(0).getX();
//            double targetY = particle.getEnergy();
//            PhetVector dest = new PhetVector(targetX, targetY);
//            sc.addState(new MoveToPosition(dest, energySection.getSpeed()));
//            sc.addState(new Remove(energySection));
//            return sc;
//        }
        if( cell != null && right == null && energySection.getVoltage() > 0 && cell.getIndex() == 1 && particle.isLocatedAtCell() && particle.isExcited() ) {

//            StateChain sc = new StateChain();
            double targetX = energySection.getRightBand().getX() + energySection.getRightBand().getWidth();
            double targetY = particle.getY();
            PhetVector dest = new PhetVector( targetX, targetY );
            ExitRightState ers = new ExitRightState( dest, energySection.getSpeed(), energySection );
//            sc.addState(new MoveToPosition(dest, energySection.getSpeed()));
//            sc.addState(new Remove(energySection));
            particle.setState( ers );
//            particle.setState(sc);
        }
        return null;
    }
}
