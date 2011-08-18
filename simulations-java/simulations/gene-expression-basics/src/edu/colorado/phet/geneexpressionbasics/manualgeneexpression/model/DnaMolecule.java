// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.common.model.ShapeChangingModelElement;

/**
 * This class models a molecule of DNA in the model.  It includes the shape of
 * the two strands of the DNA and the base pairs, defines where the various
 * genes reside, and retains other information about the DNA molecule.  This is
 * an important and central object in the model for this simulation.
 *
 * @author John Blanco
 */
public class DnaMolecule {

    private static final double STRAND_WIDTH = 200; // In picometers.
    private static final double LENGTH_PER_TWIST = 340; // In picometers.
    private static final double BASE_PAIRS_PER_TWIST = 10; // In picometers.
    private static final double DISTANCE_BETWEEN_BASE_PAIRS = LENGTH_PER_TWIST / BASE_PAIRS_PER_TWIST;
    private static final double INTER_STRAND_OFFSET = LENGTH_PER_TWIST * 0.3;
    private static final int NUMBER_OF_TWISTS = 150;
    private static final double DISTANCE_BETWEEN_GENES = 15000; // In picometers.
    private static final double LEFT_EDGE_X_POS = -DISTANCE_BETWEEN_GENES;

    private DnaStrand strand1;
    private DnaStrand strand2;
    private ArrayList<BasePair> basePairs = new ArrayList<BasePair>();
    private ArrayList<Gene> genes = new ArrayList<Gene>();
    private ArrayList<PlacementHint> placementHints = new ArrayList<PlacementHint>();

    /**
     * Constructor.
     */
    public DnaMolecule() {
        // Create the two strands that comprise the DNA "backbone".
        strand1 = generateDnaStrand( LEFT_EDGE_X_POS, LENGTH_PER_TWIST * NUMBER_OF_TWISTS, true );
        strand2 = generateDnaStrand( LEFT_EDGE_X_POS + INTER_STRAND_OFFSET, LENGTH_PER_TWIST * NUMBER_OF_TWISTS, false );

        // Add in the base pairs between the strands.  This calculates the
        // distance between the two strands and puts a line between them in
        // order to look like the base pair.  This counts on the strands being
        // close to sine waves.
        double basePairXPos = LEFT_EDGE_X_POS + INTER_STRAND_OFFSET;
        while ( basePairXPos < strand2.get( strand2.size() - 1 ).getShape().getBounds2D().getMaxX() ) {
            double height = Math.abs( ( Math.sin( ( basePairXPos - LEFT_EDGE_X_POS - INTER_STRAND_OFFSET ) / LENGTH_PER_TWIST * 2 * Math.PI ) -
                                        Math.sin( ( basePairXPos - LEFT_EDGE_X_POS ) / LENGTH_PER_TWIST * 2 * Math.PI ) ) ) * STRAND_WIDTH / 2;
            double basePairYPos = ( Math.sin( ( basePairXPos - LEFT_EDGE_X_POS - INTER_STRAND_OFFSET ) / LENGTH_PER_TWIST * 2 * Math.PI ) +
                                    Math.sin( ( basePairXPos - LEFT_EDGE_X_POS ) / LENGTH_PER_TWIST * 2 * Math.PI ) ) / 2 * STRAND_WIDTH / 2;
            basePairs.add( new BasePair( new Point2D.Double( basePairXPos, basePairYPos ), height ) );
            basePairXPos += DISTANCE_BETWEEN_BASE_PAIRS;
        }

        // Add the genes.  The first gene is set up to be centered at (0,0)in
        // model space to having to scroll the gene at startup.
        double geneStartX = DISTANCE_BETWEEN_GENES - 2000;
        genes.add( new Gene( this,
                             new DoubleRange( geneStartX, geneStartX + 1000 ),
                             new Color( 30, 144, 255 ),
                             new DoubleRange( geneStartX + 1000, geneStartX + 4000 ),
                             new Color( 255, 165, 79, 150 ) ) );
        geneStartX += DISTANCE_BETWEEN_GENES;
        genes.add( new Gene( this,
                             new DoubleRange( geneStartX, geneStartX + 2000 ),
                             new Color( 30, 144, 255 ),
                             new DoubleRange( geneStartX + 2000, geneStartX + 6000 ),
                             new Color( 240, 246, 143, 150 ) ) );
        geneStartX += DISTANCE_BETWEEN_GENES;
        genes.add( new Gene( this,
                             new DoubleRange( geneStartX, geneStartX + 2000 ),
                             new Color( 30, 144, 255 ),
                             new DoubleRange( geneStartX + 2000, geneStartX + 8000 ),
                             new Color( 205, 255, 112, 150 ) ) );

        // Add the placement hints.  TODO: Decide if these should be set up to be associated with particular genes.
        Point2D origin = new Point2D.Double( strand1.get( 0 ).getShape().getBounds2D().getMinX(), strand1.get( 0 ).getShape().getBounds2D().getCenterY() );
        placementHints.add( new PlacementHint( new RnaPolymerase( new Point2D.Double( origin.getX() + DISTANCE_BETWEEN_GENES - 1500, origin.getY() ) ) ) );
        placementHints.add( new PlacementHint( TranscriptionFactor.generateTranscriptionFactor( 0, true, new Point2D.Double( origin.getX() + DISTANCE_BETWEEN_GENES - 1500, origin.getY() ) ) ) );
    }

