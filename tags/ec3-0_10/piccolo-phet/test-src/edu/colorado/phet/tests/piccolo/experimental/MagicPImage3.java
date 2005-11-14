/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.experimental;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Aug 5, 2005
 * Time: 7:33:22 AM
 * Copyright (c) Aug 5, 2005 by Sam Reid
 */

public class MagicPImage3 extends PImage {
    private ImageSource imageSource;
    private AffineTransform renderTransform;

    public MagicPImage3( BufferedImage image ) {
        this( new PreRenderedImageSource( image ) );
        super.setImage( image );
    }

    public static interface ImageSource {
        Image newImage( AffineTransform transform );
    }

    public MagicPImage3( ImageSource imageSource ) {
        this.imageSource = imageSource;
    }

    protected void paint( PPaintContext paintContext ) {
        if( imageSource == null ) {
            return;
        }
        AffineTransform transform = paintContext.getGraphics().getTransform();
        if( renderTransform == null || !transformEquals( transform ) ) {
            rerender( transform );
            renderTransform = new AffineTransform( transform );
            System.out.println( System.currentTimeMillis() + ": Re-Rendering" );
            System.out.println( "transform = " + transform );
            invalidateFullBounds();
            repaint();
        }
        paintContext.getGraphics().setTransform( new AffineTransform() );
        paintContext.getGraphics().drawImage( getImage(), (int)transform.getTranslateX(), (int)transform.getTranslateY(), null );
        paintContext.getGraphics().setTransform( transform );
    }

    private boolean transformEquals( AffineTransform transform ) {
        return transform.getScaleX() == renderTransform.getScaleX();
    }

    //to make this class behave more like PImage
    public void setImage( BufferedImage image ) {
        imageSource = new PreRenderedImageSource( image );
        renderTransform = null;
//        super.setImage( fileName );
    }

    private void rerender( AffineTransform transform ) {
        super.setImage( imageSource.newImage( transform ) );
//        setBounds( -transform.getTranslateX(), -transform.getTranslateY(), getImage().getWidth( null ), getImage().getHeight( null ) );
        setBounds( 0, 0, getImage().getWidth( null )/transform.getScaleX(), getImage().getHeight( null )/transform.getScaleY() );
    }

}
