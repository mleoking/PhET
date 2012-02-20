// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.geom.Point2D;

import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;

/**
 * @author John Blanco
 */
public class TranscriptionFactorAttachmentSite extends AttachmentSite {

    public final TranscriptionFactorConfig tfConfig;

    /**
     * Constructor.
     *
     * @param initialLocation
     * @param affinity
     */
    public TranscriptionFactorAttachmentSite( Point2D initialLocation, TranscriptionFactorConfig tfConfig, double affinity ) {
        super( initialLocation, affinity );
        this.tfConfig = tfConfig;
    }
}
