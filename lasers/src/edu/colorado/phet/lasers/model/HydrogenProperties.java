/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model;

import edu.colorado.phet.dischargelamps.model.FiftyPercentAbsorptionStrategy;
import edu.colorado.phet.dischargelamps.model.HydrogenEnergyEmissionStrategy;

/**
 * HydrogenProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HydrogenProperties extends ElementProperties {
    private static double[] energyLevels = {
        -13.6,
        -3.4,
        -1.511,
        -0.850,
        -0.544,
        -0.378};

    public HydrogenProperties() {
        super( "Hydrogen", energyLevels,
               new HydrogenEnergyEmissionStrategy(),
               new FiftyPercentAbsorptionStrategy() );
    }
}

