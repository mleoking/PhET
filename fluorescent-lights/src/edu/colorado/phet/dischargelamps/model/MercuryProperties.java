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
public class MercuryProperties extends DischargeLampElementProperties {
    private static double[] energyLevels = {
            -10.38,
            -5.73,
            -4.94,
            -3.70,
            -2.67,
            -2.47,
            -1.55,
            -0.85
    };

    public MercuryProperties() {
        super( SimStrings.get( "Element.mercury" ),
               MercuryProperties.energyLevels,
               new HydrogenEnergyEmissionStrategy(),
               new FiftyPercentAbsorptionStrategy(),
               DischargeLampAtom.DEFAULT_STATE_LIFETIME );
    }
}
