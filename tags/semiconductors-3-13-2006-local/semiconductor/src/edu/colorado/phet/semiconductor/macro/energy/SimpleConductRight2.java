/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 9:24:11 PM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public class SimpleConductRight2 extends DefaultStateDiagram {
    private ExciteForConduction leftExcite;
    private ExciteForConduction rightExcite;

    public SimpleConductRight2( EnergySection energySection, DopantType dopantType ) {
        super( energySection );
        leftExcite = excite( dopantType.getDopingBand(), 0, dopantType.getNumFilledLevels() - 1 );
        rightExcite = excite( dopantType.getDopingBand(), 1, dopantType.getNumFilledLevels() - 1 );
        enter( leftExcite.getLeftCell() );
        exitRight( rightExcite.getRightCell() );
        propagateRight( leftExcite.getLeftCell() );
    }

    public ExciteForConduction getLeftExcite() {
        return leftExcite;
    }

    public ExciteForConduction getRightExcite() {
        return rightExcite;
    }
}
