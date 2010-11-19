package edu.colorado.phet.common.phetcommon.view.graphics.transforms;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * @author Sam Reid
 */
public class ModelViewTransform {
    private AffineTransform transform;

    public Point2D modelToView( Point2D pt ) {
        return transform.transform( pt, null );
    }

    public Point2D viewToModel( Point2D pt ) {
        try {
            return transform.createInverse().transform( pt, null );
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
    }

    public Point2D viewToModelDelta( Point2D delta ) {
        try {
            return transform.createInverse().deltaTransform( delta, null );
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
    }

    public Point2D modelToViewDelta( Point2D delta ) {
        return transform.deltaTransform( delta, null );
    }

    /**
     * Returns a defensive copy of the AffineTransform in the ModelViewTransform.
     *
     * @return
     */
    public AffineTransform getTransform() {
        return new AffineTransform( transform );
    }
}
