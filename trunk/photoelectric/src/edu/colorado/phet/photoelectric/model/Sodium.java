/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.dischargelamps.model.*;

/**
 * Zinc
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Sodium extends ElementProperties {
    private static final String NAME = "Sodium";
    private static final double[] ENERGY_LEVELS = new double[]{-13.6};
    private static final EnergyEmissionStrategy ENERGY_EMISSION_STRATEGY = new DefaultEnergyEmissionStrategy();
    private static final EnergyAbsorptionStrategy ENERGY_ABSORPTION_STRATEGY = new DefaultAbsorptionStrategy();
    private static final double WORK_FUNCTION = 2.3;

    public Sodium() {
        super( NAME, ENERGY_LEVELS, ENERGY_EMISSION_STRATEGY, ENERGY_ABSORPTION_STRATEGY );
        setWorkFunction( WORK_FUNCTION );
    }
}
