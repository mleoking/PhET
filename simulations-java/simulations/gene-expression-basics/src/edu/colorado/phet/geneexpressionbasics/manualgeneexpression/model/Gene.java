// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;

/**
 * This class is the model representation of a gene on a DNA molecule.  The
 * consists of a regulatory region and a transcribed region, and it keeps track
 * of where the transcription factors and polymerase attaches, and how strong
 * the affinities are for attachment.  In real life, a gene is just a
 * collection of base pairs on the DNA strand.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public abstract class Gene {

    // Offset from the first base pair in the regulatory region of the gene
    // where the high-affinity site for the transcription factor lives.
    private static final int TRANSCRIPTION_FACTOR_LOCATION_OFFSET = 6;

    private final DnaMolecule dnaMolecule;
    private final Color regulatoryRegionColor;
    private final Color transcribedRegionColor;
    private final IntegerRange regulatoryRegion;
    private final IntegerRange transcribedRegion;
    private final AttachmentSite polymeraseAttachmentSite;
    private final AttachmentSite transcriptionFactorAttachmentSite;

    // Each gene has an ID that is used for labeling it in the view and for
    // determining which transcription factors are associated with it.
    public final int identifier;

    // Placement hints associated with this gene.
    private final PlacementHint rnaPolymerasePlacementHint;
    private final PlacementHint positiveTranscriptionFactorPlacementHint;
    private final PlacementHint negativeTranscriptionFactorPlacementHint;

    /**
     * Constructor.
     *
     * @param dnaMolecule            The DNA molecule within which this gene
     *                               exists.
     * @param regulatoryRegion       The range, in terms of base pairs on the
     *                               DNA strand, where this region exists.
     * @param regulatoryRegionColor
     * @param transcribedRegion      The range, in terms of base pairs on the
     *                               DNA strand, where this region exists.
     * @param transcribedRegionColor
     */
    protected Gene( DnaMolecule dnaMolecule, IntegerRange regulatoryRegion, Color regulatoryRegionColor,
                    IntegerRange transcribedRegion, Color transcribedRegionColor, int identifier ) {
        this.dnaMolecule = dnaMolecule;
        this.regulatoryRegion = regulatoryRegion;
        this.regulatoryRegionColor = regulatoryRegionColor;
        this.transcribedRegion = transcribedRegion;
        this.transcribedRegionColor = transcribedRegionColor;
        this.identifier = identifier;

        // Create the attachment sites for polymerase and transcription factors.
        polymeraseAttachmentSite = new AttachmentSite( new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex(
                regulatoryRegion.getMax() ), DnaMolecule.Y_POS ), identifier );
        transcriptionFactorAttachmentSite = new AttachmentSite(
                new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex( regulatoryRegion.getMin() + TRANSCRIPTION_FACTOR_LOCATION_OFFSET ),
                                    DnaMolecule.Y_POS ), 1 );

        // Initialize the placement hints.
        rnaPolymerasePlacementHint = new PlacementHint( new RnaPolymerase() );
        rnaPolymerasePlacementHint.setPosition( polymeraseAttachmentSite.locationProperty.get() );
        positiveTranscriptionFactorPlacementHint = new PlacementHint( TranscriptionFactor.generateTranscriptionFactor(
                new StubGeneExpressionModel(), identifier, true, transcriptionFactorAttachmentSite.locationProperty.get() ) );
        negativeTranscriptionFactorPlacementHint = new PlacementHint( TranscriptionFactor.generateTranscriptionFactor(
                new StubGeneExpressionModel(), identifier, false, transcriptionFactorAttachmentSite.locationProperty.get() ) );
    }

    public Color getRegulatoryRegionColor() {
        return regulatoryRegionColor;
    }

    public Color getTranscribedRegionColor() {
        return transcribedRegionColor;
    }

    public double getCenterX() {
        return getStartX() + ( getEndX() - getStartX() ) / 2;
    }

    public double getStartX() {
        return dnaMolecule.getBasePairXOffsetByIndex( regulatoryRegion.getMin() );
    }

    public double getEndX() {
        return dnaMolecule.getBasePairXOffsetByIndex( transcribedRegion.getMax() );
    }

    public IntegerRange getRegulatoryRegion() {
        return regulatoryRegion;
    }

    public IntegerRange getTranscribedRegion() {
        return transcribedRegion;
    }

    public double getTranscribedRegionLength() {
        return transcribedRegion.getLength() * DnaMolecule.DISTANCE_BETWEEN_BASE_PAIRS;
    }

    /**
     * Get the attachment site for a location that is contained within this
     * gene.  In many cases, the affinity of the attachment site will be the
     * same as the default for any DNA, but in some cases it may be especially
     * strong.
     *
     * @param basePairIndex - Index of the base pair on the DNA strand, NOT the
     *                      index within this gene.  In the real world,
     *                      affinities are associated with sets of base pairs
     *                      rather than an individual one, so this is a bit of a
     *                      simplification.
     * @return
     */
    public AttachmentSite getPolymeraseAttachmentSite( int basePairIndex ) {
        if ( basePairIndex == regulatoryRegion.getMax() && transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get().isSome() ) {
            // This is the last base pair within the regulatory region, which
            // is where the polymerase would begin transcription, and the
            // transcription factor is attached to the appropriate place.
            if ( polymeraseAttachmentSite.attachedOrAttachingMolecule.get().isSome() ) {
                // Already in use, return a zero affinity attachment site.
                return new AttachmentSite( transcriptionFactorAttachmentSite.locationProperty.get(), 0 );
            }
            else {
                TranscriptionFactor attachedTranscriptionFactor = (TranscriptionFactor) transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get().get();
                if ( attachedTranscriptionFactor.isPositive() ) {
                    // The positive transcription factor is attached, so we
                    // set the attachment site to the max affinity.
                    polymeraseAttachmentSite.setAffinity( 1 );
                }
                else {
                    // It must be a negative transcription factor, so the
                    // affinity should be very low.
                    polymeraseAttachmentSite.setAffinity( 0 );
                }
                return polymeraseAttachmentSite;
            }
        }
        else {
            // Not a special location as far as this biomolecule is concerned,
            // so return the default affinity.
            return dnaMolecule.createDefaultAffinityAttachmentSite( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ) );
        }
    }

    /**
     * Get the attachment site for a location that is contained within this
     * gene.  In many cases, the affinity of the attachment site will be the
     * same as the default for any DNA, but in some cases it may be especially
     * strong.
     *
     * @param basePairIndex - Index of the base pair on the DNA strand, NOT the
     *                      index within this gene.  In the real world,
     *                      affinities are associated with sets of base pairs
     *                      rather than an individual one, so this is a bit of a
     *                      simplification.
     * @return
     */
    public AttachmentSite getTranscriptionFactorAttachmentSite( int basePairIndex ) {
        if ( basePairIndex == regulatoryRegion.getMin() + TRANSCRIPTION_FACTOR_LOCATION_OFFSET ) {
            if ( transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get().isNone() ) {
                // The attachment site is open.
                return transcriptionFactorAttachmentSite;
            }
            else {
                // This attachment site is in use, so we return one with an
                // affinity of 0 so that another transcription factor won't
                // attach to it.
                return new AttachmentSite( new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ), dnaMolecule.Y_POS ), 0 );
            }
        }
        else {
            // Not a special location as far as this biomolecule is concerned,
            // so return an attachment site with the default affinity.
            return dnaMolecule.createDefaultAffinityAttachmentSite( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ) );
        }
    }

    public boolean containsBasePair( int basePairIndex ) {
        return regulatoryRegion.contains( basePairIndex ) || transcribedRegion.contains( basePairIndex );
    }

    /**
     * Activate any and all placement hints associated with the given
     * biomolecule.
     *
     * @param biomolecule
     */
    public void activateHints( MobileBiomolecule biomolecule ) {
        if ( rnaPolymerasePlacementHint.isMatchingBiomolecule( biomolecule ) ) {
            if ( transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get().isNone() ) {
                // Activate both the polymerase hint AND the positive
                // transcription factor hint in order to convey to the user
                // that both are needed for transcription to start.
                rnaPolymerasePlacementHint.active.set( true );
                positiveTranscriptionFactorPlacementHint.active.set( true );
            }
            else if ( ( (TranscriptionFactor) ( transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get().get() ) ).isPositive() ) {
                // The positive transcription factor is already in place, so
                // only activate the polymerase hint.
                rnaPolymerasePlacementHint.active.set( true );
            }
            // Note that if the negative transcription factor is in place, the
            // polymerase hint is not activated.
        }
        else if ( positiveTranscriptionFactorPlacementHint.isMatchingBiomolecule( biomolecule ) ) {
            positiveTranscriptionFactorPlacementHint.active.set( true );
        }
        else if ( negativeTranscriptionFactorPlacementHint.isMatchingBiomolecule( biomolecule ) ) {
            negativeTranscriptionFactorPlacementHint.active.set( true );
        }
    }

    public void deactivateHints() {
        rnaPolymerasePlacementHint.active.set( false );
        positiveTranscriptionFactorPlacementHint.active.set( false );
        negativeTranscriptionFactorPlacementHint.active.set( false );
    }

    public List<PlacementHint> getPlacementHints() {
        return new ArrayList<PlacementHint>() {{
            add( rnaPolymerasePlacementHint );
            add( positiveTranscriptionFactorPlacementHint );
            add( negativeTranscriptionFactorPlacementHint );
        }};
    }

    /**
     * Clear the attachment sites, generally only done as part of a reset
     * operation.
     */
    public void clearAttachmentSites() {
        transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.set( new Option.None<MobileBiomolecule>() );
        polymeraseAttachmentSite.attachedOrAttachingMolecule.set( new Option.None<MobileBiomolecule>() );
    }

    public Protein getProteinPrototype() {
        // TODO: This is lame and should be handled in subclasses.
        switch( identifier ) {
            case 1:
                return new ProteinA();
            case 2:
                return new ProteinB();
            case 3:
                return new ProteinC();
            default:
                assert false;
                return null;
        }
    }
}
