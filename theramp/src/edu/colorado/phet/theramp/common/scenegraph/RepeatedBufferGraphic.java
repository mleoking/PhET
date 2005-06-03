/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 10:06:42 PM
 * Copyright (c) Jun 2, 2005 by Sam Reid
 */

public class RepeatedBufferGraphic extends AbstractGraphic {
    private AbstractGraphic graphic;

    public RepeatedBufferGraphic( AbstractGraphic graphic ) {
        this.graphic = graphic;
    }

    public void paint( Graphics2D graphics2D ) {
        Rectangle2D r = getLocalBounds();
        BufferedImage image = new BufferedImage( (int)r.getWidth(), (int)r.getHeight(), BufferedImage.TYPE_INT_RGB );
        Graphics2D bufferedGraphics = image.createGraphics();
        bufferedGraphics.translate( r.getX(), r.getY() );
        graphic.paint( bufferedGraphics );
        super.setup( graphics2D );
        graphics2D.drawRenderedImage( image, AffineTransform.getTranslateInstance( r.getX(), r.getY() ) );
        super.restore( graphics2D );
    }

    public Rectangle2D getLocalBounds() {
        return graphic.getLocalBounds();//todo should account for our transform.
    }
}
