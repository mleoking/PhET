// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.ShapeCreationUtils;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a transcription factor in the model.  There are
 * multiple transcription factors, and some are positive (in the sense that they
 * increase the likelihood of transcription) and some are negative (i.e. the
 * reduce the likelihood of transcription).
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
        add( new TranscriptionFactorConfig( 0, ShapeCreationUtils.createRandomShape( SIZE, 1000 ), true, Color.yellow ) );
        add( new TranscriptionFactorConfig( 0, ShapeCreationUtils.createRandomShape( SIZE, 2000 ), false, Color.red ) );
        add( new TranscriptionFactorConfig( 1, ShapeCreationUtils.createRandomShape( SIZE, 3000 ), true, Color.green ) );
        add( new TranscriptionFactorConfig( 1, ShapeCreationUtils.createRandomShape( SIZE, 4000 ), false, Color.pink ) );
        add( new TranscriptionFactorConfig( 2, ShapeCreationUtils.createRandomShape( SIZE, 57 ), true, new Color( 255, 127, 0 ) ) );
        add( new TranscriptionFactorConfig( 2, ShapeCreationUtils.createRandomShape( SIZE, 40 ), false, Color.magenta ) );
        // Default config used if no match found.
        add( new TranscriptionFactorConfig( Integer.MAX_VALUE, ShapeCreationUtils.createRandomShape( SIZE, 123 ), false, Color.magenta ) );
    }};

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final boolean isPositive;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    private TranscriptionFactor( Shape shape, Point2D initialPosition, Color baseColor, boolean isPositive ) {
        super( shape, baseColor );
        this.isPositive = isPositive;
        setPosition( initialPosition );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get an indication of whether this transcription factor is positive
     * (enhances transcription) or negative (prevents or decreases
     * transcription).
     *
     * @return
     */
    public boolean isPositive() {
        return isPositive;
    }

    /**
     * Static factor method that generates an instance of a transcription factor
     * for the specified gene and of the specified polarity (i.e. positive or
     * negative).
     *
     * @param geneNumber
     * @param positive
     * @param initialPosition
     * @return
     */
    public static TranscriptionFactor generateTranscriptionFactor( int geneNumber, boolean positive, Point2D initialPosition ) {
        TranscriptionFactorConfig config = null;
        for ( TranscriptionFactorConfig tfc : positiveTranscriptionFactorInfo ) {
            if ( tfc.geneID == geneNumber && tfc.isPositive == positive ) {
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
        return new TranscriptionFactor( config.shape, initialPosition, config.baseColor, positive );
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
