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
public class HydrogenProperties extends DischargeLampElementProperties {
    private static double[] energyLevels = {
        -13.6,
        -3.4,
        -1.511,
        -0.850,
        -0.544,
        -0.378};


    static TransitionEntry[] teA = new TransitionEntry[]{
            new TransitionEntry( 5, 1, 1 ),
            new TransitionEntry( 5, 2, 1 ),
            new TransitionEntry( 5, 0, 1 ),
//            new TransitionEntry( 5, 0, 0.39 ),
//            new TransitionEntry( 5, 1, 0.06 ),
//            new TransitionEntry( 5, 2, 0.02 ),
            new TransitionEntry( 4, 0, 0.69 ),
            new TransitionEntry( 4, 1, 0.11 ),
            new TransitionEntry( 4, 3, 0.04 ),
            new TransitionEntry( 3, 0, 1.36 ),
            new TransitionEntry( 3, 1, 0.24 ),
            new TransitionEntry( 3, 2, 0.07 ),
            new TransitionEntry( 2, 0, 3.34 ),
            new TransitionEntry( 2, 1, 0.87 ),
            new TransitionEntry( 1, 0, 12.53 )
    };

    static LevelSpecificEnergyEmissionStrategy energyEmissionStrategy = new LevelSpecificEnergyEmissionStrategy( teA );

    public HydrogenProperties() {
        super( SimStrings.get("Element.hydrogen"), energyLevels,
               energyEmissionStrategy,
//               new HydrogenEnergyEmissionStrategy(),
               new FiftyPercentAbsorptionStrategy(),
               DischargeLampAtom.DEFAULT_STATE_LIFETIME );
         energyEmissionStrategy.setStates( getStates() );
    }
}

