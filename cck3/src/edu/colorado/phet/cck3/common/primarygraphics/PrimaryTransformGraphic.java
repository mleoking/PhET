/** Sam Reid*/
package edu.colorado.phet.cck3.common.primarygraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 8:40:09 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class PrimaryTransformGraphic extends PrimaryGraphic {
    PrimaryGraphic graphic;
    AffineTransform transform;

    public PrimaryTransformGraphic( PrimaryGraphic graphic, AffineTransform transform ) {
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
        transform.preConcatenate( AffineTransform.getRotateInstance( angle));
        setBoundsDirty();
        repaint();
    }

}
