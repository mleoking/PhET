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
public class NeonProperties extends DischargeLampElementProperties {
    private static double[] energyLevels = {
            -21.56,
            -4.94,
            -4.89,
            -4.84,
            -4.71,
            -3.18,
            -2.99,
            -2.94,
            -2.85,
            -2.59
    };

    public NeonProperties() {
        super( SimStrings.get( "Element.neon" ),
               energyLevels,
               new HydrogenEnergyEmissionStrategy(),
               new FiftyPercentAbsorptionStrategy(),
               DischargeLampAtom.DEFAULT_STATE_LIFETIME );
    }
}

