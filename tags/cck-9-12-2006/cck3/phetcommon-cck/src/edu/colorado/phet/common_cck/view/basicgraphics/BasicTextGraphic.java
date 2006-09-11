/** Sam Reid*/
package edu.colorado.phet.common_cck.view.basicgraphics;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 5:59:29 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class BasicTextGraphic extends BasicGraphic implements ITextGraphic {
    private Font font;
    private String text;
    private Paint paint;
    private int x;
    private int y;
    private FontMetrics fontMetrics;

    public BasicTextGraphic( String text, Font font, Paint paint, int x, int y, FontMetrics fontMetrics ) {
        this.font = font;
        this.text = text;
        this.paint = paint;
        this.x = x;
        this.y = y;
        this.fontMetrics = fontMetrics;
    }

    public void paint( Graphics2D g ) {
        Font oldFont = g.getFont();
        Paint oldPaint = g.getPaint();
        g.setFont( font );
        g.setPaint( paint );
        g.drawString( text, x, y );
        g.setFont( oldFont );
        g.setPaint( oldPaint );
    }

    public Rectangle getBounds() {
        int width = fontMetrics.stringWidth( text );//this ignores antialias and fractional metrics.
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int leading = fontMetrics.getLeading();
        Rectangle bounds = new Rectangle( (int)this.x, (int)this.y - ascent + leading, width, ascent + descent + leading );
        return bounds;
    }

    public Point getPosition() {
        return new Point( x, y );
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Font getFont() {
        return font;
    }

    public String getText() {
        return text;
    }

    public Paint getPaint() {
        return paint;
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    public void setPosition( int x, int y ) {
        if( this.x == x && this.y == y ) {
            return;
        }
        this.x = x;
        this.y = y;
        boundsChanged();
    }

    public void setText( String text ) {
        if( this.text == text ) {
            return;
        }
        this.text = text;
        boundsChanged();
    }

    public void setPaint( Paint paint ) {
        this.paint = paint;
        appearanceChanged();
    }

    public void setFont( Font font ) {
        this.font = font;
        boundsChanged();
    }
}
