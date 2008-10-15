/**
 * Class: TextGraphic
 * Package: edu.colorado.phet.common.view.graphics
 * Author: Another Guy
 * Date: May 11, 2004
 */
package edu.colorado.phet.common_1200.view;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class TextGraphic implements BoundedGraphic {
    private String text;
    private Font font;
    float x;
    float y;
    Paint paint;
    private FontRenderContext fontRenderContext;

    public TextGraphic( String text, Font font, float x, float y, Paint paint ) {
        this.text = text;
        this.font = font;
        this.x = x;
        this.y = y;
        this.paint = paint;
    }

    public void paint( Graphics2D g ) {
        this.fontRenderContext = g.getFontRenderContext();
        g.setPaint( paint );
        g.setFont( font );
        g.drawString( text, x, y );
    }

    public boolean contains( int x, int y ) {
        if( fontRenderContext == null ) {
            return false;
        }
        else {
            Rectangle2D bounds = font.getStringBounds( text, fontRenderContext );
            return bounds.contains( x, y );
        }
    }

    public void setText( String text ) {
        this.text = text;
    }

    public void setFont( Font font ) {
        this.font = font;
    }

    public void setLocation( float x, float y ) {
        this.x = x;
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public Point2D.Float getLocation() {
        return new Point2D.Float( x, y );
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint( Paint paint ) {
        this.paint = paint;
    }

    public Rectangle2D getBounds2D() {
        Rectangle2D result = null;
        if( fontRenderContext != null ) {
            result = font.getStringBounds( text, fontRenderContext );
            result.setRect( result.getX() + x, result.getY() + y, result.getWidth(), result.getHeight() );
        }
        return result;
    }

    public Rectangle getBounds() {
        Rectangle2D b2 = getBounds2D();
        if( b2 == null ) {
            return null;
        }
        return new Rectangle( (int)b2.getX(), (int)b2.getY(), (int)b2.getWidth(), (int)b2.getHeight() );
    }
}
