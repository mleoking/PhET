// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;

/**
 * Specialization of the attachment site for transcription factors -
 * associates a transcription factor configuration and a property with the
 * attachment site.
 *
 * NOTE TO SELF (or other future developers): If any other attachment site is
 * needed that has variable affinity, the class hierarchy should be changed to
 * make this more general.
 *
 * @author John Blanco
 */
public class TranscriptionFactorAttachmentSite extends AttachmentSite {

    // Configuration of TF that attaches to this site.
    private final TranscriptionFactorConfig tfConfig;

    // Property that can be used to change the affinity of the attachment site.
    public final Property<Double> affinityProperty;

    /**
     * Constructor.
     *
     * @param initialLocation
     * @param initialAffinity
     */
    public TranscriptionFactorAttachmentSite( Point2D initialLocation, TranscriptionFactorConfig tfConfig, double initialAffinity ) {
        super( initialLocation, initialAffinity );
        this.tfConfig = tfConfig;
        affinityProperty = new Property<Double>( initialAffinity );
        // Link the property to the affinity in the base class.
        affinityProperty.addObserver( new VoidFunction1<Double>() {
            public void apply( Double affinity ) {
                TranscriptionFactorAttachmentSite.this.affinity = affinity;
            }
        } );
    }

    @Override public void setAffinity( double affinity ) {
        affinityProperty.set( affinity );
    }

    public boolean configurationMatches( TranscriptionFactorConfig tfConfig ){
        return this.tfConfig.equals( tfConfig );
    }

    public TranscriptionFactorConfig getTfConfig() {
        return tfConfig;
    }
}
