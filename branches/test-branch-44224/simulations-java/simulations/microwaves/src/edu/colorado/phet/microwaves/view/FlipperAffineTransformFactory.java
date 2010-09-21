/**
 * Class: FlipperAffineTransformFactory
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.microwaves.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.microwaves.common.graphics.AffineTransformFactory;

/**
 * Produces affine transforms that map from model to view coordinates,
 * and invert the y axis.
 */
public class FlipperAffineTransformFactory implements AffineTransformFactory {
    private Rectangle2D modelBounds;

    public FlipperAffineTransformFactory( Rectangle2D modelBounds ) {
        this.modelBounds = modelBounds;
    }

    public void setModelBounds( Rectangle2D modelBounds ) {
        this.modelBounds = modelBounds;
    }

    /**
     * Returns an affine transform that will transform from model to
     * view coordinates, and invert the y axis.
     *
     * @param viewBounds
     * @return
     */
    public AffineTransform getTx( Rectangle viewBounds ) {
        AffineTransform aTx = new AffineTransform();
        aTx.translate( viewBounds.getMinX(), viewBounds.getMinY() );
        aTx.scale( viewBounds.getWidth() / modelBounds.getWidth(), viewBounds.getHeight() / -modelBounds.getHeight() );
        aTx.translate( -modelBounds.getMinX(), -modelBounds.getMaxY() );
        return aTx;
    }
}
