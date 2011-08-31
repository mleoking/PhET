// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.geom.Point2D;

import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.IMotionStrategy;

/**
 * A motion strategy that causes the user to move towards the attachment site
 * until within a certain threshold, then remain at the attachment site's
 * location even if it moves.
 *
 * @author John Blanco
 */
public class AttachToSiteMotionStrategy implements IMotionStrategy {

    public AttachToSiteMotionStrategy( AttachmentSite attachmentSite ) {

    }

    public Point2D getNextLocation( double dt, Point2D currentLocation ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
