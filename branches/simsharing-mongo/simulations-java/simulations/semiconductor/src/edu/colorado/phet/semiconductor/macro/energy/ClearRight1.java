// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 10:13:54 AM
 */
public class ClearRight1 extends DefaultStateDiagram {
    public ClearRight1( EnergySection energySection, int band, int bandSet, int srcLevel ) {
        super( energySection );

        ExciteForConduction prightexcite = createExcite( band, bandSet, srcLevel );
        EnergyCell left = energySection.getLowerNeighbor( prightexcite.getLeftCell() );
        EnergyCell right = energySection.getRightNeighbor( left );
        move( left, right, getEnergySection().getSpeed() );
        enter( left );
        fall( prightexcite.getLeftCell() );
        fall( prightexcite.getRightCell() );

        move( prightexcite.getLeftCell(), prightexcite.getRightCell(), getSpeed() );
        exitRight( prightexcite.getRightCell() );
        unexcite( left );
        unexcite( right );
    }

}
