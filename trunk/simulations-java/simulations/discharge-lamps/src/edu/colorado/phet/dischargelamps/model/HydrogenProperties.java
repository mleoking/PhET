// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.dischargelamps.DischargeLampsResources;


/**
 * HydrogenProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HydrogenProperties extends DischargeLampElementProperties {

    // Energy levels
    private static double[] energyLevels = {
            -13.6,
            -3.4,
            -1.511,
            -0.850,
            -0.544,
            -0.378};

    // Likelihoods of emission transitions from one state to another
    static TransitionEntry[] teA = new TransitionEntry[]{
            new TransitionEntry( 5, 0, 0.39 / 4 ),
            new TransitionEntry( 5, 1, 0.06 ),
            new TransitionEntry( 5, 2, 0.02 ),
            new TransitionEntry( 4, 0, 0.69 / 4 ),
            new TransitionEntry( 4, 1, 0.11 ),
            new TransitionEntry( 4, 3, 0.04 ),
            new TransitionEntry( 3, 0, 1.36 / 4 ),
            new TransitionEntry( 3, 1, 0.24 ),
            new TransitionEntry( 3, 2, 0.07 ),
            new TransitionEntry( 2, 0, 3.34 / 4 ),
            new TransitionEntry( 2, 1, 0.87 ),
            new TransitionEntry( 1, 0, 12.53 / 4 ),
            new TransitionEntry( 0, 0, 1 / 4 ),
    };

    /**
     *
     */
    public HydrogenProperties() {
        super( DischargeLampsResources.getString( "Element.hydrogen" ), energyLevels, teA );
    }
}

