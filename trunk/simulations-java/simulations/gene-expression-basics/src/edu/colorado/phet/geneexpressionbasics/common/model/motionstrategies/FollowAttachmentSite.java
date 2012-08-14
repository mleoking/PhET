// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;

/**
 * Motion strategy that tracks an attachment site and moves to wherever it is.
 *
 * @author John Blanco
 */
public class FollowAttachmentSite extends MotionStrategy {
    private final AttachmentSite attachmentSite;

    public FollowAttachmentSite( AttachmentSite attachmentSite ) {
        this.attachmentSite = attachmentSite;
    }

    @Override public Vector2D getNextLocation( Vector2D currentLocation, Shape shape, double dt ) {
        return attachmentSite.locationProperty.get();
    }
}
