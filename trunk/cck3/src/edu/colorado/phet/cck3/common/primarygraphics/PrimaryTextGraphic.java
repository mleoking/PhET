/** Sam Reid*/
package edu.colorado.phet.cck3.common.primarygraphics;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 5:59:29 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class PrimaryTextGraphic extends PrimaryGraphic implements IPrimaryTextGraphic {
    private Font font;
    private String text;
    private Color color;
    private int x;
    private int y;
    private FontMetrics fontMetrics;

    public PrimaryTextGraphic( Component component, Font font, String text, Color color, int x, int y ) {
        super( component );
        this.font = font;
        this.text = text;
        this.color = color;
        this.x = x;
        this.y = y;
        fontMetrics = component.getFontMetrics( font );
    }

    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            g.setFont( font );
            g.setColor( color );
            g.drawString( text, x, y );
        }
    }

    protected Rectangle determineBounds() {
        int width = fontMetrics.stringWidth( text );//this ignores antialias and fractional metrics.
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int leading =fontMetrics.getLeading();
        Rectangle bounds = new Rectangle( (int)this.x, (int)this.y - ascent+leading, width, ascent + descent+leading );
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

    public Color getColor() {
        return color;
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
        setBoundsDirty();
        repaint();
    }
}
