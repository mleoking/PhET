// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.geom.Point2D;

import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;

/**
 * @author John Blanco
 */
public class TranscriptionFactorAttachmentSite extends AttachmentSite {

    private final TranscriptionFactorConfig tfConfig;

    /**
     * Constructor.
     *
     * @param initialLocation
     * @param affinity
     */
    public TranscriptionFactorAttachmentSite( Point2D initialLocation, TranscriptionFactorConfig tfConfig, double affinity ) {
        super( initialLocation );
        this.tfConfig = tfConfig;
    }

    public boolean configurationMatches( TranscriptionFactorConfig tfConfig ){
        return this.tfConfig.equals( tfConfig );
    }

    public TranscriptionFactorConfig getTfConfig() {
        return tfConfig;
    }
}
