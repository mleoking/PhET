/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics.positioned;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 16, 2003
 * Time: 6:23:33 PM
 * Copyright (c) Apr 16, 2003 by Sam Reid
 */
public class PositionedImageGraphic implements PositionedGraphic {
    BufferedImage bi;

    public PositionedImageGraphic( BufferedImage bi ) {
        this.bi = bi;
    }

    public void paint( Graphics2D g, int x, int y ) {
        AffineTransform at = AffineTransform.getTranslateInstance( x, y );
        g.drawRenderedImage( bi, at );
    }

    public Rectangle getRectangle( int x, int y ) {
        return new Rectangle( x, y, bi.getWidth(), bi.getHeight() );
    }
}
