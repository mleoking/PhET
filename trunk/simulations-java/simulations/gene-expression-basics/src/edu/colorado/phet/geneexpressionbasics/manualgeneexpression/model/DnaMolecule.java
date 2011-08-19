// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.IAttachmentSiteOwner;
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
public class DnaMolecule implements IAttachmentSiteOwner {

    private static final double STRAND_WIDTH = 200; // In picometers.
    private static final double LENGTH_PER_TWIST = 340; // In picometers.
    private static final double BASE_PAIRS_PER_TWIST = 10; // In picometers.
    private static final double DISTANCE_BETWEEN_BASE_PAIRS = LENGTH_PER_TWIST / BASE_PAIRS_PER_TWIST;
    private static final double INTER_STRAND_OFFSET = LENGTH_PER_TWIST * 0.3;
    private static final int NUMBER_OF_TWISTS = 150;
    private static final double MOLECULE_LENGTH = NUMBER_OF_TWISTS * LENGTH_PER_TWIST;
    private static final double DISTANCE_BETWEEN_GENES = 15000; // In picometers.
    private static final double LEFT_EDGE_X_POS = -DISTANCE_BETWEEN_GENES;
    private static final double Y_POS = 0;

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
                             new Color( 216, 191, 216 ),
                             new DoubleRange( geneStartX + 1000, geneStartX + 4000 ),
                             new Color( 255, 165, 79, 150 ) ) );
        geneStartX += DISTANCE_BETWEEN_GENES;
        genes.add( new Gene( this,
                             new DoubleRange( geneStartX, geneStartX + 2000 ),
                             new Color( 216, 191, 216 ),
                             new DoubleRange( geneStartX + 2000, geneStartX + 6000 ),
                             new Color( 240, 246, 143, 150 ) ) );
        geneStartX += DISTANCE_BETWEEN_GENES;
        genes.add( new Gene( this,
                             new DoubleRange( geneStartX, geneStartX + 2000 ),
                             new Color( 216, 191, 216 ),
                             new DoubleRange( geneStartX + 2000, geneStartX + 8000 ),
                             new Color( 205, 255, 112, 150 ) ) );

        // Add the placement hints.  TODO: Decide if these should be set up to be associated with particular genes.
        Point2D origin = new Point2D.Double( strand1.get( 0 ).getShape().getBounds2D().getMinX(), strand1.get( 0 ).getShape().getBounds2D().getCenterY() );
        placementHints.add( new PlacementHint( new RnaPolymerase( new Point2D.Double( origin.getX() + DISTANCE_BETWEEN_GENES - 1500, origin.getY() ) ) ) );
        placementHints.add( new PlacementHint( TranscriptionFactor.generateTranscriptionFactor( 0, true, new Point2D.Double( origin.getX() + DISTANCE_BETWEEN_GENES - 1500, origin.getY() ) ) ) );
    }

    /**
     * Get the X position of the specified base pair.  The first base pair at
     * the left side of the DNA molecule is base pair 0, and it goes up from
     * there.
     */
    private double getBasePairXOffsetByIndex( int basePairNumber ) {
        return LEFT_EDGE_X_POS + INTER_STRAND_OFFSET + (double) basePairNumber * DISTANCE_BETWEEN_BASE_PAIRS;
    }

    /**
     * Get the index of the nearest base pair given an X position in model
     * space.
     */
    private int getBasePairIndexFromXOffset( double xOffset ) {
        assert xOffset >= LEFT_EDGE_X_POS && xOffset < LEFT_EDGE_X_POS + LENGTH_PER_TWIST * NUMBER_OF_TWISTS;
        xOffset = MathUtil.clamp( LEFT_EDGE_X_POS, xOffset, LEFT_EDGE_X_POS + LENGTH_PER_TWIST * NUMBER_OF_TWISTS );
        return (int) Math.round( ( xOffset - LEFT_EDGE_X_POS - INTER_STRAND_OFFSET ) / DISTANCE_BETWEEN_BASE_PAIRS );
    }

    /**
     * Get the X location of the nearest base pair given an arbitrary x
     * location.
     */
    private double getNearestBasePairXOffset( double xPos ) {
        return getBasePairXOffsetByIndex( getBasePairIndexFromXOffset( xPos ) );
    }

    // Generate a single DNA strand, i.e. one side of the double helix.
    private DnaStrand generateDnaStrand( double initialOffset, double length, boolean initialInFront ) {
        double xOffset = initialOffset;
        boolean inFront = initialInFront;
        boolean curveUp = true;
        DnaStrand dnaStrand = new DnaStrand();
        while ( xOffset + LENGTH_PER_TWIST < length ) {
            // Create the next segment.
            DoubleGeneralPath segmentPath = new DoubleGeneralPath();
            segmentPath.moveTo( xOffset, Y_POS );
            if ( curveUp ) {
                segmentPath.quadTo( xOffset + LENGTH_PER_TWIST / 4, STRAND_WIDTH / 2 * 2.0,
                                    xOffset + LENGTH_PER_TWIST / 2, 0 );
            }
            else {
                segmentPath.quadTo( xOffset + LENGTH_PER_TWIST / 4, -STRAND_WIDTH / 2 * 2.0,
                                    xOffset + LENGTH_PER_TWIST / 2, 0 );
            }

            // Add the strand segment to the end of the strand.
            dnaStrand.add( new DnaStrandSegment( segmentPath.getGeneralPath(), inFront ) );
            curveUp = !curveUp;
            inFront = !inFront;
            xOffset += LENGTH_PER_TWIST / 2;
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
        return new Point2D.Double( LEFT_EDGE_X_POS, Y_POS );
    }

    public double getWidth() {
        return STRAND_WIDTH;
    }

    /**
     * This method commands the DNA strand to see if it has any potential
     * attachment sites for the specified biomolecule and, if so, propose them
     * to the biomolecule.  The biomolecule can accept or decline the proposal.
     *
     * @param mobileBiomolecule
     */
    public void proposeAttachmentSitesTo( MobileBiomolecule mobileBiomolecule ) {
        double maxDistanceForProposal = 500; // In picometers, empirically determined.
        if ( ( mobileBiomolecule instanceof RnaPolymerase ||
               mobileBiomolecule instanceof TranscriptionFactor ) &&
             mobileBiomolecule.getPosition().getY() - Y_POS < maxDistanceForProposal ) {
            // Propose a set of attachments to this biomolecule.
            double proposalSpan = DISTANCE_BETWEEN_BASE_PAIRS * 5; // Span is pretty arbitrary.
            double startingSearchX = Math.max( mobileBiomolecule.getPosition().getX() - proposalSpan / 2, LEFT_EDGE_X_POS );
            double endingSearchX = Math.min( startingSearchX + proposalSpan, LEFT_EDGE_X_POS + MOLECULE_LENGTH );
            List<AttachmentSite> proposedAttachmentSites = new ArrayList<AttachmentSite>();
            for ( double xOffset = startingSearchX; xOffset < endingSearchX; xOffset += DISTANCE_BETWEEN_BASE_PAIRS ) {
                // Create an attachment site.
                // TODO: Will need at some point to check against existing sites
                // also the check if the genes have any in this range.
                proposedAttachmentSites.add( new AttachmentSite( new Point2D.Double( getNearestBasePairXOffset( xOffset ), Y_POS ), 0.05 ) );
            }
            mobileBiomolecule.proposeAttachmentSites( proposedAttachmentSites );
            // TODO: Need to check the list of attachment sites and add any that
            // have been marked as being in use to a local list.
        }
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
