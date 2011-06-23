// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    // Model objects.  TODO: Improve this comment if this is kept.
    private static final List<ModelObject> modelObjects = new ArrayList<ModelObject>();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public ManualGeneExpressionModel() {
        modelObjects.add( new TestSquare( 0, 0, 20, Color.BLUE ) );
        modelObjects.add( new TestSquare( 200, 0, 20, Color.RED ) );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    public List<ModelObject> getModelObjects() {
        return modelObjects;
    }

    public DnaStrand getDnaStrand() {
        return dnaStrand;
    }
}
