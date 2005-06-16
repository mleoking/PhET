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

public class StoredBufferGraphic extends AbstractGraphic {
    private AbstractGraphic graphic;
    private BufferedImage buffer;
    private Rectangle2D renderBounds;

    public StoredBufferGraphic( AbstractGraphic graphic ) {
        this.graphic = graphic;
        updateBuffer();
    }

    public void updateBuffer() {
        graphic.paint( new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB ).createGraphics() );//to ensure we have bounds, etc...
        Rectangle2D r = graphic.getLocalBounds();
        BufferedImage image = createBuffer( (int)r.getWidth(), (int)r.getHeight() );
        Graphics2D bufferedGraphics = image.createGraphics();
        bufferedGraphics.translate( r.getX(), r.getY() );
        graphic.paint( bufferedGraphics );
        this.renderBounds = r;
        this.buffer = image;
    }

    protected BufferedImage createBuffer( int width, int height ) {
        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        g2.setColor( Color.green );
        g2.fillRect( 0, 0, width, height );
        return image;
    }

    public void paint( Graphics2D graphics2D ) {
        super.setup( graphics2D );
        graphics2D.drawRenderedImage( buffer, new AffineTransform() );
        super.restore( graphics2D );
    }

    public Rectangle2D getLocalBounds() {
        return new Rectangle2D.Double( 0, 0, buffer.getWidth(), buffer.getHeight() );
    }
}
