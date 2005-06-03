/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:17:02 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class ImageGraphic extends AbstractGraphic {
    private RenderedImage image;
    private static final AffineTransform IDENTITY = new AffineTransform();

    public ImageGraphic( RenderedImage image ) {
        this.image = image;
    }

    public void paint( Graphics2D graphics2D ) {
        super.setup( graphics2D );
        graphics2D.drawRenderedImage( image, IDENTITY );
        super.restore( graphics2D );
    }

    public Rectangle2D getLocalBounds() {
        return new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() );
    }

}
