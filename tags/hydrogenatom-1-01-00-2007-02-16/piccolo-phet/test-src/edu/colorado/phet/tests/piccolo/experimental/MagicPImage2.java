/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.experimental;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Aug 5, 2005
 * Time: 7:33:22 AM
 * Copyright (c) Aug 5, 2005 by Sam Reid
 */

public class MagicPImage2 extends PImage {
    private ImageSource imageSource;
    private double lastRenderScale = 0;
    private boolean nullImage = false;

    public MagicPImage2( Image image ) {
//        this( new PreRenderedImageSource( image ) );
    }

    public static interface ImageSource {
        Image newImage( int width );
    }

    public MagicPImage2( ImageSource imageSource ) {
        this.imageSource = imageSource;
    }

    public Image getImage() {
        return nullImage ? null : super.getImage();
    }

    protected void paint( PPaintContext paintContext ) {
        this.nullImage = true;
        super.paint( paintContext );//trick to get to super.super.paint, without the render.
        this.nullImage = false;
        double scale = paintContext.getScale();
//                System.out.println( "ORIG_RENDER_IMAGE_SCALE= " + scale );//should be about 1.0
        if( scale != 1.0 ) {
            paintContext.getGraphics().scale( 1.0 / scale, 1.0 / scale );
        }

        if( !sameRenderScale( scale ) ) {
            lastRenderScale = scale;
//            Image image = imageSource.newImage( (int)( width * scale ) );
//            setImage( image );
//            setScale( 1.0 / scale );
//            AffineTransform origTx = paintContext.getGraphics().getTransform();
//            paintContext.getGraphics().drawImage( image, (int)getOffset().getX(), (int)getOffset().getY(), null );
        }
    }

    private boolean sameRenderScale( double scale ) {
        double diff = Math.abs( scale - lastRenderScale );
        return diff < 10E-6;
    }

}
