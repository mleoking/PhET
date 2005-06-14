/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph2;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

/**
 * User: Sam Reid
 * Date: Jun 8, 2005
 * Time: 4:39:00 AM
 * Copyright (c) Jun 8, 2005 by Sam Reid
 */

public class RenderEvent {
    private Graphics2D g;

    public RenderEvent( Graphics2D g ) {
        this.g = g;
    }

    public Shape getClip() {
        return g.getClip();
    }

    public void fill( Shape shape ) {
        g.fill( shape );
    }

    public void transform( AffineTransform transform ) {
        g.transform( transform );
    }

    public void setPaint( Paint paint ) {
        g.setPaint( paint );
    }

    public void setFont( Font font ) {
        g.setFont( font );
    }

    public void clip( Shape clip ) {
        g.setClip( clip );
    }

    public void setStroke( Stroke stroke ) {
        g.setStroke( stroke );
    }

    public void setRenderingHints( RenderingHints renderingHints ) {
        g.setRenderingHints( renderingHints );
    }

    public void drawRenderedImage( RenderedImage image, AffineTransform transform ) {
        g.drawRenderedImage( image, transform );
    }

    public void draw( Shape shape ) {
        g.draw( shape );
    }

    public FontRenderContext getFontRenderContext() {
        return g.getFontRenderContext();
    }

    public void drawString( String text, float x, float y ) {
        g.drawString( text, x, y );
    }

    public AffineTransform getTransform() {
        return g.getTransform();
    }

    public Graphics2D getGraphics2D() {
        return g;
    }

    public Shape createTransformedShape( Rectangle2D localBounds ) {
        return g.getTransform().createTransformedShape( localBounds );
    }
}
