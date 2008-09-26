/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
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
 */
public class ExitRight extends StateTransition {

    public BandParticleState getState( BandParticle particle, EnergySection energySection ) {
        EnergyCell cell = particle.getEnergyCell();
        if ( cell == null ) {
            return null;
        }
        EnergyCell right = energySection.getNeighbor( cell, 0, 1 );
        if ( cell != null && right == null && cell.getIndex() == 1 && particle.isLocatedAtCell() && particle.isExcited() ) {
            double targetX = energySection.getRightBand().getX() + energySection.getRightBand().getWidth();
            double targetY = particle.getY();
            Vector2D.Double dest = new Vector2D.Double( targetX, targetY );
            ExitRightState ers = new ExitRightState( dest, energySection.getSpeed(), energySection );
            particle.setState( ers );
        }
        return null;
    }
}
