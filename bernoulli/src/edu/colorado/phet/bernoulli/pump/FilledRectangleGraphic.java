/**
 * Class: FilledRectangleGraphic
 * Package: edu.colorado.phet.bernoulli.pump
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.bernoulli.pump;

import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class FilledRectangleGraphic extends RectangleGraphic {

    private Color fillColor;

    public FilledRectangleGraphic( Rectangle2D.Double model, ModelViewTransform2d transform, Color fillColor ) {
        super( model, transform );
        this.fillColor = fillColor;
    }

    public void paint( Graphics2D g ) {
        g.setColor( fillColor );
        g.fill( getRectangle() );
        super.paint( g );
    }

}
