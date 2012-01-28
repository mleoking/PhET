// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.ShapeChangingModelElement;

/**
 * This class models a molecule of DNA in the model.  It includes the shape of
 * the two strands of the DNA and the base pairs, defines where the various
 * genes reside, and retains other information about the DNA molecule.  This is
 * an important and central object in the model for this simulation.
 * <p/>
 * A big simplifying assumption that this class makes is that molecules that
 * attach to the DNA do so to individual base pairs.  In reality, biomolecules
 * attach to SETS OF THREE base pairs (called "codons") that dictate where
 * biomolecules attach. This was unnecessarily complicated for the needs of
 * this sim.
 *
 * @author John Blanco
 */
public class DnaMolecule {

    // Constants the define the size and related attributes of the strand.
    public static final double STRAND_DIAMETER = 200; // In picometers.
    private static final double LENGTH_PER_TWIST = 340; // In picometers.
    private static final int BASE_PAIRS_PER_TWIST = 10; // In picometers.
    public static final double DISTANCE_BETWEEN_BASE_PAIRS = LENGTH_PER_TWIST / BASE_PAIRS_PER_TWIST;
    private static final double INTER_STRAND_OFFSET = LENGTH_PER_TWIST * 0.3;
    private static final int NUMBER_OF_TWISTS = 130;
    private static final int NUMBER_OF_BASE_PAIRS = BASE_PAIRS_PER_TWIST * NUMBER_OF_TWISTS;
    public static final double MOLECULE_LENGTH = NUMBER_OF_TWISTS * LENGTH_PER_TWIST;
    private static final double DISTANCE_BETWEEN_GENES = 15000; // In picometers.
    private static final double LEFT_EDGE_X_POS = -DISTANCE_BETWEEN_GENES;
    public static final double Y_POS = 0;

    // Distance within which transcription factors may attach.
    private static final double TRANSCRIPTION_FACTOR_ATTACHMENT_DISTANCE = 400;

    // Distance within which RNA polymerase may attach.
    private static final double RNA_POLYMERASE_ATTACHMENT_DISTANCE = 400;

