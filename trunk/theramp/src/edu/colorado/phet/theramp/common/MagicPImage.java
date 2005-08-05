/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;

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
        Image newImage( double width );
    }

    public MagicPImage( ImageSource imageSource, int width ) {
        this.imageSource = imageSource;
        this.width = width;
    }

    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        double scale = paintContext.getScale();
        if( imageSource == null || lastRenderScale != scale ) {
            lastRenderScale = scale;
            Image image = imageSource.newImage( (int)( width / scale ) );
            this.image.setImage( image );
            this.image.setScale( scale );
        }
    }
}
