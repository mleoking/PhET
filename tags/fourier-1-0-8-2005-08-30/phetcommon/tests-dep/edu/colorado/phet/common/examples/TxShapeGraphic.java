/**
 * Class: TxShapeGraphic
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: Jun 25, 2004
 */
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TxShapeGraphic implements Graphic {
    AffineTransform tx;
    FastPaintShapeGraphic fastPaintShapeGraphic;

    public void setShape( Shape shape ) {
        fastPaintShapeGraphic.setShape( tx.createTransformedShape( shape ) );
    }

    public void paint( Graphics2D g ) {
        fastPaintShapeGraphic.paint( g );
    }

}
