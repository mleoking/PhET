/**
 * Class: TxBoundary
 * Package: edu.colorado.phet.common.examples.examples
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.common.view.graphics.bounds;

import edu.colorado.phet.common.view.graphics.Boundary;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * @deprecated
 */
public class TxBoundary implements Boundary {
    AffineTransform transform;
    ModelBoundary modelBounds;
    /**
     * @deprecated 
     * @param modelBounds
     * @param transform
     */
    public TxBoundary( ModelBoundary modelBounds, AffineTransform transform ) {
        this.transform = transform;
        this.modelBounds = modelBounds;
    }

    public boolean contains( int x, int y ) {
        Point2D modelCoordinate = transform.transform( new Point( x, y ), null );
        return modelBounds.contains( modelCoordinate.getX(), modelCoordinate.getY() );
    }

}
