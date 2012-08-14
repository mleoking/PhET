// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.geneexpressionbasics.common.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.common.model.Ribosome;

/**
 * This class defines a very specific motion strategy used by a ribosome to
 * follow the translation attachment point of a strand of mRNA.
 *
 * @author John Blanco
 */
public class RibosomeTranslatingRnaMotionStrategy extends MotionStrategy {
    private final MessengerRna messengerRna;
    private final Ribosome ribosome;

    public RibosomeTranslatingRnaMotionStrategy( Ribosome ribosome ) {
        this.ribosome = ribosome;
        this.messengerRna = ribosome.getMessengerRnaBeingTranslated();
    }

    @Override public Vector2D getNextLocation( Vector2D currentLocation, Shape shape, double dt ) {
        Vector2D ribosomeAttachmentPoint = messengerRna.getRibosomeAttachmentLocation( ribosome );
        return ribosomeAttachmentPoint.minus( Ribosome.OFFSET_TO_TRANSLATION_CHANNEL_ENTRANCE );
    }
}
