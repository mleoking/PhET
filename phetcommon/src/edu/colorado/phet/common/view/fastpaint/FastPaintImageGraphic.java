/**
 * Class: FastPaintImageGraphic
 * Package: edu.colorado.phet.common.view.fastpaint
 * Author: Another Guy
 * Date: May 19, 2004
 */
package edu.colorado.phet.common.view.fastpaint;

import edu.colorado.phet.common.view.graphics.SimpleBufferedImageGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FastPaintImageGraphic extends SimpleBufferedImageGraphic {
    private Component parent;

    public FastPaintImageGraphic( BufferedImage image, Component parent ) {
        super( image );
        this.parent = parent;
    }

    public FastPaintImageGraphic( BufferedImage image, AffineTransform transform, Component parent ) {
        super( image, transform );
        this.parent = parent;
    }

    public Rectangle getVisibleRect() {
        return super.getShape().getBounds();
    }

    public void setImage( BufferedImage image ) {
        Rectangle origRect = getVisibleRect();
        super.setImage( image );
        Rectangle newRect = getVisibleRect();
        repaint( origRect, newRect );
    }

    public void setTransform( AffineTransform transform ) {
        Rectangle origRect = getVisibleRect();
        super.setTransform( transform );
        Rectangle newRect = getVisibleRect();
        repaint( origRect, newRect );
    }

    public void setPosition( Point ctr ) {
        Rectangle origRect = getVisibleRect();
        super.setPosition( ctr );
        Rectangle newRect = getVisibleRect();
        repaint( origRect, newRect );
    }

    public void setLocation( int centerX, int centerY ) {
        Rectangle origRect = getVisibleRect();
        super.setLocation( centerX, centerY );
        Rectangle newRect = getVisibleRect();
        repaint( origRect, newRect );
    }

    private void repaint( Rectangle bounds ) {
        if( bounds != null ) {
            parent.repaint( bounds.x, bounds.y, bounds.width, bounds.height );
        }
    }

    private void repaint( Rectangle orig, Rectangle newRect ) {
        repaint( orig );
        repaint( newRect );
    }
}
