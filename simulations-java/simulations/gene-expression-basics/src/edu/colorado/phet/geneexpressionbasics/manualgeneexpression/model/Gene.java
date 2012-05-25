// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.common.model.TranscriptionFactorAttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.TranscriptionFactorPlacementHint;
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

    // Placement hint for polymerase.  There is always only one.
    private final PlacementHint rnaPolymerasePlacementHint = new PlacementHint( new RnaPolymerase() );

    // Placement hints for transcription factors.
    private final List<TranscriptionFactorPlacementHint> transcriptionFactorPlacementHints = new ArrayList<TranscriptionFactorPlacementHint>();

    // Attachment sites for transcription factors.
    private final List<TranscriptionFactorAttachmentSite> transcriptionFactorAttachmentSites = new ArrayList<TranscriptionFactorAttachmentSite>();

    // Map of transcription factors that interact with this gene to the
    // location, in terms of base pair offset, where the TF attaches.
    private final Map<Integer, TranscriptionFactor> transcriptionFactorMap = new HashMap<Integer, TranscriptionFactor>();

    // Property that determines the affinity of the site where polymerase
    // attaches when the transcription factors support transcription.
    private final Property<Double> polymeraseAffinityProperty = new Property<Double>( 1.0 );

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
    protected Gene( edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule dnaMolecule, edu.colorado.phet.common.phetcommon.util.IntegerRange regulatoryRegion, java.awt.Color regulatoryRegionColor,
                    edu.colorado.phet.common.phetcommon.util.IntegerRange transcribedRegion, java.awt.Color transcribedRegionColor ) {
        this.dnaMolecule = dnaMolecule;
        this.regulatoryRegion = regulatoryRegion;
        this.regulatoryRegionColor = regulatoryRegionColor;
        this.transcribedRegion = transcribedRegion;
        this.transcribedRegionColor = transcribedRegionColor;

        // Create the attachment site for polymerase.  It is always at the end
        // of the regulatory region.
        polymeraseAttachmentSite = new AttachmentSite( new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex(
                regulatoryRegion.getMax() ), DnaMolecule.Y_POS ), 1 );

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
            return polymeraseAttachmentSite;
        }

        // There is currently nothing special about this site, so return a
        // default affinity site.
        return dnaMolecule.createDefaultAffinityAttachmentSite( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ) );
    }

    /**
     * Get the attachment site where RNA polymerase would start transcribing
     * the DNA.  This is assumes that there is only one such site on the
     * gene.
     *
     * @return
     */
    public AttachmentSite getPolymeraseAttachmentSite() {
        return polymeraseAttachmentSite;
    }

    public void updateAffinities() {
        // Update the affinity of the polymerase attachment site based upon the
        // state of the transcription factors.
        if ( transcriptionFactorsSupportTranscription() ) {
            polymeraseAttachmentSite.affinityProperty.set( polymeraseAffinityProperty.get() );
        }
        else {
            polymeraseAttachmentSite.affinityProperty.set( DnaMolecule.DEFAULT_AFFINITY );
        }
    }

    /**
     * Method used by descendant classes to add locations where transcription
     * factors go on the gene.  Generally this is only used during
     * construction.
     *
     * @param basePairOffset - Offset WITHIN THIS GENE where the transcription
     *                       factor's high affinity site will exist.
     * @param tfConfig
     */
    protected void addTranscriptionFactor( int basePairOffset, TranscriptionFactorConfig tfConfig ) {
        transcriptionFactorMap.put( basePairOffset, new TranscriptionFactor( tfConfig ) );
        Point2D position = new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex( basePairOffset + regulatoryRegion.getMin() ), DnaMolecule.Y_POS );
        transcriptionFactorPlacementHints.add( new TranscriptionFactorPlacementHint( new TranscriptionFactor( new StubGeneExpressionModel(), tfConfig, position ) ) );
        transcriptionFactorAttachmentSites.add( new TranscriptionFactorAttachmentSite( position, tfConfig, 1 ) );
    }

    /**
     * Returns true if all positive transcription factors are attached and
     * no negative ones are attached, which indicates that transcription is
     * essentially enabled.
     */
    public boolean transcriptionFactorsSupportTranscription() {

        // In this sim, blocking factors overrule positive factors, so test for
        // those first.
        if ( transcriptionFactorsBlockTranscription() ) {
            return false;
        }

        // Count the number of positive transcription factors needed to enable
        // transcription.
        int numPositiveTranscriptionFactorsNeeded = 0;
        for ( TranscriptionFactor transcriptionFactor : transcriptionFactorMap.values() ) {
            if ( transcriptionFactor.getConfig().isPositive ) {
                numPositiveTranscriptionFactorsNeeded++;
            }
        }

        // Count the number of positive transcription factors attached.
        int numPositiveTranscriptionFactorsAttached = 0;
        for ( AttachmentSite transcriptionFactorAttachmentSite : transcriptionFactorAttachmentSites ) {
            if ( transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get() != null ) {
                TranscriptionFactor tf = (TranscriptionFactor) transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get();
                if ( tf.getPosition().distance( transcriptionFactorAttachmentSite.locationProperty.get() ) == 0 && tf.isPositive() ) {
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
     * same as the default for any base pair on the DNA, but if the specified
     * base pair matches the location of the high-affinity site for this
     * transcription factory, it will generally be higher than the default.
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
                    // Yes, so this is the site where the given TF should go.
                    attachmentSite = transcriptionFactorAttachmentSite;
                    break;
                }
            }
        }

        return attachmentSite;
    }

    /**
     * Get the attachment site that is specific to the given transcription
     * factor configuration, if one exists.
     * <p/>
     * NOTE: This assumes a max of one site per TF config.  This will need to
     * change if multiple identical configs are supported on a single gene.
     *
     * @param transcriptionFactorConfig
     * @return attachment site for the config if present on the gene, null if not.
     */
    public AttachmentSite getMatchingSite( TranscriptionFactorConfig transcriptionFactorConfig ) {
        for ( TranscriptionFactorAttachmentSite transcriptionFactorAttachmentSite : transcriptionFactorAttachmentSites ) {
            if ( transcriptionFactorAttachmentSite.configurationMatches( transcriptionFactorConfig ) ) {
                return transcriptionFactorAttachmentSite;
            }
        }
        return null;
    }

    /**
     * Get a property that can be used to vary the affinity of the attachment
     * site associated with the specified transcription factor.
     *
     * @param tfConfig
     * @return
     */
    public Property<Double> getTranscriptionFactorAffinityProperty( TranscriptionFactorConfig tfConfig ) {
        Property<Double> affinityProperty = null;
        for ( TranscriptionFactorAttachmentSite transcriptionFactorAttachmentSite : transcriptionFactorAttachmentSites ) {
            if ( transcriptionFactorAttachmentSite.configurationMatches( tfConfig ) ) {
                affinityProperty = transcriptionFactorAttachmentSite.affinityProperty;
                // Built-in assumption here: Only one site for given TF config.
                break;
            }
        }
        return affinityProperty;
    }

    /**
     * Get the property that controls the affinity of the site where polymerase
     * binds when initiating transcription.
     *
     * @return
     */
    public Property<Double> getPolymeraseAffinityProperty() {
        return polymeraseAffinityProperty;
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
                    if ( transcriptionFactorAttachmentSite.attachedOrAttachingMolecule.get() == null && transcriptionFactorAttachmentSite.getTfConfig().isPositive ) {
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
        for ( TranscriptionFactorPlacementHint transcriptionFactorPlacementHint : transcriptionFactorPlacementHints ) {
            transcriptionFactorPlacementHint.activateIfConfigMatch( tfConfig );
        }
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

    /**
     * Get the list of all transcription factors that have high-affinity
     * binding sites on this gene.
     *
     * @return
     */
    public List<TranscriptionFactorConfig> getTranscriptionFactorConfigs() {
        List<TranscriptionFactorConfig> configList = new ArrayList<TranscriptionFactorConfig>();
        for ( TranscriptionFactor transcriptionFactor : transcriptionFactorMap.values() ) {
            configList.add( transcriptionFactor.getConfig() );
        }
        return configList;
    }
}
