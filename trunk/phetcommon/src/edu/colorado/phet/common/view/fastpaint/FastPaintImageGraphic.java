package edu.colorado.phet.common.view.fastpaint;

import edu.colorado.phet.common.view.graphics.BufferedImageGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FastPaintImageGraphic extends BufferedImageGraphic {
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
        Rectangle visibleBounds = super.getShape().getBounds();
        return visibleBounds;
    }

    public void setImage( BufferedImage image ) {
        Rectangle origRect = getVisibleRect();
        super.setImage( image );
        Rectangle newRect = getVisibleRect();
        GraphicsUtil.fastRepaint( parent, origRect, newRect );
    }

    public void setTransform( AffineTransform transform ) {
        Rectangle origRect = getVisibleRect();
        super.setTransform( transform );
        Rectangle newRect = getVisibleRect();
        GraphicsUtil.fastRepaint( parent, origRect, newRect );
    }

    public void setPosition( Point ctr ) {
        setTransform( getCenterTransform( ctr.x, ctr.y ) );
    }

}