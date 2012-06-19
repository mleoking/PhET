// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;

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

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        Point2D ribosomeAttachmentPoint = messengerRna.getRibosomeAttachmentLocation( ribosome );
        return new Point2D.Double( ribosomeAttachmentPoint.getX() - Ribosome.OFFSET_TO_TRANSLATION_CHANNEL_ENTRANCE.getX(),
                                   ribosomeAttachmentPoint.getY() - Ribosome.OFFSET_TO_TRANSLATION_CHANNEL_ENTRANCE.getY() );
    }
}