    // Generate a single DNA strand, i.e. one side of the double helix.
    private DnaStrand generateDnaStrand( double initialOffset, double length, boolean initialInFront ) {
        double offset = initialOffset;
        boolean inFront = initialInFront;
        boolean curveUp = true;
        DnaStrand dnaStrand = new DnaStrand();
        while ( offset + LENGTH_PER_TWIST < length ) {
            // Create the next segment.
            DoubleGeneralPath segmentPath = new DoubleGeneralPath();
            segmentPath.moveTo( offset, 0 );
            if ( curveUp ) {
                segmentPath.quadTo( offset + LENGTH_PER_TWIST / 4, STRAND_WIDTH / 2 * 2.0,
                                    offset + LENGTH_PER_TWIST / 2, 0 );
            }
            else {
                segmentPath.quadTo( offset + LENGTH_PER_TWIST / 4, -STRAND_WIDTH / 2 * 2.0,
                                    offset + LENGTH_PER_TWIST / 2, 0 );
            }

            // Add the strand segment to the end of the strand.
            dnaStrand.add( new DnaStrandSegment( segmentPath.getGeneralPath(), inFront ) );
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

    public ArrayList<Gene> getGenes() {
        return genes;
    }

    public ArrayList<PlacementHint> getPlacementHints() {
        return placementHints;
    }

    public Gene getLastGene() {
        return genes.get( genes.size() - 1 );
    }

    public ArrayList<BasePair> getBasePairs() {
        return basePairs;
    }

    public void activateHints( MobileBiomolecule biomolecule ) {
        for ( PlacementHint placementHint : placementHints ) {
            if ( placementHint.isMatchingBiomolecule( biomolecule ) ) {
                placementHint.active.set( true );
            }
        }
    }

    public void deactivateAllHints() {
        for ( PlacementHint placementHint : placementHints ) {
            placementHint.active.set( false );
        }
    }

    /**
     * Get the position in model space of the leftmost edge of the DNA strand.
     * The Y position is in the vertical center of the strand.
     */
    public Point2D getLeftEdgePos() {
        // Note: Y position of zero is a built-in assumption.  This will need
        // to change if the DNA strand needs to be somewhere else in the Y
        // direction.
        return new Point2D.Double( LEFT_EDGE_X_POS, 0 );
    }

    public double getWidth() {
        return STRAND_WIDTH;
    }

    /**
     * This class defines a segment of the DNA strand.  It is needed because the
     * DNA molecule needs to look like it is 3D, but we are only modeling it as
     * 2D, so in order to create the appearance of a twist between the two
     * strands, we need to track which segments are in front and which are in
     * back.
     */
    public class DnaStrandSegment extends ShapeChangingModelElement {
        public final boolean inFront;

        public DnaStrandSegment( Shape shape, boolean inFront ) {
            super( shape );
            this.inFront = inFront;
        }
    }

    public class DnaStrand extends ArrayList<DnaStrandSegment> {
    }
}
