package edu.colorado.phet.common_1200.view.fastpaint;

import edu.colorado.phet.common_1200.view.graphics.BufferedImageGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FastPaintImageGraphic extends BufferedImageGraphic implements FastPaint.Graphic {
    FastPaint fastPaint;
    //  Allows the user to set a location without getting the rectangle starting at 0,0 unioned into the repaint.
    boolean inited = false;

    public FastPaintImageGraphic( BufferedImage image, Component parent ) {
        super( image );
        fastPaint = new FastPaint( parent, this );
        repaint();
    }

    public Rectangle getBounds() {
        Rectangle visibleBounds = super.getShape().getBounds();
        return visibleBounds;
    }

    public void setImage( BufferedImage image ) {
        super.setImage( image );
        repaint();
    }

    private void repaint() {
        fastPaint.repaint();
    }

    public void setTransform( AffineTransform transform ) {
        if( !inited ) {
            super.setTransform( transform );
            repaint();
            inited = true;
        }
        else {
            super.setTransform( transform );
            repaint();
        }
    }

    public void setPosition( Point ctr ) {
        setTransform( getCenterTransform( ctr.x, ctr.y ) );
    }

}