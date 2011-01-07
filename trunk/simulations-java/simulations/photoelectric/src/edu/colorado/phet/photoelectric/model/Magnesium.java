// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.common.quantum.model.EnergyEmissionStrategy;
import edu.colorado.phet.dischargelamps.model.DefaultEnergyEmissionStrategy;
import edu.colorado.phet.dischargelamps.model.DischargeLampElementProperties;
import edu.colorado.phet.dischargelamps.model.EnergyAbsorptionStrategy;
import edu.colorado.phet.photoelectric.PhotoelectricResources;

/**
 * Magnesium
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Magnesium extends DischargeLampElementProperties {
    private static final String NAME = PhotoelectricResources.getString( "Element.Magnesium" );
    private static final double[] ENERGY_LEVELS = new double[]{-13.6};
    private static final EnergyEmissionStrategy ENERGY_EMISSION_STRATEGY = new DefaultEnergyEmissionStrategy();
    private static final double WORK_FUNCTION = 3.7;
    private static final EnergyAbsorptionStrategy ENERGY_ABSORPTION_STRATEGY = new MetalEnergyAbsorptionStrategy( WORK_FUNCTION );

    public Magnesium() {
        super( NAME, ENERGY_LEVELS, ENERGY_EMISSION_STRATEGY, ENERGY_ABSORPTION_STRATEGY );
        setWorkFunction( WORK_FUNCTION );
    }
}
