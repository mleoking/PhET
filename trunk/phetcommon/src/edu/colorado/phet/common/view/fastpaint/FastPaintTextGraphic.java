/**
 * Class: FastPaintTextGraphic
 * Package: edu.colorado.phet.common.view.fastpaint
 * Author: Another Guy
 * Date: May 19, 2004
 */
package edu.colorado.phet.common.view.fastpaint;

import edu.colorado.phet.common.view.graphics.TextGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class FastPaintTextGraphic extends TextGraphic {
    private Component parent;

    public FastPaintTextGraphic( String text, Font font, float x, float y, Component parent ) {
        super( text, font, x, y );
        this.parent = parent;
    }

    private Rectangle getViewBounds() {
        Rectangle2D rect2D = super.getBounds();
        Rectangle rect = null;
        if( rect2D != null ) {
            rect = new Rectangle( (int)rect2D.getX(), (int)rect2D.getY(),
                                  (int)rect2D.getWidth(), (int)rect2D.getHeight() );
        }
        return rect;
    }

    public void setText( String text ) {
        Rectangle rectA = getViewBounds();
        super.setText( text );
        Rectangle rectB = getViewBounds();
        repaint( rectA, rectB );
    }

    public void setFont( Font font ) {
        Rectangle rectA = getViewBounds();
        super.setFont( font );
        Rectangle rectB = getViewBounds();
        repaint( rectA, rectB );
    }

    public void setLocation( float x, float y ) {
        Rectangle rectA = getViewBounds();
        super.setLocation( x, y );
        Rectangle rectB = getViewBounds();
        repaint( rectA, rectB );
    }

    public void setPaint( Paint paint ) {
        Rectangle rect = getViewBounds();
        super.setPaint( paint );
        repaint( rect );
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
