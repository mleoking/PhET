// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Primary model for the manual gene expression tab.
 *
 * @author John Blanco
 */
public class ManualGeneExpressionModel {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }
}
