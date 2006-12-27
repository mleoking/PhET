package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * User: Sam Reid
 * Date: Sep 23, 2003
 * Time: 2:29:58 AM
 * Copyright (c) Sep 23, 2003 by Sam Reid
 */
public class RectangleImageGraphic2WithBuffer implements Graphic {
    BufferedImage image;
    private ImageObserver observer;
    Rectangle outputRect;
    AffineTransform at;
    private boolean visible = true;

    BufferedImage buffer;

    public RectangleImageGraphic2WithBuffer( BufferedImage image, ImageObserver observer ) {
        this.image = image;
        this.observer = observer;
    }

    public void paint( Graphics2D g ) {
        if( at != null && visible && buffer != null ) {
            g.drawImage( buffer, outputRect.x, outputRect.y, observer );
        }
    }

    public void setOutputRect( Rectangle outputRect ) {
        this.outputRect = outputRect;
        at = new AffineTransform();
        double sx = outputRect.getWidth() / image.getWidth();
        double sy = outputRect.getHeight() / image.getHeight();
        at.scale( sx, sy );
//        isx = (int) sx;
//        isy = (int) sy;
//        at.translate(outputRect.x/sx,outputRect.y/sy);
//        double dx=outputRect.x;
//        double dy
        AffineTransformOp ato = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );
        buffer = null;
        buffer = ato.filter( image, buffer );
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}