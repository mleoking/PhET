/**
 * Class: TxGraphic
 * Package: edu.colorado.phet.common.examples.examples
 * Author: Another Guy
 * Date: Dec 19, 2003
 * asdf
 */
package edu.colorado.phet.common.view.graphics;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TxGraphic implements Graphic {
    private Graphic graphic;
    private AffineTransform modelToViewTx;

    public TxGraphic( Graphic circleGraphic, AffineTransform modelToViewTx ) {
        this.graphic = circleGraphic;
        this.modelToViewTx = modelToViewTx;
    }

    public void paint( Graphics2D g ) {
        AffineTransform orgTx = g.getTransform();
        g.setTransform( modelToViewTx );
        graphic.paint( g );
        g.setTransform( orgTx );
    }
}
