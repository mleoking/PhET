/** Sam Reid*/
package edu.colorado.phet.common_cck.view.phetgraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Applies a transform to the delegate PhetGraphic before painting
 * and bounds-handling.
 */
public class PhetTransformGraphic extends PhetGraphic {
    private PhetGraphic graphic;
    private AffineTransform transform;

    public PhetTransformGraphic( PhetGraphic graphic, AffineTransform transform ) {
        super( graphic.getComponent() );
        this.graphic = graphic;
        this.transform = transform;
    }

    protected Rectangle determineBounds() {
        Rectangle origBounds = graphic.determineBounds();
        return transform.createTransformedShape( origBounds ).getBounds();
    }

    public void paint( Graphics2D g ) {
        AffineTransform origTx = g.getTransform();
        g.transform( transform );
        graphic.paint( g );
        g.setTransform( origTx );
    }

    public void translate( double dx, double dy ) {
        transform.preConcatenate( AffineTransform.getTranslateInstance( dx, dy ) );
        setBoundsDirty();
        repaint();
    }

    public void rotate( double angle ) {
        transform.preConcatenate( AffineTransform.getRotateInstance( angle ) );
        setBoundsDirty();
        repaint();
    }

    public void rotate( double angle, double x, double y ) {
        transform.preConcatenate( AffineTransform.getRotateInstance( angle, x, y ) );
    }

    public void setTransform( AffineTransform transform ) {
        this.transform = transform;
        setBoundsDirty();
        repaint();
    }

}
