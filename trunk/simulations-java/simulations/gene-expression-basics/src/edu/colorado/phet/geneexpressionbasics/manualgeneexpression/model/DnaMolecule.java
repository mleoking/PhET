// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.ShapeChangingModelElement;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;

/**
 * This class models a molecule of DNA in the model.  It includes the shape of
 * the two "backbone" strands of the DNA and the individual base pairs, defines
 * where the various genes reside, and retains other information about the DNA
 * molecule.  This is an important and central object in the model for this
 * simulation.
 * <p/>
 * A big simplifying assumption that this class makes is that molecules that
 * attach to the DNA do so to individual base pairs.  In reality, biomolecules
 * attach to groups of base pairs, the exact configuration of which dictate
 * where biomolecules attach. This was unnecessarily complicated for the needs
 * of this sim.
 *
 * @author John Blanco
 */
public class DnaMolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Constants the define the geometry of the DNA molecule.
    public static final double DIAMETER = 200; // In picometers.
    private static final double LENGTH_PER_TWIST = 340; // In picometers.
    public static final int BASE_PAIRS_PER_TWIST = 10; // In picometers.
    public static final double DISTANCE_BETWEEN_BASE_PAIRS = LENGTH_PER_TWIST / BASE_PAIRS_PER_TWIST;
    private static final double INTER_STRAND_OFFSET = LENGTH_PER_TWIST * 0.3;
    public static final double Y_POS = 0; // Y position of the molecule in model space.

    // Distance within which transcription factors may attach.
    private static final double TRANSCRIPTION_FACTOR_ATTACHMENT_DISTANCE = 400;

    // Distance within which RNA polymerase may attach.
    private static final double RNA_POLYMERASE_ATTACHMENT_DISTANCE = 400;

    // Default affinity for any given biomolecule.
    public static final double DEFAULT_AFFINITY = 0.05;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Reference to the model in which this is contained.
    private final GeneExpressionModel model;

    // Aspects of the DNA strand that are initialized at construction.
    private final int numBasePairs;
    private final double moleculeLength;
    private final double numberOfTwists;
    private final double leftEdgeXOffset;

    // Points that, when connected, define the shape of the DNA strands.
    private final List<DnaStrandPoint> strandPoints = new ArrayList<DnaStrandPoint>();

    // Shadow of the points that define the strand shapes, used for rapid
    // evaluation of any shape changes.
    private final List<DnaStrandPoint> strandPointsShadow;

    // The backbone strands that are portrayed in the view, which consist of
    // lists of shapes.  This is done so that the shapes can be colored
    // differently and layered in order to create a "twisted" look.
    private final List<DnaStrandSegment> strand1Segments = new ArrayList<DnaStrandSegment>();
    private final List<DnaStrandSegment> strand2Segments = new ArrayList<DnaStrandSegment>();

    // Base pairs within the DNA strand.
    private final ArrayList<BasePair> basePairs = new ArrayList<BasePair>();

    // Genes on this strand of DNA.
    private final ArrayList<Gene> genes = new ArrayList<Gene>();

    // List of forced separations between the two strands.
    private final List<DnaSeparation> separations = new ArrayList<DnaSeparation>();

    // Flag that controls active pursual of transcription factors and polymerase.
    private final boolean pursueAttachments;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor that doesn't specify a model, so a stub model is created.
     * This is primarily for use in creating DNA likenesses on control panels.
     *
     * @param numBasePairs      - number of base pairs in the strand
     * @param leftEdgeXOffset   - x position in model space of the left side of
     *                          the molecule.  Y position is assumed to be zero.
     * @param pursueAttachments - flag that controls whether the DNA strand
     *                          actively pulls in transcription factors and polymerase, or just lets
     *                          them drift into place.
     */
    public DnaMolecule( int numBasePairs, double leftEdgeXOffset, boolean pursueAttachments ) {
        this( new StubGeneExpressionModel(), numBasePairs, leftEdgeXOffset, pursueAttachments );
    }

    /**
     * Constructor.
     *
     * @param model           - The gene expression model within which this DNA strand
     *                        exists.  Needed for evaluation of biomolecule interaction.
     * @param numBasePairs    - The number of base pairs on the DNA strand.  This
     *                        defines the length of the strand.
     * @param leftEdgeXOffset - Offset of the left edge of the DNA strand in
     *                        model space.  This is needed to allow the DNA strand to be initially
     *                        shifted such that a gene is visible to the user when the view is first
     *                        shown.
     */
    public DnaMolecule( GeneExpressionModel model, int numBasePairs, double leftEdgeXOffset, boolean pursueAttachments ) {
        this.model = model;
        this.numBasePairs = numBasePairs;
        this.leftEdgeXOffset = leftEdgeXOffset;
        this.pursueAttachments = pursueAttachments;

        moleculeLength = (double) numBasePairs * DISTANCE_BETWEEN_BASE_PAIRS;
        numberOfTwists = moleculeLength / LENGTH_PER_TWIST;

        // Add the initial set of shape-defining points for each of the two
        // strands.  Points are spaced the same as the base pairs.
        for ( int i = 0; i < moleculeLength / DISTANCE_BETWEEN_BASE_PAIRS; i++ ) {
            double xPos = leftEdgeXOffset + i * DISTANCE_BETWEEN_BASE_PAIRS;
            strandPoints.add( new DnaStrandPoint( xPos, getDnaStrandYPosition( xPos, 0 ), getDnaStrandYPosition( xPos, INTER_STRAND_OFFSET ) ) );
        }

        // Create a shadow of the shape-defining points.  This will be used for
        // detecting shape changes.
        strandPointsShadow = new ArrayList<DnaStrandPoint>( strandPoints.size() );
        for ( DnaStrandPoint strandPoint : strandPoints ) {
            strandPointsShadow.add( new DnaStrandPoint( strandPoint ) );
        }

        // Create the sets of segments that will be observed by the view.
        initializeStrandSegments();

        // Add in the base pairs between the backbone strands.  This calculates
        // the distance between the two strands and puts a line between them in
        // order to look like the base pair.
        double basePairXPos = leftEdgeXOffset;
        while ( basePairXPos <= strandPoints.get( strandPoints.size() - 1 ).xPos ) {
            double strand1YPos = getDnaStrandYPosition( basePairXPos, 0 );
            double strand2YPos = getDnaStrandYPosition( basePairXPos, INTER_STRAND_OFFSET );
            double height = Math.abs( strand1YPos - strand2YPos );
            double basePairYPos = ( strand1YPos + strand2YPos ) / 2;
            basePairs.add( new BasePair( new Point2D.Double( basePairXPos, basePairYPos ), height ) );
            basePairXPos += DISTANCE_BETWEEN_BASE_PAIRS;
        }
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        updateStrandSegments();
        for ( Gene gene : genes ) {
            gene.updateAffinities();
        }
    }

    public double getLength() {
        return moleculeLength;
    }

    /**
     * Add a gene to the DNA strand.  Adding a gene essentially defines it,
     * since in this sim, the base pairs don't actually encode anything, so
     * adding the gene essentially delineates where it is on the strand.
     *
     * @param geneToAdd
     */
    public void addGene( Gene geneToAdd ) {
        genes.add( geneToAdd );
    }

    /**
     * Get the X position of the specified base pair.  The first base pair at
     * the left side of the DNA molecule is base pair 0, and it goes up from
     * there.
     */
    public double getBasePairXOffsetByIndex( int basePairNumber ) {
        return leftEdgeXOffset + INTER_STRAND_OFFSET + (double) basePairNumber * DISTANCE_BETWEEN_BASE_PAIRS;
    }

    public void addSeparation( DnaSeparation separation ) {
        separations.add( separation );
    }

    public void removeSeparation( DnaSeparation separation ) {
        if ( !separations.contains( separation ) ) {
            System.out.println( getClass().getName() + " - Warning: Ignoring attempt to remove separation that can't be found." );
        }
        else {
            separations.remove( separation );
        }
    }

    /**
     * Get the index of the nearest base pair given an X position in model
     * space.
     */
    private int getBasePairIndexFromXOffset( double xOffset ) {
        assert xOffset >= leftEdgeXOffset && xOffset < leftEdgeXOffset + moleculeLength;
        xOffset = MathUtil.clamp( leftEdgeXOffset, xOffset, leftEdgeXOffset + LENGTH_PER_TWIST * numberOfTwists );
        return (int) Math.round( ( xOffset - leftEdgeXOffset - INTER_STRAND_OFFSET ) / DISTANCE_BETWEEN_BASE_PAIRS );
    }

    /**
     * Get the X location of the nearest base pair given an arbitrary x
     * location.
     */
    private double getNearestBasePairXOffset( double xPos ) {
        return getBasePairXOffsetByIndex( getBasePairIndexFromXOffset( xPos ) );
    }

    /**
     * Generate a strand of the DNA molecule.  This is used during construction
     * only and, since there are two strands, it is expected that it be called
     * twice.  The input to this method is the set of shape-defining points.
     * The output is a set of segments, each representing segments, each
     * representing half a twist of the DNA strand in length.  The segments
     * alternate front to back so that when drawn, the twisting nature of the
     * DNA should be visible.
     */
    private static List<DnaStrandSegment> generateDnaStrand( List<Point2D> points, boolean initialInFront ) {
        assert points.size() > 0; // Parameter checking.
        List<DnaStrandSegment> strandSegments = new ArrayList<DnaStrandSegment>();

        List<Point2D> segmentPoints = new ArrayList<Point2D>();
        double segmentStartX = points.get( 0 ).getX();
        boolean inFront = initialInFront;
        for ( Point2D point : points ) {
            segmentPoints.add( point );
            if ( point.getX() - segmentStartX >= ( LENGTH_PER_TWIST / 2 ) ) {
                // Time to add this segment and start a new one.
                strandSegments.add( new DnaStrandSegment( BioShapeUtils.createCurvyLineFromPoints( segmentPoints ), inFront ) );
                segmentPoints.clear();
                segmentPoints.add( point ); // This point must be on this segment too in order to prevent gaps.
                segmentStartX = point.getX();
                inFront = !inFront;
            }
        }
        return strandSegments;
    }

    // Initialize the DNA stand segment lists.
    private void initializeStrandSegments() {
        assert strandPoints.size() > 0; // Parameter checking.

        List<Point2D> strand1SegmentPoints = new ArrayList<Point2D>();
        List<Point2D> strand2SegmentPoints = new ArrayList<Point2D>();
        double segmentStartX = strandPoints.get( 0 ).xPos;
        boolean strand1InFront = true;
        for ( DnaStrandPoint dnaStrandPoint : strandPoints ) {
            double xPos = dnaStrandPoint.xPos;
            strand1SegmentPoints.add( new Point2D.Double( xPos, dnaStrandPoint.strand1YPos ) );
            strand2SegmentPoints.add( new Point2D.Double( xPos, dnaStrandPoint.strand2YPos ) );
            if ( xPos - segmentStartX >= ( LENGTH_PER_TWIST / 2 ) ) {
                // Time to add these segments and start a new ones.
                strand1Segments.add( new DnaStrandSegment( BioShapeUtils.createCurvyLineFromPoints( strand1SegmentPoints ), strand1InFront ) );
                strand2Segments.add( new DnaStrandSegment( BioShapeUtils.createCurvyLineFromPoints( strand2SegmentPoints ), !strand1InFront ) );
                Point2D firstPointOfNextSegment = strand1SegmentPoints.get( strand1SegmentPoints.size() - 1 );
                strand1SegmentPoints.clear();
                strand1SegmentPoints.add( firstPointOfNextSegment ); // This point must be on this segment too in order to prevent gaps.
                firstPointOfNextSegment = strand2SegmentPoints.get( strand2SegmentPoints.size() - 1 );
                strand2SegmentPoints.clear();
                strand2SegmentPoints.add( firstPointOfNextSegment ); // This point must be on this segment too in order to prevent gaps.
                segmentStartX = firstPointOfNextSegment.getX();
                strand1InFront = !strand1InFront;
            }
        }
    }

    /**
     * Get the Y position in model space for a DNA strand for the given X
     * position and offset.  The offset acts like a "phase difference", thus
     * allowing this method to be used to get location information for both
     * DNA strands.
     *
     * @param xPos
     * @param offset
     * @return
     */
    private double getDnaStrandYPosition( double xPos, double offset ) {
        return Math.sin( ( xPos + offset ) / LENGTH_PER_TWIST * Math.PI * 2 ) * DIAMETER / 2;
    }

    /**
     * Update the strand segment shapes based on things that might have
     * changed, such as biomolecules attaching and separating the strands or
     * otherwise deforming the nominal double-helix shape.
     */
    private void updateStrandSegments() {

        // Set the shadow points to the nominal, non-deformed positions.
        for ( DnaStrandPoint dnaStrandPoint : strandPointsShadow ) {
            dnaStrandPoint.strand1YPos = getDnaStrandYPosition( dnaStrandPoint.xPos, 0 );
            dnaStrandPoint.strand2YPos = getDnaStrandYPosition( dnaStrandPoint.xPos, INTER_STRAND_OFFSET );
        }

        // Move the shadow points to account for any separations.
        for ( DnaSeparation separation : separations ) {
            double windowWidth = separation.getAmount() * 1.5; // Make the window wider than it is high.  This was chosen to look decent, tweak if needed.
            IntegerRange separationWindowXIndexRange = new IntegerRange( (int) Math.floor( ( separation.getXPos() - ( windowWidth / 2 ) - leftEdgeXOffset ) / DISTANCE_BETWEEN_BASE_PAIRS ),
                                                                         (int) Math.floor( ( separation.getXPos() + ( windowWidth / 2 ) - leftEdgeXOffset ) / DISTANCE_BETWEEN_BASE_PAIRS ) );
            for ( int i = separationWindowXIndexRange.getMin(); i < separationWindowXIndexRange.getMax(); i++ ) {
                double windowCenterX = ( separationWindowXIndexRange.getMin() + separationWindowXIndexRange.getMax() ) / 2;
                if ( i >= 0 && i < strandPointsShadow.size() ) {

                    // Perform a windowing algorithm that weights the separation
                    // at 1 in the center, 0 at the edges, and linear
                    // graduations in between.  By 
                    double separationWeight = 1 - Math.abs( 2 * ( i - windowCenterX ) / separationWindowXIndexRange.getLength() );
                    strandPointsShadow.get( i ).strand1YPos = ( 1 - separationWeight ) * strandPointsShadow.get( i ).strand1YPos +
                                                              separationWeight * separation.getAmount() / 2;
                    strandPointsShadow.get( i ).strand2YPos = ( 1 - separationWeight ) * strandPointsShadow.get( i ).strand2YPos -
                                                              separationWeight * separation.getAmount() / 2;
                }
            }
        }

        // See if any of the points have moved and, if so, update the
        // corresponding shape segment.
        int numSegments = strand1Segments.size();
        assert numSegments == strand2Segments.size(); // Should be the same, won't work if not.
        for ( int i = 0; i < numSegments; i++ ) {
            boolean segmentChanged = false;
            DnaStrandSegment strand1Segment = strand1Segments.get( i );
            DnaStrandSegment strand2Segment = strand2Segments.get( i );

            // Determine the bounds of the current segment.  Assumes that the
            // bounds for the strand1 and strand2 segments are the same, which
            // should be a safe assumption.
            Rectangle2D bounds = strand1Segment.getShape().getBounds2D();
            IntegerRange pointIndexRange = new IntegerRange( (int) Math.floor( ( bounds.getMinX() - leftEdgeXOffset ) / DISTANCE_BETWEEN_BASE_PAIRS ),
                                                             (int) Math.floor( ( bounds.getMaxX() - leftEdgeXOffset ) / DISTANCE_BETWEEN_BASE_PAIRS ) );

            // Check to see if any of the points within the identified range
            // have changed and, if so, update the corresponding segment shape
            // in the strands.  If the points for either strand has changed,
            // both are updated.
            for ( int j = pointIndexRange.getMin(); j <= pointIndexRange.getMax(); j++ ) {
                if ( !strandPoints.get( j ).equals( strandPointsShadow.get( j ) ) ) {
                    // The point has changed.  Update it, mark the change.
                    strandPoints.get( j ).set( strandPointsShadow.get( j ) );
                    segmentChanged = true;
                }
            }

            if ( segmentChanged ) {
                // Update the shape of this segment.
                List<Point2D> strand1ShapePoints = new ArrayList<Point2D>();
                List<Point2D> strand2ShapePoints = new ArrayList<Point2D>();
                for ( int j = pointIndexRange.getMin(); j <= pointIndexRange.getMax(); j++ ) {
                    strand1ShapePoints.add( new Point2D.Double( strandPoints.get( j ).xPos, strandPoints.get( j ).strand1YPos ) );
                    strand2ShapePoints.add( new Point2D.Double( strandPoints.get( j ).xPos, strandPoints.get( j ).strand2YPos ) );
                }
                strand1Segment.setShape( BioShapeUtils.createCurvyLineFromPoints( strand1ShapePoints ) );
                strand2Segment.setShape( BioShapeUtils.createCurvyLineFromPoints( strand2ShapePoints ) );
            }
        }
    }

    public List<DnaStrandSegment> getStrand1Segments() {
        return strand1Segments;
    }

    public List<DnaStrandSegment> getStrand2Segments() {
        return strand2Segments;
    }

    public ArrayList<Gene> getGenes() {
        return genes;
    }

    public Gene getLastGene() {
        return genes.get( genes.size() - 1 );
    }

    public ArrayList<BasePair> getBasePairs() {
        return basePairs;
    }

    public void activateHints( MobileBiomolecule biomolecule ) {
        for ( Gene gene : genes ) {
            gene.activateHints( biomolecule );
        }
    }

    public void deactivateAllHints() {
        for ( Gene gene : genes ) {
            gene.deactivateHints();
        }
    }

    /**
     * Get the position in model space of the leftmost edge of the DNA strand.
     * The Y position is in the vertical center of the strand.
     */
    public Point2D getLeftEdgePos() {
        return new Point2D.Double( leftEdgeXOffset, Y_POS );
    }

    /**
     * Consider an attachment proposal from a transcription factor instance.
     * To determine whether or not to accept or reject this proposal, the base
     * pairs are scanned in order to determine whether there is an appropriate
     * and available attachment site within the attachment distance.
     *
     * @param transcriptionFactor
     * @return
     */
    public AttachmentSite considerProposalFrom( TranscriptionFactor transcriptionFactor ) {
        List<AttachmentSite> potentialAttachmentSites = new ArrayList<AttachmentSite>();
        for ( int i = 0; i < basePairs.size(); i++ ) {
            // See if the base pair is within the max attachment distance.
            if ( basePairs.get( i ).getCenterLocation().distance( transcriptionFactor.getPosition() ) <= TRANSCRIPTION_FACTOR_ATTACHMENT_DISTANCE ) {
                // In range.  Add it to the list if it is available.
                AttachmentSite potentialAttachmentSite = getTranscriptionFactorAttachmentSiteForBasePairIndex( i, transcriptionFactor.getConfig() );
                if ( potentialAttachmentSite.attachedOrAttachingMolecule.get() == null ) {
                    potentialAttachmentSites.add( potentialAttachmentSite );
                }
            }
        }

        // If there aren't any potential attachment sites in range, check for
        // a particular set of conditions under which the DNA provides an
        // attachment site anyways.
        if ( potentialAttachmentSites.size() == 0 && pursueAttachments ) {
            for ( Gene gene : genes ) {
                AttachmentSite matchingSite = gene.getMatchingSite( transcriptionFactor.getConfig() );
                if ( matchingSite != null ) {
                    // Found a matching site on a gene.
                    if ( matchingSite.attachedOrAttachingMolecule.get() == null ) {
                        // The site is unoccupied, so add it to the list of
                        // potential sites.
                        potentialAttachmentSites.add( matchingSite );
                    }
                    else if ( !matchingSite.isMoleculeAttached() ) {
                        double thisDistance = transcriptionFactor.getPosition().distance( matchingSite.locationProperty.get() );
                        double thatDistance = matchingSite.attachedOrAttachingMolecule.get().getPosition().distance( matchingSite.locationProperty.get() );
                        if ( thisDistance < thatDistance ) {
                            // The other molecule is not yet attached, and this
                            // one is closer, so force the other molecule to
                            // abort its pending attachment.
                            matchingSite.attachedOrAttachingMolecule.get().forceAbortPendingAttachment();
                            // Add this site to the list of potential sites.
                            potentialAttachmentSites.add( matchingSite );
                        }
                    }
                }
            }
        }

        // Eliminate sites where attaching would cause the biomolecule to move
        // out of its motion bounds.
        eliminateOutOfBoundsAttachmentSites( transcriptionFactor, potentialAttachmentSites );

        // Eliminate any attachment site where attaching would cause overlap
        // with other biomolecules that are already on the DNA strand.
        eliminateOverlappedAttachmentSites( transcriptionFactor, potentialAttachmentSites );

        if ( potentialAttachmentSites.size() == 0 ) {
            // No acceptable sites found.
            return null;
        }

        // Sort the collection so that the best match is first.
        Collections.sort( potentialAttachmentSites, new AttachmentSiteComparator<AttachmentSite>( transcriptionFactor.getPosition() ) );

        // Return the first attachment site on the list.
        return potentialAttachmentSites.get( 0 );
    }

    /**
     * Consider an attachment proposal from an instance of RNA polymerase.
     *
     * @param rnaPolymerase
     * @return
     */
    public AttachmentSite considerProposalFrom( RnaPolymerase rnaPolymerase ) {
        List<AttachmentSite> potentialAttachmentSites = new ArrayList<AttachmentSite>();
        for ( int i = 0; i < basePairs.size(); i++ ) {
            // See if the base pair is within the max attachment distance.
            Point2D attachmentSiteLocation = new java.awt.geom.Point2D.Double( basePairs.get( i ).getCenterLocation().getX(), Y_POS );
            if ( attachmentSiteLocation.distance( rnaPolymerase.getPosition() ) <= RNA_POLYMERASE_ATTACHMENT_DISTANCE ) {
                // In range.  Add it to the list if it is available.
                AttachmentSite potentialAttachmentSite = getRnaPolymeraseAttachmentSiteForBasePairIndex( i );
                if ( potentialAttachmentSite.attachedOrAttachingMolecule.get() == null ) {
                    potentialAttachmentSites.add( potentialAttachmentSite );
                }
            }
        }

        // If there aren't any potential attachment sites in range, check for
        // a particular set of conditions under which the DNA provides an
        // attachment site anyways.
        if ( potentialAttachmentSites.size() == 0 && pursueAttachments ) {
            for ( Gene gene : genes ) {
                if ( gene.transcriptionFactorsSupportTranscription() ) {
                    AttachmentSite matchingSite = gene.getPolymeraseAttachmentSite();
                    // Found a matching site on a gene.
                    if ( matchingSite.attachedOrAttachingMolecule.get() == null ) {
                        // The site is unoccupied, so add it to the list of
                        // potential sites.
                        potentialAttachmentSites.add( matchingSite );
                    }
                    else if ( !matchingSite.isMoleculeAttached() ) {
                        double thisDistance = rnaPolymerase.getPosition().distance( matchingSite.locationProperty.get() );
                        double thatDistance = matchingSite.attachedOrAttachingMolecule.get().getPosition().distance( matchingSite.locationProperty.get() );
                        if ( thisDistance < thatDistance ) {
                            // The other molecule is not yet attached, and this
                            // one is closer, so force the other molecule to
                            // abort its pending attachment.
                            matchingSite.attachedOrAttachingMolecule.get().forceAbortPendingAttachment();
                            // Add this site to the list of potential sites.
                            potentialAttachmentSites.add( matchingSite );
                        }
                    }
                }
            }
        }

        // Eliminate any attachment site where attaching would cause the
        // biomolecule to go out of bounds.
        eliminateOutOfBoundsAttachmentSites( rnaPolymerase, potentialAttachmentSites );

        // Eliminate any attachment site where attaching would cause overlap
        // with other biomolecules that are already on the DNA strand.
        eliminateOverlappedAttachmentSites( rnaPolymerase, potentialAttachmentSites );

        if ( potentialAttachmentSites.size() == 0 ) {
            // No acceptable sites found.
            return null;
        }

        // Sort the collection so that the best site is at the top of the list.
        Collections.sort( potentialAttachmentSites, new AttachmentSiteComparator<AttachmentSite>( rnaPolymerase.getPosition() ) );

        // Return the optimal attachment site.
        return potentialAttachmentSites.get( 0 );
    }

    /**
     * Take a list of attachment sites and eliminate any of them that, if the
     * given molecule attaches, it would end up overlapping with another
     * biomolecule that is already attached to the DNA strand.
     *
     * @param biomolecule              - the biomolecule that is potentially going to
     *                                 attach to the provided list of attachment sites.
     * @param potentialAttachmentSites - attachment sites where the given
     *                                 biomolecule could attach.
     */
    private void eliminateOverlappedAttachmentSites( MobileBiomolecule biomolecule, List<AttachmentSite> potentialAttachmentSites ) {
        for ( AttachmentSite potentialAttachmentSite : new ArrayList<AttachmentSite>( potentialAttachmentSites ) ) {
            ImmutableVector2D translationVector = new ImmutableVector2D( biomolecule.getPosition(), potentialAttachmentSite.locationProperty.get() );
            AffineTransform transform = AffineTransform.getTranslateInstance( translationVector.getX(), translationVector.getY() );
            Shape translatedShape = transform.createTransformedShape( biomolecule.getShape() );
            for ( MobileBiomolecule mobileBiomolecule : model.getOverlappingBiomolecules( translatedShape ) ) {
                if ( mobileBiomolecule.attachedToDna.get() && mobileBiomolecule != biomolecule ) {
                    // Eliminate this attachment site, since attaching to it
                    // would cause overlap with molecules already there.
                    potentialAttachmentSites.remove( potentialAttachmentSite );
                    break;
                }
            }
        }
    }

    /**
     * Take a list of attachment sites and eliminate any of them that, if the
     * given molecule attaches, it would end up with some portion of it outside
     * of its motion bounds.
     *
     * @param biomolecule              The biomolecule that is potentially going to
     *                                 attach to the provided list of attachment sites.
     * @param potentialAttachmentSites Attachment sites where the given
     *                                 biomolecule could attach.
     */
    private void eliminateOutOfBoundsAttachmentSites( MobileBiomolecule biomolecule, List<AttachmentSite> potentialAttachmentSites ) {
        for ( AttachmentSite potentialAttachmentSite : new ArrayList<AttachmentSite>( potentialAttachmentSites ) ) {
            ImmutableVector2D translationVector = new ImmutableVector2D( biomolecule.getPosition(), potentialAttachmentSite.locationProperty.get() );
            AffineTransform transform = AffineTransform.getTranslateInstance( translationVector.getX(), translationVector.getY() );
            Shape translatedShape = transform.createTransformedShape( biomolecule.getShape() );
            if ( !biomolecule.motionBoundsProperty.get().inBounds( translatedShape ) ) {
                // This attachment site would put the biomolecule out of its
                // motion bounds, so eliminate it.
                potentialAttachmentSites.remove( potentialAttachmentSite );
            }
        }
    }

    private AttachmentSite getTranscriptionFactorAttachmentSiteForBasePairIndex( int i, TranscriptionFactorConfig tfConfig ) {
        // See if this base pair is inside a gene.
        Gene gene = getGeneContainingBasePair( i );
        if ( gene != null ) {
            // Base pair is in a gene, so get it from the gene.
            return gene.getTranscriptionFactorAttachmentSite( i, tfConfig );
        }
        else {
            // Base pair is not contained within a gene, so use the default.
            return createDefaultAffinityAttachmentSite( i );
        }
    }

    private AttachmentSite getRnaPolymeraseAttachmentSiteForBasePairIndex( int i ) {
        // See if this base pair is inside a gene.
        Gene gene = getGeneContainingBasePair( i );
        if ( gene != null ) {
            // Base pair is in a gene.  See if site is available.
            return gene.getPolymeraseAttachmentSite( i );
        }
        else {
            // Base pair is not contained within a gene, so use the default.
            return createDefaultAffinityAttachmentSite( i );
        }
    }

    /**
     * Get the two base pair attachment sites that are next to the provided
     * one, i.e. the one before it on the DNA strand and the one after it.  If
     * at one end of the strand, only one site will be returned.  Occupied
     * sites are not returned.
     *
     * @param attachmentSite
     * @return
     */
    public List<AttachmentSite> getAdjacentAttachmentSites( TranscriptionFactor transcriptionFactor, AttachmentSite attachmentSite ) {
        int basePairIndex = getBasePairIndexFromXOffset( attachmentSite.locationProperty.get().getX() );
        List<AttachmentSite> attachmentSites = new ArrayList<AttachmentSite>();
        if ( basePairIndex != 0 ) {
            AttachmentSite potentialSite = getTranscriptionFactorAttachmentSiteForBasePairIndex( basePairIndex - 1, transcriptionFactor.getConfig() );
            if ( potentialSite.attachedOrAttachingMolecule.get() == null ) {
                attachmentSites.add( potentialSite );
            }
        }
        if ( basePairIndex != basePairs.size() - 1 ) {
            AttachmentSite potentialSite = getTranscriptionFactorAttachmentSiteForBasePairIndex( basePairIndex + 1, transcriptionFactor.getConfig() );
            if ( potentialSite.attachedOrAttachingMolecule.get() == null ) {
                attachmentSites.add( potentialSite );
            }
        }

        // Eliminate any attachment site where attaching would cause the
        // biomolecule to go out of bounds.
        eliminateOutOfBoundsAttachmentSites( transcriptionFactor, attachmentSites );

        // Eliminate any sites that, if attached to, would cause overlapping
        // biomolecules on the same strand.
        eliminateOverlappedAttachmentSites( transcriptionFactor, attachmentSites );

        return attachmentSites;
    }

    /**
     * Get the two base pair attachment sites that are next to the provided
     * one, i.e. the one before it on the DNA strand and the one after it.  If
     * at one end of the strand, only one site will be returned.  Occupied
     * sites are not returned.
     *
     * @param attachmentSite
     * @return
     */
    public List<AttachmentSite> getAdjacentAttachmentSites( RnaPolymerase rnaPolymerase, AttachmentSite attachmentSite ) {
        int basePairIndex = getBasePairIndexFromXOffset( attachmentSite.locationProperty.get().getX() );
        List<AttachmentSite> attachmentSites = new ArrayList<AttachmentSite>();
        if ( basePairIndex != 0 ) {
            AttachmentSite potentialSite = getRnaPolymeraseAttachmentSiteForBasePairIndex( basePairIndex - 1 );
            if ( potentialSite.attachedOrAttachingMolecule.get() == null ) {
                attachmentSites.add( potentialSite );
            }
        }
        if ( basePairIndex != basePairs.size() - 1 ) {
            AttachmentSite potentialSite = getRnaPolymeraseAttachmentSiteForBasePairIndex( basePairIndex + 1 );
            if ( potentialSite.attachedOrAttachingMolecule.get() == null ) {
                attachmentSites.add( potentialSite );
            }
        }

        // Eliminate any attachment site where attaching would cause the
        // biomolecule to go out of bounds.
        eliminateOutOfBoundsAttachmentSites( rnaPolymerase, attachmentSites );

        // Eliminate any sites that, if attached to, would cause overlapping
        // biomolecules on the same strand.
        eliminateOverlappedAttachmentSites( rnaPolymerase, attachmentSites );

        return attachmentSites;
    }

    private Gene getGeneContainingBasePair( int basePairIndex ) {
        Gene geneContainingBasePair = null;
        for ( Gene gene : genes ) {
            if ( gene.containsBasePair( basePairIndex ) ) {
                geneContainingBasePair = gene;
                break;
            }
        }
        return geneContainingBasePair;
    }

    /**
     * Create an attachment site instance with the default affinity for all
     * DNA-attaching biomolecules at the specified x offset.
     *
     * @param xOffset
     * @return
     */
    public AttachmentSite createDefaultAffinityAttachmentSite( double xOffset ) {
        return new AttachmentSite( new Point2D.Double( getNearestBasePairXOffset( xOffset ), Y_POS ), DEFAULT_AFFINITY );
    }

    public AttachmentSite createDefaultAffinityAttachmentSite( int basePairIndex ) {
        return new AttachmentSite( new Point2D.Double( getBasePairXOffsetByIndex( basePairIndex ), Y_POS ), DEFAULT_AFFINITY );
    }

    /**
     * Get a reference to the gene that contains the given location.
     *
     * @param location
     * @return Gene at the location, null if no gene exists.
     */
    public Gene getGeneAtLocation( Point2D location ) {
        boolean isLocationOnMolecule = location.getX() >= leftEdgeXOffset && location.getX() <= leftEdgeXOffset + moleculeLength &&
                                       location.getY() >= Y_POS - DIAMETER / 2 && location.getY() <= Y_POS + DIAMETER / 2;
        assert isLocationOnMolecule; // At the time of this development, this method should never be called when not on the DNA molecule.
        Gene geneAtLocation = null;
        int basePairIndex = getBasePairIndexFromXOffset( location.getX() );
        if ( isLocationOnMolecule ) {
            for ( Gene gene : genes ) {
                if ( gene.containsBasePair( basePairIndex ) ) {
                    // Found the corresponding gene.
                    geneAtLocation = gene;
                    break;
                }
            }
        }
        return geneAtLocation;
    }

    public void reset() {
        for ( Gene gene : genes ) {
            gene.clearAttachmentSites();
        }
        separations.clear();
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    /**
     * This class defines a segment of the DNA strand.  It is needed because the
     * DNA molecule needs to look like it is 3D, but we are only modeling it as
     * 2D, so in order to create the appearance of a twist between the two
     * strands, we need to track which segments are in front and which are in
     * back.
     */
    public static class DnaStrandSegment extends ShapeChangingModelElement {
        public final boolean inFront;

        public DnaStrandSegment( Shape shape, boolean inFront ) {
            super( shape );
            this.inFront = inFront;
        }

        public void setShape( Shape newShape ) {
            shapeProperty.set( newShape );
        }
    }

    /**
     * Class with one x position and two y positions, used for defining the
     * two strands that comprise the backbone of one DNA molecule.
     */
    protected class DnaStrandPoint {
        public double xPos = 0;
        public double strand1YPos = 0;
        public double strand2YPos = 0;

        public DnaStrandPoint( double xPos, double strand1YPos, double strand2YPos ) {
            this.xPos = xPos;
            this.strand1YPos = strand1YPos;
            this.strand2YPos = strand2YPos;
        }

        public DnaStrandPoint( DnaStrandPoint strandPoint ) {
            xPos = strandPoint.xPos;
            strand1YPos = strandPoint.strand1YPos;
            strand2YPos = strandPoint.strand2YPos;
        }

        public void set( DnaStrandPoint dnaStrandPoint ) {
            this.xPos = dnaStrandPoint.xPos;
            this.strand1YPos = dnaStrandPoint.strand1YPos;
            this.strand2YPos = dnaStrandPoint.strand2YPos;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) { return true; }
            if ( o == null || getClass() != o.getClass() ) { return false; }

            DnaStrandPoint that = (DnaStrandPoint) o;

            if ( Double.compare( that.strand1YPos, strand1YPos ) != 0 ) {
                return false;
            }
            if ( Double.compare( that.strand2YPos, strand2YPos ) != 0 ) {
                return false;
            }
            return Double.compare( that.xPos, xPos ) == 0;
        }
    }

    // Comparator class to use when comparing two attachment sites.
    private static class AttachmentSiteComparator<T extends AttachmentSite> implements Comparator<T> {
        private final Point2D attachLocation;

        private AttachmentSiteComparator( Point2D attachLocation ) {
            this.attachLocation = attachLocation;
        }

        // Compare the two attachment sites.  The comparison is based on a
        // combination of the affinity and the distance.
        public int compare( T attachmentSite1, T attachmentSite2 ) {
            // The comparison is based on a combination of the affinity and the
            // distance, much like gravitational attraction.  The exponent
            // effectively sets the relative weighting of one versus another.
            // An exponent value of zero means only the affinity matters, a
            // value of 100 means it is pretty much entirely distance.  A value
            // of 2 is how gravity works, so it appears kind of natural.  Tweak
            // as needed.
            double exponent = 1;
            double as1Factor = attachmentSite1.getAffinity() / Math.pow( attachLocation.distance( attachmentSite1.locationProperty.get() ), exponent );
            double as2Factor = attachmentSite2.getAffinity() / Math.pow( attachLocation.distance( attachmentSite2.locationProperty.get() ), exponent );
            return Double.compare( as2Factor, as1Factor );
        }

        public boolean equals( Object obj ) {
            // Stubbed, because it isn't needed.
            assert false; // Make sure no one calls this.
            return false;
        }
    }
}
