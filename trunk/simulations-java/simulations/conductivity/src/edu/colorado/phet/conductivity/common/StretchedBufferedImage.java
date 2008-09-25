// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.common;

import java.awt.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.conductivity.oldphetgraphics.Graphic;


public class StretchedBufferedImage
        implements Graphic {

    public StretchedBufferedImage( BufferedImage bufferedimage, Rectangle rectangle1 ) {
        battIm = bufferedimage;
        rectangle = rectangle1;
    }

    public void paint( Graphics2D graphics2d ) {
        java.awt.geom.Rectangle2D.Double double1 = new java.awt.geom.Rectangle2D.Double( 0.0D, 0.0D, battIm.getWidth(), battIm.getHeight() );
        ModelViewTransform2D modelviewtransform2d = new ModelViewTransform2D( double1, rectangle );
        java.awt.geom.AffineTransform affinetransform = modelviewtransform2d.getAffineTransform();
        graphics2d.drawRenderedImage( battIm, affinetransform );
    }

    private BufferedImage battIm;
    private Rectangle rectangle;

    public void setOutputRect( Rectangle rectangle ) {
        this.rectangle = rectangle;
    }
}
