// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.*;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.common.model.TranscriptionFactorPlacementHint;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.TranscriptionFactorAttachmentSite;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;

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

    private final DnaMolecule dnaMolecule;
    private final Color regulatoryRegionColor;
    private final Color transcribedRegionColor;
    private final IntegerRange regulatoryRegion;
    private final IntegerRange transcribedRegion;
    private final AttachmentSite polymeraseAttachmentSite;

    // Each gene has an ID that is used for labeling it in the view and for
    // determining which transcription factors are associated with it.
    public final int identifier;

    // Placement hint for polymerase.  There is always only one.
    private final PlacementHint rnaPolymerasePlacementHint = new PlacementHint( new RnaPolymerase() );

    // Placement hints for transcription factors.
    private final List<TranscriptionFactorPlacementHint> transcriptionFactorPlacementHints = new ArrayList<TranscriptionFactorPlacementHint>();

    // Attachment sites for transcription factors.
    private final List<TranscriptionFactorAttachmentSite> transcriptionFactorAttachmentSites = new ArrayList<TranscriptionFactorAttachmentSite>();

    // Map of transcription factors that interact with this gene to the
    // location, in terms of base pair offset, where the TF attaches.
    private final Map<Integer, TranscriptionFactorConfig> transcriptionFactorMap = new HashMap<Integer, TranscriptionFactorConfig>();

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

        // Create the attachment site for polymerase.  It is always at the end
        // of the regulatory region.
        polymeraseAttachmentSite = new AttachmentSite( new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex(
                regulatoryRegion.getMax() ), DnaMolecule.Y_POS ), identifier );

        // Initialize the placement hint for polymerase.
        rnaPolymerasePlacementHint.setPosition( polymeraseAttachmentSite.locationProperty.get() );
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
        if ( basePairIndex == regulatoryRegion.getMax() ) {
            // This is the last base pair within the regulatory region, which
            // is where the polymerase would begin transcription.
            if ( polymeraseAttachmentSite.attachedOrAttachingMolecule.get() != null || transcriptionFactorsBlockTranscription() ) {
                // Something is blocking attachment, return a low affinity site.
                return new AttachmentSite( polymeraseAttachmentSite.locationProperty.get(), 0 );
            }
            else if ( transcriptionFactorsSupportTranscription() ) {
                // Transcription enabled, return high affinity site.
                polymeraseAttachmentSite.setAffinity( 1 );
                return polymeraseAttachmentSite;
            }
        }

        // There is currently nothing special about this site, so return a
        // default affinity site.
        return dnaMolecule.createDefaultAffinityAttachmentSite( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ) );
    }


    /**
     * Method used by descendant classes to add locations where transcription
     * factors go on the gene.  Generally this is only used during
     * construction.
     *
     * @param basePairOffset - Offset WITHIN THIS GENE where the transcription
     * factor's high affinity site will exist.
     * @param tfConfig
     */
    protected void addTranscriptionFactor( int basePairOffset, TranscriptionFactorConfig tfConfig ){
        transcriptionFactorMap.put( basePairOffset, tfConfig );
        Point2D position = new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex( basePairOffset + regulatoryRegion.getMin() ), DnaMolecule.Y_POS );
        transcriptionFactorPlacementHints.add( new TranscriptionFactorPlacementHint( new TranscriptionFactor( new StubGeneExpressionModel(), tfConfig, position ) ) );
        transcriptionFactorAttachmentSites.add( new TranscriptionFactorAttachmentSite( position, tfConfig, 0.5 ) );
    }

    /**
     * Returns true if all positive transcription factors are attached and
     * no negative ones are attached, which indicates that transcription is
     * essentially enabled.
     */
    private boolean transcriptionFactorsSupportTranscription() {

        // In this sim, blocking factors overrule positive factors, so test for
        // those first.
        if ( transcriptionFactorsBlockTranscription() ) {
            return false;
        }

        // Count the number of positive transcription factors needed to enable
        // transcription.
        int numPositiveTranscriptionFactorsNeeded = 0;
        for ( TranscriptionFactorConfig transcriptionFactorConfig : transcriptionFactorMap.values() ) {
            if ( transcriptionFactorConfig.isPositive ) {
                numPositiveTranscriptionFactorsNeeded++;
            }
        }

        // Count the number of positive transcription factors attached.
        int numPositiveTranscriptionFactorsAttached = 0;
        for ( AttachmentSite transcriptionFactorAttachmentSite : transcriptionFactorAttachmentSites ) {
            if ( transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get() != null ) {
                if ( ( (TranscriptionFactor) transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get() ).isPositive() ) {
                    numPositiveTranscriptionFactorsAttached++;
                }
            }
        }

        return numPositiveTranscriptionFactorsAttached == numPositiveTranscriptionFactorsNeeded;
    }

    /**
     * Evaluate if transcription factors are blocking transcription.
     *
     * @return true if there are transcription factors that block transcription.
     */
    private boolean transcriptionFactorsBlockTranscription() {
        for ( AttachmentSite transcriptionFactorAttachmentSite : transcriptionFactorAttachmentSites ) {
            if ( transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get() != null ) {
                if ( !( (TranscriptionFactor) transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get() ).isPositive() ) {
                    return true;
                }
            }
        }
        return false;
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
    public AttachmentSite getTranscriptionFactorAttachmentSite( int basePairIndex, TranscriptionFactorConfig tfConfig ) {

        // Assume a default affinity site until proven otherwise.
        AttachmentSite attachmentSite = dnaMolecule.createDefaultAffinityAttachmentSite( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ) );

        // Determine whether there are any transcription factor attachment
        // sites on this gene that match the specified configuration.
        for ( TranscriptionFactorAttachmentSite transcriptionFactorAttachmentSite : transcriptionFactorAttachmentSites ) {
            if ( transcriptionFactorAttachmentSite.configurationMatches( tfConfig ) ) {
                // Found matching site.  Is it available and in the right place?
                if ( transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get() == null &&
                     Math.abs( transcriptionFactorAttachmentSite.locationProperty.get().getX() - dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ) ) < DnaMolecule.DISTANCE_BETWEEN_BASE_PAIRS / 2 ) {
                    {
                        // Yes, so this is the site where the given TF should go.
                        attachmentSite = transcriptionFactorAttachmentSite;
                        break;
                    }
                }
            }
        }

        return attachmentSite;
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

            if ( !transcriptionFactorsBlockTranscription() ) {
                // Activate the polymerase hint.
                rnaPolymerasePlacementHint.active.set( true );

                // Also activate any unoccupied positive transcription factor
                // hints in order to convey to the user that these are needed
                // for transcription to start.
                for ( TranscriptionFactorAttachmentSite transcriptionFactorAttachmentSite : transcriptionFactorAttachmentSites ) {
                    if ( transcriptionFactorAttachmentSite.attachedOrAttachingMolecule == null && transcriptionFactorAttachmentSite.getTfConfig().isPositive ) {
                        activateTranscriptionFactorHint( transcriptionFactorAttachmentSite.getTfConfig() );
                    }
                }
            }
        }
        else if ( biomolecule instanceof TranscriptionFactor ) {
            // Activate hint that matches this transcription factor.
            for ( TranscriptionFactorPlacementHint transcriptionFactorPlacementHint : transcriptionFactorPlacementHints ) {
                transcriptionFactorPlacementHint.activateIfMatch( biomolecule );
            }
        }
    }

    private void activateTranscriptionFactorHint( TranscriptionFactorConfig tfConfig ) {

        //To change body of created methods use File | Settings | File Templates.
    }

    public void deactivateHints() {
        rnaPolymerasePlacementHint.active.set( false );
        for ( TranscriptionFactorPlacementHint transcriptionFactorPlacementHint : transcriptionFactorPlacementHints ) {
            transcriptionFactorPlacementHint.active.set( false );
        }
    }

    public List<PlacementHint> getPlacementHints() {
        return new ArrayList<PlacementHint>() {{
            add( rnaPolymerasePlacementHint );
            for ( TranscriptionFactorPlacementHint transcriptionFactorPlacementHint : transcriptionFactorPlacementHints ) {
                add( transcriptionFactorPlacementHint );
            }
        }};
    }

    /**
     * Clear the attachment sites, generally only done as part of a reset
     * operation.
     */
    public void clearAttachmentSites() {
        polymeraseAttachmentSite.attachedOrAttachingMolecule.set( null );
        for ( TranscriptionFactorAttachmentSite transcriptionFactorAttachmentSite : transcriptionFactorAttachmentSites ) {
            transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.set( null );
        }
    }

    /**
     * Get an instance (a.k.a. a prototype) of the protein associated with this
     * gene.
     *
     * @return
     */
    public abstract Protein getProteinPrototype();
}
