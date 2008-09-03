/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.ExitRightState;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 8:27:41 AM
 */
public class ExitRightFrom extends StateTransition {
    EnergyCell from;

    public ExitRightFrom( EnergyCell from ) {
        this.from = from;
    }

    public BandParticleState getState( BandParticle particle, EnergySection energySection ) {
        EnergyCell cell = particle.getEnergyCell();
        if( cell == null ) {
            return null;
        }
        EnergyCell right = energySection.getNeighbor( cell, 0, 1 );
        if( cell != null && right == null && cell.getIndex() == 1 && particle.isLocatedAtCell() && particle.isExcited() && cell == from ) {

            double targetX = energySection.getRightBand().getX() + energySection.getRightBand().getWidth();
            double targetY = particle.getY();
            PhetVector dest = new PhetVector( targetX, targetY );
            ExitRightState ers = new ExitRightState( dest, energySection.getSpeed(), energySection );
            particle.setState( ers );
        }
        return null;
    }
}
