// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 9:44:55 AM
 */
public class SimpleConductionRight1 extends DefaultStateDiagram {
    public SimpleConductionRight1( EnergySection energySection, int band, int bandSet, int srcLevel ) {
        super( energySection );
        ExciteForConduction prightexcite = excite( band, bandSet, srcLevel );
        move( prightexcite.getLeftCell(), prightexcite.getRightCell(), getEnergySection().getSpeed() );
        exitRight( prightexcite.getRightCell() );
        enter( prightexcite.getLeftCell() );
    }

}
