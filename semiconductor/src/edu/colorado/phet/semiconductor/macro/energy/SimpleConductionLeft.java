/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 9:44:55 AM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public class SimpleConductionLeft extends DefaultStateDiagram {
    public SimpleConductionLeft( EnergySection energySection, int band, int bandSet, int srcLevel ) {
        super( energySection );
        ExciteForConduction excite = excite( band, bandSet, srcLevel );
        move( excite.getRightCell(), excite.getLeftCell(), getEnergySection().getSpeed() );
        exitLeft( excite.getLeftCell() );
        enter( excite.getRightCell() );
    }
}
