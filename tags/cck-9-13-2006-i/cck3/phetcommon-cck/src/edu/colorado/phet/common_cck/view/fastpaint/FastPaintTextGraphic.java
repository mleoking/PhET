/**
 * Class: FastPaintTextGraphic
 * Package: edu.colorado.phet.common.view.fastpaint
 * Author: Another Guy
 * Date: May 19, 2004
 */
package edu.colorado.phet.common_cck.view.fastpaint;

import edu.colorado.phet.common_cck.view.graphics.TextGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class FastPaintTextGraphic extends TextGraphic implements FastPaint.Graphic {
    FastPaint fastPaint;

    public FastPaintTextGraphic( String text, Font font, float x, float y, Component parent ) {
        super( text, font, x, y );
        fastPaint = new FastPaint( parent, this );
        repaint();
    }

    public FastPaintTextGraphic( String text, Font font, float x, float y, Paint paint, Component parent ) {
        super( text, font, x, y, paint );
        fastPaint = new FastPaint( parent, this );
        repaint();
    }

    private void repaint() {
        fastPaint.repaint();
    }

    public Rectangle getBounds() {
        Rectangle2D rect2D = super.getBounds2D();
        Rectangle rect = null;
        if( rect2D != null ) {
            rect = new Rectangle( (int)rect2D.getX(), (int)rect2D.getY(),
                                  (int)rect2D.getWidth(), (int)rect2D.getHeight() );
        }
        return rect;
    }

    public void setText( String text ) {
        String oldText = super.getText();
        if( !oldText.equals( text ) ) {
            super.setText( text );
            repaint();
        }
    }

    public void setFont( Font font ) {
        super.setFont( font );
        repaint();
    }

    public void setLocation( double x, double y ) {
        super.setLocation( (float)x, (float)y );
        repaint();
    }

    public void setPaint( Paint paint ) {
        super.setPaint( paint );
        repaint();
    }
}
