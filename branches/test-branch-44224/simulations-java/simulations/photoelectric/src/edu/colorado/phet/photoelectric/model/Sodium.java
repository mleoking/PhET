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

import edu.colorado.phet.dischargelamps.model.DischargeLampElementProperties;
import edu.colorado.phet.dischargelamps.model.EnergyAbsorptionStrategy;
import edu.colorado.phet.dischargelamps.model.LevelSpecificEnergyEmissionStrategy;
import edu.colorado.phet.photoelectric.PhotoelectricResources;

/**
 * Sodium
 * <p/>
 * Note that this differs from the SodiumProperties used in DischargeLamps because it has a metal energy
 * absorption strategy
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Sodium extends DischargeLampElementProperties {
    private static final String NAME = PhotoelectricResources.getString( "Element.Sodium" );
    private static final double[] ENERGY_LEVELS = new double[]{
            -5.14
            - 3.03
            - 1.95
            - 1.52
            - 1.39
            - 1.02
            - 0.86
    };

    // Likelihoods of emission transitions from one
    // state to another
    static TransitionEntry[] teA = new TransitionEntry[]{
            new TransitionEntry( 4, 0, 0.05 ),
            new TransitionEntry( 5, 1, 0.24 ),
            new TransitionEntry( 1, 0, 1.23 ),
            new TransitionEntry( 5, 1, 0.07 ),
            new TransitionEntry( 3, 1, 1.03 ),
            new TransitionEntry( 2, 1, 0.26 ),
            new TransitionEntry( 5, 3, 0.15 ),
            new TransitionEntry( 4, 2, 0.13 ),
            new TransitionEntry( 5, 4, 0.13 ),
            new TransitionEntry( 0, 0, 1 ),
    };

    private static final double WORK_FUNCTION = 2.3;
    private static final EnergyAbsorptionStrategy ENERGY_ABSORPTION_STRATEGY = new MetalEnergyAbsorptionStrategy( WORK_FUNCTION );

    /**
     *
     */
    public Sodium() {
        super( NAME,
               ENERGY_LEVELS,
               new LevelSpecificEnergyEmissionStrategy( teA ),
               ENERGY_ABSORPTION_STRATEGY );

        ( (LevelSpecificEnergyEmissionStrategy)getEnergyEmissionStrategy() ).setStates( getStates() );
        setWorkFunction( WORK_FUNCTION );
    }
}