    private DnaStrand strand1;
    private DnaStrand strand2;
    private ArrayList<BasePair> basePairs = new ArrayList<BasePair>();
    private ArrayList<Gene> genes = new ArrayList<Gene>();

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
                                        Math.sin( ( basePairXPos - LEFT_EDGE_X_POS ) / LENGTH_PER_TWIST * 2 * Math.PI ) ) ) * STRAND_DIAMETER / 2;
            double basePairYPos = ( Math.sin( ( basePairXPos - LEFT_EDGE_X_POS - INTER_STRAND_OFFSET ) / LENGTH_PER_TWIST * 2 * Math.PI ) +
                                    Math.sin( ( basePairXPos - LEFT_EDGE_X_POS ) / LENGTH_PER_TWIST * 2 * Math.PI ) ) / 2 * STRAND_DIAMETER / 2;
            basePairs.add( new BasePair( new Point2D.Double( basePairXPos, basePairYPos ), height ) );
            basePairXPos += DISTANCE_BETWEEN_BASE_PAIRS;
        }

        // Add the genes.  The initial parameters can be tweaked in order to
        // adjust the sizes of the genes on the screen.
        int regRegionSize = 16;                   // Base pairs in the regulatory region for all genes.
        int gene1TranscribedRegionSize = 100;     // Base pairs in transcribed region.
        int gene2TranscribedRegionSize = 150;     // Base pairs in transcribed region.
        int gene3TranscribedRegionSize = 200;     // Base pairs in transcribed region.

        // The first gene is set up to be centered at or near (0,0) in model
        // space to avoid having to scroll the DNA at startup.
        int startIndex = getBasePairIndexFromXOffset( 0 ) - ( regRegionSize + gene1TranscribedRegionSize ) / 2;
        genes.add( new Gene( this,
                             new IntegerRange( startIndex, startIndex + regRegionSize ),
                             new Color( 216, 191, 216 ),
                             new IntegerRange( startIndex + regRegionSize, startIndex + regRegionSize + gene1TranscribedRegionSize ),
                             new Color( 255, 165, 79, 150 ),
                             1 ) );
        startIndex += DISTANCE_BETWEEN_GENES / DISTANCE_BETWEEN_BASE_PAIRS;
        genes.add( new Gene( this,
                             new IntegerRange( startIndex, startIndex + regRegionSize ),
                             new Color( 216, 191, 216 ),
                             new IntegerRange( startIndex + regRegionSize, startIndex + regRegionSize + gene2TranscribedRegionSize ),
                             new Color( 240, 246, 143, 150 ),
                             2 ) );
        startIndex += DISTANCE_BETWEEN_GENES / DISTANCE_BETWEEN_BASE_PAIRS;
        genes.add( new Gene( this,
                             new IntegerRange( startIndex, startIndex + regRegionSize ),
                             new Color( 216, 191, 216 ),
                             new IntegerRange( startIndex + regRegionSize, startIndex + regRegionSize + gene3TranscribedRegionSize ),
                             new Color( 205, 255, 112, 150 ),
                             3 ) );
    }

    /**
     * Get the X position of the specified base pair.  The first base pair at
     * the left side of the DNA molecule is base pair 0, and it goes up from
     * there.
     */
    public double getBasePairXOffsetByIndex( int basePairNumber ) {
        return LEFT_EDGE_X_POS + INTER_STRAND_OFFSET + (double) basePairNumber * DISTANCE_BETWEEN_BASE_PAIRS;
    }

    /**
     * Get the index of the nearest base pair given an X position in model
     * space.
     */
    private int getBasePairIndexFromXOffset( double xOffset ) {
        assert xOffset >= LEFT_EDGE_X_POS && xOffset < LEFT_EDGE_X_POS + MOLECULE_LENGTH;
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
                segmentPath.quadTo( xOffset + LENGTH_PER_TWIST / 4, STRAND_DIAMETER / 2 * 2.0,
                                    xOffset + LENGTH_PER_TWIST / 2, 0 );
            }
            else {
                segmentPath.quadTo( xOffset + LENGTH_PER_TWIST / 4, -STRAND_DIAMETER / 2 * 2.0,
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

    // Generate a single DNA strand, i.e. one side of the double helix.
    private DnaStrand generateDnaStrandOld( double initialOffset, double length, boolean initialInFront ) {
        double xOffset = initialOffset;
        boolean inFront = initialInFront;
        boolean curveUp = true;
        DnaStrand dnaStrand = new DnaStrand();
        while ( xOffset + LENGTH_PER_TWIST < length ) {
            // Create the next segment.
            DoubleGeneralPath segmentPath = new DoubleGeneralPath();
            segmentPath.moveTo( xOffset, Y_POS );
            if ( curveUp ) {
                segmentPath.quadTo( xOffset + LENGTH_PER_TWIST / 4, STRAND_DIAMETER / 2 * 2.0,
                                    xOffset + LENGTH_PER_TWIST / 2, 0 );
            }
            else {
                segmentPath.quadTo( xOffset + LENGTH_PER_TWIST / 4, -STRAND_DIAMETER / 2 * 2.0,
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
        return new Point2D.Double( LEFT_EDGE_X_POS, Y_POS );
    }

    public double getDiameter() {
        return STRAND_DIAMETER;
    }

    public List<AttachmentSite> getNearbyPolymeraseAttachmentSites( Point2D position ) {
        List<AttachmentSite> nearbyAttachmentSites = new ArrayList<AttachmentSite>();
        // TODO: Fix or replace.
//        IntegerRange basePairsToScan = getBasePairScanningRange( position.getX() );
//        for ( int i = basePairsToScan.getMin(); i <= basePairsToScan.getMax(); i++ ) {
//            Gene gene = getGeneContainingBasePair( i );
//            if ( gene != null ) {
//                nearbyAttachmentSites.add( gene.getPolymeraseAttachmentSite( i ) );
//            }
//            else {
//                // Base pair is not contained within a gene, so use the default.
//                nearbyAttachmentSites.add( createDefaultAffinityAttachmentSite( i ) );
//            }
//        }
        return nearbyAttachmentSites;
    }

    public List<AttachmentSite> getNearbyTranscriptionFactorAttachmentSites( Point2D position ) {
        List<AttachmentSite> nearbyAttachmentSites = new ArrayList<AttachmentSite>();
        IntegerRange basePairsToScan = getBasePairScanningRange( position.getX() );
        for ( int i = basePairsToScan.getMin(); i <= basePairsToScan.getMax(); i++ ) {
            Gene gene = getGeneContainingBasePair( i );
            if ( gene != null ) {
                nearbyAttachmentSites.add( gene.getTranscriptionFactorAttachmentSite( i ) );
            }
            else {
                // Base pair is not contained within a gene, so use the default.
                nearbyAttachmentSites.add( createDefaultAffinityAttachmentSite( i ) );
            }
        }
        return nearbyAttachmentSites;
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
                AttachmentSite potentialAttachmentSite = getTranscriptionFactorAttachmentSiteForBasePairIndex( i );
                if ( potentialAttachmentSite.attachedOrAttachingMolecule.get().isNone() ) {
                    potentialAttachmentSites.add( potentialAttachmentSite );
                }
            }
        }

        if ( potentialAttachmentSites.size() == 0 ) {
            // No acceptable sites found.
            return null;
        }

        Collections.sort( potentialAttachmentSites, new AttachmentSiteComparator<AttachmentSite>( transcriptionFactor.getPosition() ) );
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
            if ( basePairs.get( i ).getCenterLocation().distance( rnaPolymerase.getPosition() ) <= RNA_POLYMERASE_ATTACHMENT_DISTANCE ) {
                // In range.  Add it to the list if it is available.
                AttachmentSite potentialAttachmentSite = getRnaPolymeraseAttachmentSiteForBasePairIndex( i );
                if ( potentialAttachmentSite.attachedOrAttachingMolecule.get().isNone() ) {
                    potentialAttachmentSites.add( potentialAttachmentSite );
                }
            }
        }

        if ( potentialAttachmentSites.size() == 0 ) {
            // No acceptable sites found.
            return null;
        }

        // Sort the collection so that the best site is at the top of the list.
        Collections.sort( potentialAttachmentSites, new AttachmentSiteComparator<AttachmentSite>( rnaPolymerase.getPosition() ) );

        // Return the optimal attachment site.
        return potentialAttachmentSites.get( 0 );
    }

    private AttachmentSite getTranscriptionFactorAttachmentSiteForBasePairIndex( int i ) {
        // See if this base pair is inside a gene.
        Gene gene = getGeneContainingBasePair( i );
        if ( gene != null ) {
            // Base pair is in a gene.  See if site is available.
            return gene.getTranscriptionFactorAttachmentSite( i );
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
     * at one end of the strand, only one site will be returned.
     *
     * @param attachmentSite
     * @return
     */
    public List<AttachmentSite> getAdjacentTranscriptionFactorAttachmentSites( AttachmentSite attachmentSite ) {
        // TODO: Fix this up when base pairs each have their own attachment sites.
        int basePairIndex = getBasePairIndexFromXOffset( attachmentSite.locationProperty.get().getX() );
        if ( basePairIndex == 0 || basePairIndex == basePairs.size() - 1 ) {
            System.out.println( getClass().getName() + " Suspicious index for base pair, value = " + basePairIndex );
        }
        List<AttachmentSite> attachmentSites = new ArrayList<AttachmentSite>();
        if ( basePairIndex != 0 ) {
            attachmentSites.add( getTranscriptionFactorAttachmentSiteForBasePairIndex( basePairIndex - 1 ) );
        }
        if ( basePairIndex != basePairs.size() - 1 ) {
            attachmentSites.add( getTranscriptionFactorAttachmentSiteForBasePairIndex( basePairIndex + 1 ) );
        }
        return attachmentSites;
    }

    /**
     * Get a list of all attachments sites for transcription factors within a
     * specified radius from a given point in model space.
     *
     * @param position
     * @param distance
     * @return
     */
    public List<AttachmentSite> getTranscriptionFactorAttachmentSites( Point2D position, double distance ) {
        List<AttachmentSite> attachmentSites = new ArrayList<AttachmentSite>();
        // Attachment sites are associated with base pairs, so index through
        // all base pairs and determine the attachment sites that are in range.
        for ( int i = 0; i < basePairs.size(); i++ ) {
            if ( basePairs.get( i ).getCenterLocation().distance( position ) <= distance ) {
                // This base pair is in range.  All such base pairs have some
                // affinity for transcription factors, but some have more.
                // Determine whether this is a normal- or high-affinity site.
                Gene gene = getGeneContainingBasePair( i );
                if ( gene != null ) {
                    attachmentSites.add( gene.getTranscriptionFactorAttachmentSite( i ) );
                }
                else {
                    // Base pair is not contained within a gene, so use the default.
                    attachmentSites.add( createDefaultAffinityAttachmentSite( i ) );
                }

            }
        }
        return attachmentSites;
    }

    public List<BasePair> getBasePairsWithinDistance( Point2D position, double distance ) {
        List<BasePair> basePairList = new ArrayList<BasePair>();
        for ( BasePair basePair : getBasePairs() ) {
            if ( basePair.getCenterLocation().distance( position ) <= distance ) {
                basePairList.add( basePair );
            }
        }
        return basePairList;
    }


    /**
     * Get a range of base pairs to scan for attachment sites given an X
     * position in model space.
     *
     * @param xOffsetOnStrand
     * @return - An integer range representing the indexes of the base pairs
     *         on the DNA strand that match the criteria.
     */
    private IntegerRange getBasePairScanningRange( double xOffsetOnStrand ) {
        int scanningRange = 2; // Pretty arbitrary, can adjust if needed.
        int centerBasePairIndex = getBasePairIndexFromXOffset( xOffsetOnStrand );
        return new IntegerRange( Math.max( 0, centerBasePairIndex - scanningRange ), Math.min( NUMBER_OF_BASE_PAIRS - 1, centerBasePairIndex + scanningRange ) );
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
        return new AttachmentSite( new Point2D.Double( getNearestBasePairXOffset( xOffset ), Y_POS ), 0.05 );
    }

    public AttachmentSite createDefaultAffinityAttachmentSite( int basePairIndex ) {
        return new AttachmentSite( new Point2D.Double( getBasePairXOffsetByIndex( basePairIndex ), Y_POS ), 0.05 );
    }

    /**
     * Get a reference to the gene that contains the given location.
     *
     * @param location
     * @return Gene at the location, null if no gene exists.
     */
    public Gene getGeneAtLocation( Point2D location ) {
        boolean isLocationOnMolecule = location.getX() >= LEFT_EDGE_X_POS && location.getX() <= LEFT_EDGE_X_POS + MOLECULE_LENGTH &&
                                       location.getY() >= Y_POS - STRAND_DIAMETER / 2 && location.getY() <= Y_POS + STRAND_DIAMETER / 2;
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

    // Comparator class to use when comparing two attachment sites.
    private static class AttachmentSiteComparator<T extends AttachmentSite> implements Comparator<T> {
        private final Point2D attachLocation;

        private AttachmentSiteComparator( Point2D attachLocation ) {
            this.attachLocation = attachLocation;
        }

        // Compare the two attachment sites.  The comparison is based on a
        // combination of the affinity and the distance.
        public int compare( T attachmentSite1, T attachmentSite2 ) {
            // The comparison is kind of like comparing gravitational attraction.
            double as1Factor = attachmentSite1.getAffinity() / Math.pow( attachLocation.distance( attachmentSite1.locationProperty.get() ), 2 );
            double as2Factor = attachmentSite2.getAffinity() / Math.pow( attachLocation.distance( attachmentSite2.locationProperty.get() ), 2 );
            return Double.compare( as2Factor, as1Factor );
        }

        public boolean equals( Object obj ) {
            // Stubbed, because it isn't needed.
            return false;
        }
    }
}
