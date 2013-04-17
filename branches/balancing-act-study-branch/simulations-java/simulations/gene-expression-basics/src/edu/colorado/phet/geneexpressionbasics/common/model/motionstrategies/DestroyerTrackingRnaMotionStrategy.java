// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.geneexpressionbasics.common.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.common.model.MessengerRnaDestroyer;

/**
 * This class defines a very specific motion strategy used by an mRNA destroyer
 * to follow the attachment point of a strand of mRNA.
 *
 * @author John Blanco
 */
public class DestroyerTrackingRnaMotionStrategy extends MotionStrategy {
    private final MessengerRna messengerRna;

    public DestroyerTrackingRnaMotionStrategy( MessengerRnaDestroyer messengerRnaDestroyer ) {
        this.messengerRna = messengerRnaDestroyer.getMessengerRnaBeingDestroyed();
    }

    @Override public Vector2D getNextLocation( Vector2D currentLocation, Shape shape, double dt ) {
        Point2D attachmentLocation = messengerRna.getDestroyerAttachmentLocation();
        return new Vector2D( attachmentLocation.getX(), attachmentLocation.getY() );
    }
}
