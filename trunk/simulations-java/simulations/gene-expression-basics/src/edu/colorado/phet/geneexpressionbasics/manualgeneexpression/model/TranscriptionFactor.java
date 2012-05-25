// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
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

    private static final double WIDTH = 325;   // In nanometers.
    private static final double HEIGHT = 240;  // In nanometers.
    private static final Dimension2D SIZE = new PDimension( WIDTH, HEIGHT );

    // Static definitions of all the transcription factor configurations that
    // are used by this sim.
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_1_POS = new TranscriptionFactorConfig( BioShapeUtils.createRandomShape( SIZE, 1000 ), true, Color.yellow );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_1_NEG = new TranscriptionFactorConfig( BioShapeUtils.createRandomShape( SIZE, 2000 ), false, Color.red );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_2_POS_1 = new TranscriptionFactorConfig( BioShapeUtils.createRandomShape( SIZE, 3001 ), true, Color.orange );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_2_POS_2 = new TranscriptionFactorConfig( BioShapeUtils.createRandomShape( SIZE, 125 ), true, new Color( 0, 255, 127 ) );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_2_NEG = new TranscriptionFactorConfig( BioShapeUtils.createRandomShape( SIZE, 4000 ), false, new Color( 255, 255, 255 ) );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_3_POS_1 = new TranscriptionFactorConfig( BioShapeUtils.createRandomShape( SIZE, 57 ), true, new Color( 255, 127, 0 ) );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_3_POS_2 = new TranscriptionFactorConfig( BioShapeUtils.createRandomShape( SIZE, 88 ), true, new Color( 255, 99, 71 ) );
    public static final TranscriptionFactorConfig TRANSCRIPTION_FACTOR_CONFIG_GENE_3_NEG = new TranscriptionFactorConfig( BioShapeUtils.createRandomShape( SIZE, 40 ), false, Color.magenta );

    // Random number generator.
    private static final Random RAND = new Random( System.currentTimeMillis() - 8 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Configuration used to create this transcription factor, used when
    // comparing factors and creating copies.
    private final TranscriptionFactorConfig config;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor for a TF that doesn't need to interact with a real model.
     *
     * @param config
     */
    public TranscriptionFactor( TranscriptionFactorConfig config ) {
        this( new StubGeneExpressionModel(), config );
    }

    /**
     * Constructor with default position.
     *
     * @param model
     * @param config
     */
    public TranscriptionFactor( GeneExpressionModel model, TranscriptionFactorConfig config ) {
        this( model, config, new Point2D.Double( 0, 0 ) );
    }

    /**
     * Primary constructor.
     *
     * @param model
     * @param config
     * @param initialPosition
     */
    public TranscriptionFactor( GeneExpressionModel model, TranscriptionFactorConfig config, Point2D initialPosition ) {
        super( model, config.shape, config.baseColor );
        this.config = config;
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
        return config.isPositive;
    }

    // Overridden in order to provide some unique behavior for transcription
    // factors.
    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new TranscriptionFactorAttachmentStateMachine( this );
    }

    @Override protected void handleReleasedByUser() {
        super.handleReleasedByUser();

        // A case that is unique to transcription factors: If released on top
        // of another biomolecule on the DNA strand, go directly into the
        // detaching state so that this drifts away from the DNA.  This makes it
        // clear the you can't have two transcription factors in the same
        // place on the DNA.
        for ( MobileBiomolecule biomolecule : model.getOverlappingBiomolecules( this.getShape() ) ) {
            if ( biomolecule != this && biomolecule.attachedToDna.get() ) {
                attachmentStateMachine.forceImmediateUnattachedButUnavailable();
                break;
            }
        }
    }

    @Override public ImmutableVector2D getDetachDirection() {
        // Randomly either up or down when detaching from DNA.
        return RAND.nextBoolean() ? new ImmutableVector2D( 0, 1 ) : new ImmutableVector2D( 0, -1 );
    }

    @Override public AttachmentSite proposeAttachments() {
        // Transcription factors only attach to DNA.
        return model.getDnaMolecule().considerProposalFrom( this );
    }

    public TranscriptionFactorConfig getConfig() {
        return config;
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    /**
     * Class the defines the shape, color, polarity, etc. of a transcription
     * factor.
     */
    public static class TranscriptionFactorConfig {

        public final Shape shape;
        public final Color baseColor;
        public final boolean isPositive;

        TranscriptionFactorConfig( Shape shape, boolean positive, Color baseColor ) {
            this.shape = shape;
            this.baseColor = baseColor;
            isPositive = positive;
        }
    }
}
