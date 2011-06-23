// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

/**
 * This class models a molecule of DNA in the model.  It includes the shape of
 * the two strands of the DNA and the base pairs, defines where the various
 * genes reside, and retains other information about the DNA molecule.  This
 * is an important and central object in the model for this simulation.
 *
 * @author John Blanco
 */
public class DnaMolecule {

    private static final double STRAND_WIDTH = 200; // In picometers.
    private static final double LENGTH_PER_TWIST = 340; // In picometers.
    private static final double BASE_PAIRS_PER_TWIST = 10; // In picometers.

    private DnaStrand strand1;
    private DnaStrand strand2;

    /**
     * Constructor.
     */
    public DnaMolecule() {
        strand1 = generateDnaStrand( 0, LENGTH_PER_TWIST * 100, true );
        strand2 = generateDnaStrand( LENGTH_PER_TWIST * 0.2, LENGTH_PER_TWIST * 100, false );
    }

    private DnaStrand generateDnaStrand( double initialOffset, double length, boolean initialInFront ) {
        double offset = initialOffset;
        boolean inFront = initialInFront;
        boolean curveUp = true;
        DnaStrand dnaStrand = new DnaStrand();
        while ( offset + LENGTH_PER_TWIST < length ) {
            GeneralPath segmentShape = new GeneralPath();
            segmentShape.moveTo( offset, 0 );
            if ( curveUp ) {
                segmentShape.lineTo( offset + LENGTH_PER_TWIST / 4, STRAND_WIDTH / 2 );
            }
            else {
                segmentShape.lineTo( offset + LENGTH_PER_TWIST / 4, -STRAND_WIDTH / 2 );
            }
            segmentShape.lineTo( offset + LENGTH_PER_TWIST / 2, 0 );
            dnaStrand.add( new DnaStrandSegment( segmentShape, inFront ) );
            curveUp = !curveUp;
            inFront = !inFront;
            offset += LENGTH_PER_TWIST / 2;
        }
        return dnaStrand;
    }

    public DnaStrand getStrand1() {
        return strand1;
    }

    public DnaStrand getStrand2() {
        return strand2;
    }

    /**
     * This class defines a segment of the DNA strand.  It is needed because
     * the DNA molecule needs to look like it is 3D, but we are only modeling
     * it as 2D, so in order to create the appearance of a twist between the
     * two strands, we need to track which segments are in front and which are
     * in back.
     */
    public class DnaStrandSegment extends ShapeChangingModelObject {
        public final boolean inFront;

        public DnaStrandSegment( Shape shape, boolean inFront ) {
            super( shape );
            this.inFront = inFront;
        }
    }

    public class DnaStrand extends ArrayList<DnaStrandSegment> {
    }
}
