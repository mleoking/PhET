/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.experimental;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Aug 5, 2005
 * Time: 7:33:22 AM
 * Copyright (c) Aug 5, 2005 by Sam Reid
 */

public class MagicPImage extends PNode {
    private PImage image;
    private ImageSource imageSource;
    private int width;
    private double lastRenderScale = 0;

    public static interface ImageSource {
        Image newImage( int width );
    }

    public MagicPImage( ImageSource imageSource, int width ) {
        this.imageSource = imageSource;
        this.width = width;
        image = new PImage( new BufferedImage( 50, 50, BufferedImage.TYPE_INT_RGB ) ) {
            protected void paint( PPaintContext paintContext ) {

                double scale = paintContext.getScale();
//                System.out.println( "ORIG_RENDER_IMAGE_SCALE= " + scale );//should be about 1.0
                if( scale != 1.0 ) {
                    paintContext.getGraphics().scale( 1.0 / scale, 1.0 / scale );
                }

//                System.out.println( "FINAL_RENDER_IMAGE_SCALE= " + paintContext.getScale());//should be exactly 1.0

                super.paint( paintContext );
            }
        };
        addChild( image );
    }

    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        double scale = paintContext.getScale();
        if( !equalsRenderScale( scale ) ) {
            lastRenderScale = scale;
            Image image = imageSource.newImage( (int)( width * scale ) );
//            System.out.println( "image = " + image );
            this.image.setImage( image );
            this.image.setScale( 1.0 / scale );
//            System.out.println( "scale = " + scale );
        }
    }

    private boolean equalsRenderScale( double scale ) {
        double diff = Math.abs( scale - lastRenderScale );
        return diff < 10E-6;
    }

    public void fullPaint( PPaintContext paintContext ) {
        super.fullPaint( paintContext );
    }
}
