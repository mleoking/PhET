package edu.colorado.phet.semiconductor_semi.macro.energy;

import edu.colorado.phet.semiconductor_semi.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor_semi.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 10:13:54 AM
 */
public class ClearLeft extends DefaultStateDiagram {
    public ClearLeft( EnergySection energySection, int band, int bandSet, int srcLevel ) {
        super( energySection );

        ExciteForConduction excite = createExcite( band, bandSet, srcLevel );
        EnergyCell left = energySection.getLowerNeighbor( excite.getLeftCell() );
        EnergyCell right = energySection.getRightNeighbor( left );
        move( right, left, getEnergySection().getSpeed() );
        enter( right );
        fall( excite.getLeftCell() );
        fall( excite.getRightCell() );

        move( excite.getRightCell(), excite.getLeftCell(), getSpeed() );
        exitLeft( excite.getLeftCell() );
        unexcite( left );
        unexcite( right );
    }

}
