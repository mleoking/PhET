/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 5:12:04 PM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public class VoltageSplit implements ModelElement {
    EnergySection energySection;
    private ModelElement zero;
    private ModelElement nonZero;

    public VoltageSplit( EnergySection energySection, ModelElement zero, ModelElement nonZero ) {
        this.energySection = energySection;
        this.zero = zero;
        this.nonZero = nonZero;
    }

    public void stepInTime( double dt ) {
        if( energySection.getVoltage() == 0 ) {
            zero.stepInTime( dt );
        }
        else {
            nonZero.stepInTime( dt );
        }
    }
}
