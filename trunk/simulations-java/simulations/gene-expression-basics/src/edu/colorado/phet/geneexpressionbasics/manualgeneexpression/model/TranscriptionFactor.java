// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.TranscriptionFactorAttachmentStateMachine;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a transcription factor in the model.  There are
 * multiple transcription factors, and some are positive (in the sense that
 * they increase the likelihood of transcription) and some are negative (i.e.
 * the reduce the likelihood of transcription).
 *
 * @author John Blanco
 */
public class TranscriptionFactor extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 350;   // In nanometers.
    private static final double HEIGHT = 240;  // In nanometers.
    private static final Dimension2D SIZE = new PDimension( WIDTH, HEIGHT );

    /**
     * This data structure contains the configuration information for all of the
     * transcription factors used in this sim.
     */
    private static final List<TranscriptionFactorConfig> positiveTranscriptionFactorInfo = new ArrayList<TranscriptionFactorConfig>() {{
        add( new TranscriptionFactorConfig( 1, BioShapeUtils.createRandomShape( SIZE, 1000 ), true, Color.yellow ) );
        add( new TranscriptionFactorConfig( 1, BioShapeUtils.createRandomShape( SIZE, 2000 ), false, Color.red ) );
        add( new TranscriptionFactorConfig( 2, BioShapeUtils.createRandomShape( SIZE, 3000 ), true, Color.green ) );
        add( new TranscriptionFactorConfig( 2, BioShapeUtils.createRandomShape( SIZE, 4000 ), false, Color.pink ) );
        add( new TranscriptionFactorConfig( 3, BioShapeUtils.createRandomShape( SIZE, 57 ), true, new Color( 255, 127, 0 ) ) );
        add( new TranscriptionFactorConfig( 3, BioShapeUtils.createRandomShape( SIZE, 40 ), false, Color.magenta ) );
        // Default config used if no match found.
        add( new TranscriptionFactorConfig( Integer.MAX_VALUE, BioShapeUtils.createRandomShape( SIZE, 123 ), false, Color.magenta ) );
    }};

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Flag that determines whether this transcription factor is positive or
    // negative, i.e. whether it enhances or diminishes the likelihood of
    // transcription.
    private final boolean isPositive;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    private TranscriptionFactor( GeneExpressionModel model, Shape shape, Point2D initialPosition, Color baseColor, boolean isPositive ) {
        super( model, shape, baseColor );
        this.isPositive = isPositive;
        setPosition( initialPosition );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get an indication of whether this transcription factor is positive
     * (enhances transcription) or negative (prevents or decreases likelihood of
     * transcription).
     *
     * @return
     */
    public boolean isPositive() {
        return isPositive;
    }

    @Override public void stepInTime( double dt ) {
        super.stepInTime( dt );
    }

    // Overridden in order to provide some unique behavior for transcription
    // factors.
    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new TranscriptionFactorAttachmentStateMachine( this );
    }

    /**
     * Static factory method that generates an instance of a transcription
     * factor for the specified gene and of the specified polarity (i.e.
     * positive or negative).
     *
     * @param geneID
     * @param positive
     * @param initialPosition
     * @return
     */
    public static TranscriptionFactor generateTranscriptionFactor( GeneExpressionModel model, int geneID, boolean positive, Point2D initialPosition ) {
        TranscriptionFactorConfig config = null;
        for ( TranscriptionFactorConfig tfc : positiveTranscriptionFactorInfo ) {
            if ( tfc.geneID == geneID && tfc.isPositive == positive ) {
                // Matching configuration found.
                config = tfc;
                break;
            }
        }
        // Make it clear that there is a problem if no configuration is found.
        assert config != null;
        if ( config == null ) {
            System.out.println( "Error: No transcription factor information for specified parameters, using default." );
            config = positiveTranscriptionFactorInfo.get( positiveTranscriptionFactorInfo.size() - 1 );
        }
        // Create the transcription factor instance.
        return new TranscriptionFactor( model, config.shape, initialPosition, config.baseColor, positive );
    }

    @Override public AttachmentSite proposeAttachments() {
        // Transcription factors only attach to DNA.
        return model.getDnaMolecule().considerProposalFrom( this );
    }

    @Override public boolean isAttachedToDna() {
        // Since DNA is the only thing that this attaches to, if it is
        // attached to anything, it is DNA.
        return attachmentStateMachine.isAttachedOrAttaching();
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    /**
     * Convenience class for keeping together the information needed to specify
     * a transcription factor.
     */
    private static class TranscriptionFactorConfig {
        protected final Shape shape;
        protected final Color baseColor;
        protected final int geneID;
        protected final boolean isPositive;

        private TranscriptionFactorConfig( int geneID, Shape shape, boolean positive, Color baseColor ) {
            this.shape = shape;
            this.baseColor = baseColor;
            this.geneID = geneID;
            isPositive = positive;
        }
    }
}
