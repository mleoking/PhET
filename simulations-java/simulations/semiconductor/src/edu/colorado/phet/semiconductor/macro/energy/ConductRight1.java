// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.doping.DopantType;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 10:11:18 AM
 */
public class ConductRight1 extends VoltageSplit {
    public ConductRight1( EnergySection energySection, int band, int bandSet, DopantType type ) {
        super( energySection, new ClearRight1( energySection, band, bandSet, type.getNumFilledLevels() - 1 ),
               new SimpleConductionRight1( energySection, band, bandSet, type.getNumFilledLevels() - 1 ) );
    }
}
