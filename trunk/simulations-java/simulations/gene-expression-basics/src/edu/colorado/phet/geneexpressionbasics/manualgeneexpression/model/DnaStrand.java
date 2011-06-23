// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * This class models a strand of DNA in the model.  It includes the overall
 * shape of the DNA and defines where the various genes reside.  This is an
 * important and central object in the model for this simulation.
 *
 * @author John Blanco
 */
public class DnaStrand extends ModelObject {

    private static final double STRAND_WIDTH = 200; // In picometers.
    private static final double LENGTH_PER_TWIST = 340; // In picometers.
    private static final double BASE_PAIRS_PER_TWIST = 10; // In picometers.

    /**
     * Constructor.
     */
    public DnaStrand() {
        super( generateShape() );
    }

    /**
     * Generate the shape of the DNA strand.  This is static so that it can be
     * used during construction.
     */
    private static Shape generateShape() {
        return new Rectangle2D.Double( 0, -STRAND_WIDTH / 2, LENGTH_PER_TWIST * 1000, STRAND_WIDTH );
    }
}
