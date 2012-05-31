// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Version of ComponentImage but optimized for Piccolo, so that we can run things outside
 * the Swing EDT and not have to do multiple message passes to render.
 * <p/>
 * TODO: add (simple) mouse event handling, similar to ComponentImage.
 */
public class PiccoloImage extends TextureImage {
    public final PNode node;

    public PiccoloImage( int width, int height, boolean hasAlpha, int magFilter, int minFilter, PNode node ) {
        super( width, height, hasAlpha, magFilter, minFilter );
        this.node = node;
    }

    public PiccoloImage( int width, int height, boolean hasAlpha, int magFilter, int minFilter, AffineTransform imageTransform, PNode node ) {
        super( width, height, hasAlpha, magFilter, minFilter, imageTransform );
        this.node = node;
    }

    @Override public void repaint() {
        refreshImage();
    }

    @Override public void paint( Graphics2D graphicsContext ) {
        // clear everything, since we may not be repainting
        graphicsContext.setBackground( new Color( 0, 0, 0, 0 ) );
        graphicsContext.clearRect( 0, 0, getWidth(), getHeight() );

        final PPaintContext paintContext = new PPaintContext( graphicsContext );
        paintContext.setRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        node.fullPaint( paintContext );
    }
}
