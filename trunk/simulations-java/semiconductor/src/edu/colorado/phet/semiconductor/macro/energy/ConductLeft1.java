/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 10:11:18 AM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public class ConductLeft1 extends VoltageSplit {

    public ConductLeft1( EnergySection energySection, int band, int bandSet, DopantType type ) {
        super( energySection, new ClearLeft( energySection, band, bandSet, type.getNumFilledLevels() - 1 ),
               new SimpleConductionLeft( energySection, band, bandSet, type.getNumFilledLevels() - 1 ) );
    }

}
