/**
 * Class: TextGraphic
 * Package: edu.colorado.phet.common.view.graphics
 * Author: Another Guy
 * Date: May 11, 2004
 */
package edu.colorado.phet.cck3.common;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ComponentTextGraphic implements ITextGraphic {
    private String text;
    private Font font;
    float x;
    float y;
    private Component component;
    Paint paint;
    private FontMetrics fontMetrics;
    private boolean boundsDirty = true;
    private Rectangle2D bounds2d;
    private Rectangle bounds;

    public ComponentTextGraphic( String text, Font font, float x, float y, Component component ) {
        this( text, font, x, y, component, Color.black );
    }

    public ComponentTextGraphic( String text, Font font, float x, float y, Component component, Paint paint ) {
        this.text = text;
        this.font = font;
        this.x = x;
        this.y = y;
        this.component = component;
        this.fontMetrics = component.getFontMetrics( font );
        this.paint = paint;
    }

    public void paint( Graphics2D g ) {
        g.setPaint( paint );
        g.setFont( font );
        g.drawString( text, x, y );
    }

    public boolean contains( int x, int y ) {
        return getBounds2D().contains( x, y );
    }

    public void setText( String text ) {
        boundsDirty = true;
        this.text = text;
    }

    public void setFont( Font font ) {
        boundsDirty = true;
        this.font = font;
        this.fontMetrics = component.getFontMetrics( font );
    }

    public void setLocation( float x, float y ) {
        boundsDirty = true;
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
        if( boundsDirty ) {
            determineBounds();
        }
        return bounds2d;
    }

    private void determineBounds() {
        int width = fontMetrics.stringWidth( text );//this ignores antialias and fractional metrics.
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        bounds2d = new Rectangle2D.Double( this.x, this.y - ascent, width, ascent + descent );
        bounds = new Rectangle( (int)this.x, (int)this.y - ascent, width, ascent + descent );
        boundsDirty = false;
    }

    public Rectangle getBounds() {
        if( boundsDirty ) {
            determineBounds();
        }
        return bounds;
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
        this.boundsDirty = true;
    }

    public Point2D getRightPoint() {
        Rectangle2D r2 = getBounds2D();
        return new Point2D.Double( r2.getX() + r2.getWidth(), r2.getY() + r2.getHeight() / 2 );
    }
}
