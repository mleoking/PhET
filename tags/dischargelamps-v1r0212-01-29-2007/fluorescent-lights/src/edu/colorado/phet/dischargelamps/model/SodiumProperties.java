/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.common.view.util.SimStrings;

/**
 * HydrogenProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SodiumProperties extends DischargeLampElementProperties {
    private static double[] energyLevels = {
            -5.14,
            -3.03,
            -1.95,
            -1.52,
            -1.39,
            -1.02,
            -0.86
    };

    public SodiumProperties() {
        super( SimStrings.get( "Element.sodium" ),
               SodiumProperties.energyLevels,
               new HydrogenEnergyEmissionStrategy(),
               new EqualLikelihoodAbsorptionStrategy(),
               DischargeLampAtom.DEFAULT_STATE_LIFETIME );
    }
}
