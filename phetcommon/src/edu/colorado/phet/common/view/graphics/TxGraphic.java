/**
 * Class: TxGraphic
 * Package: edu.colorado.phet.common.examples.examples
 * Author: Another Guy
 * Date: Dec 19, 2003
 * asdf
 */
package edu.colorado.phet.common.view.graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A decorator for Graphic that applies an AffineTransform before
 * the Graphic is painted.
 */
public class TxGraphic implements Graphic {
    private Graphic graphic;
    private AffineTransform transform;

    public TxGraphic( Graphic graphic, AffineTransform transform ) {
        this.graphic = graphic;
        this.transform = transform;
    }

    public void setGraphic( Graphic graphic ) {
        this.graphic = graphic;
    }

    public void setTransform( AffineTransform transform ) {
        this.transform = transform;
    }

    public void paint( Graphics2D g ) {
        AffineTransform orgTx = g.getTransform();
        g.transform( transform );
        graphic.paint( g );
        g.setTransform( orgTx );
    }
}
