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

    // Energy levels
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

    // Likelihoods of emission transitions from one
    // state to another
    static TransitionEntry[] teA = new TransitionEntry[]{
            new TransitionEntry( 2, 0, 0.08 ),
            new TransitionEntry( 8, 2, 0.50 ),
            new TransitionEntry( 7, 1, 0.45 ),
            new TransitionEntry( 8, 3, 0.62 ),
            new TransitionEntry( 7, 2, 0.66 ),
            new TransitionEntry( 7, 3, 1.48 ),
            new TransitionEntry( 5, 1, 0.21 ),
            new TransitionEntry( 6, 2, 0.04 ),
            new TransitionEntry( 8, 4, 0.11 ),
            new TransitionEntry( 5, 2, 0.56 ),
            new TransitionEntry( 5, 3, 0.49 ),
            new TransitionEntry( 7, 4, 0.24 ),
            new TransitionEntry( 8, 5, 0.03 ),
            new TransitionEntry( 6, 4, 0.27 ),
            new TransitionEntry( 3, 3, 1 ),
            new TransitionEntry( 4, 4, 1 ),
            new TransitionEntry( 1, 1, 1 ),
            new TransitionEntry( 0, 0, 1 ),
    };

    /**
     *
     */
    public MercuryProperties() {
        super( SimStrings.get( "Element.mercury" ), energyLevels, teA );
    }
}
