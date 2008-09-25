// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.common;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.conductivity.oldphetgraphics.Graphic;


public class SimpleBufferedImageGraphic
        implements Graphic {

    public SimpleBufferedImageGraphic( BufferedImage bufferedimage ) {
        image = bufferedimage;
    }

    public void paint( Graphics2D graphics2d ) {
        if ( image != null && transform != null ) {
            graphics2d.drawRenderedImage( image, transform );
        }
    }

    public void setTransform( AffineTransform affinetransform ) {
        transform = affinetransform;
    }

    public void setPosition( Point point ) {
        setTransform( getCenterTransform( point ) );
    }

    public AffineTransform getCenterTransform( Point point ) {
        double d = image.getWidth();
        double d1 = image.getHeight();
        AffineTransform affinetransform = AffineTransform.getTranslateInstance( (double) point.x - d / 2D, (double) point.y - d1 / 2D );
        return affinetransform;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    BufferedImage image;
    AffineTransform transform;
}
