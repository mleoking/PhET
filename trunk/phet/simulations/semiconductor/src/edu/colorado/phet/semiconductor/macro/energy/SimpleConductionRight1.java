/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 9:44:55 AM
 * Copyright (c) Apr 26, 2004 by Sam Reid
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
