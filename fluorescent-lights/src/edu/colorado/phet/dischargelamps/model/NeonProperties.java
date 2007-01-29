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

    // Energy levels
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

    // Likelihoods of emission transitions from one
    // state to another
    static TransitionEntry[] teA = new TransitionEntry[]{
            new TransitionEntry( 4, 0, 6.11 ),
            new TransitionEntry( 2, 0, 0.48 ),
            new TransitionEntry( 9, 2, 0.01 ),
            new TransitionEntry( 9, 4, 0.68 ),
            new TransitionEntry( 8, 1, 0.26 ),
            new TransitionEntry( 8, 2, 0.85 ),
            new TransitionEntry( 7, 1, 0.35 ),
            new TransitionEntry( 8, 3, 0.40 ),
            new TransitionEntry( 7, 2, 0.36 ),
            new TransitionEntry( 6, 1, 0.68 ),
            new TransitionEntry( 6, 2, 0.30 ),
            new TransitionEntry( 7, 3, 0.11 ),
            new TransitionEntry( 8, 4, 0.68 ),
            new TransitionEntry( 7, 4, 0.19 ),
            new TransitionEntry( 5, 1, 0.25 ),
            new TransitionEntry( 6, 4, 0.03 ),
            new TransitionEntry( 5, 2, 0.09 ),
            new TransitionEntry( 5, 3, 0.02 ),
            new TransitionEntry( 3, 3, 1 ),
            new TransitionEntry( 1, 1, 1 ),
            new TransitionEntry( 0, 0, 1 ),
    };

    /**
     *
     */
    public NeonProperties() {
        super( SimStrings.get( "Element.neon" ), energyLevels, teA );
    }
}

