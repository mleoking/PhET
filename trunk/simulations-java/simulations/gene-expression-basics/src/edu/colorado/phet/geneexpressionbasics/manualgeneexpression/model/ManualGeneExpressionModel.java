// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Primary model for the manual gene expression tab.
 * <p/>
 * Dimensions in this model (and all sub-elements of the model) are in nano-
 * meters, i.e. 10E-9 meters.
 * <p/>
 * The point (0,0) in model space is at the leftmost edge of the DNA strand,
 * and at the vertical center of the strand.
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

    // DNA strand, which is where the genes reside, where the polymerase does
    // its transcription, and where a lot of the action takes place.
    private final DnaStrand dnaStrand = new DnaStrand();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public ManualGeneExpressionModel() {
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    public DnaStrand getDnaStrand() {
        return dnaStrand;
    }
}
