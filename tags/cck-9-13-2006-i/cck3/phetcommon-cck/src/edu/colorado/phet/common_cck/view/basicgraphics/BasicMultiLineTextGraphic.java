/** Sam Reid*/
package edu.colorado.phet.common_cck.view.basicgraphics;

import edu.colorado.phet.common_cck.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 10:45:57 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class BasicMultiLineTextGraphic extends BasicGraphic {
    private ArrayList textGraphics = new ArrayList();
    private LineCreator lineCreator;
    private String[] text;
    private Font font;
    private int x;
    private int y;
    private FontMetrics fontMetrics;

    public BasicMultiLineTextGraphic( String[] text, Font font, FontMetrics fontMetrics, int x, int y, Color color ) {
        this( text, font, fontMetrics, x, y, new Basic( font, fontMetrics, color ) );
    }

    public BasicMultiLineTextGraphic( String text, Font font, FontMetrics fontMetrics, int x, int y, Color foreground, int dx, int dy, Color background ) {
        this( new String[]{text}, font, fontMetrics, x, y, foreground, dx, dy, background );
    }

    public BasicMultiLineTextGraphic( String[] text, Font font, FontMetrics fontMetrics, int x, int y, Color foreground, int dx, int dy, Color background ) {
        this( text, font, fontMetrics, x, y, new Shadowed( font, fontMetrics, foreground, dx, dy, background ) );
    }

    public BasicMultiLineTextGraphic( String[] text, Font font, FontMetrics fontMetrics, int x, int y, LineCreator lineCreator ) {
        this.text = text;
        this.font = font;
        this.x = x;
        this.y = y;
        this.lineCreator = lineCreator;
        this.fontMetrics = fontMetrics;
        init();
    }

    public Font getFont() {
        return font;
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    private void init() {
        int currentY = y;
        for( int i = 0; i < text.length; i++ ) {
            String s = text[i];
            BasicGraphic g = lineCreator.createLine( s, x, currentY );
            textGraphics.add( g );
            currentY += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        }
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
        for( int i = 0; i < textGraphics.size(); i++ ) {
            ITextGraphic iTextGraphic = (ITextGraphic)textGraphics.get( i );
            iTextGraphic.setPosition( x, y );
            y += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        }
        boundsChanged();
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < textGraphics.size(); i++ ) {
            Graphic itg = (Graphic)textGraphics.get( i );
            itg.paint( g );
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getPosition() {
        return new Point( x, y );
    }

    public void setText( String[] text ) {
        if( Arrays.asList( this.text ).equals( Arrays.asList( text ) ) ) {
            return;
        }
        this.text = text;
        textGraphics.clear();
        init();
        boundsChanged();
    }

    public void setText( String text ) {
        setText( new String[]{text} );
    }

    public Rectangle getBounds() {
        if( text.length == 0 ) {
            return null;
        }
        Rectangle2D r = ( (BasicGraphic)textGraphics.get( 0 ) ).getBounds();
        for( int i = 1; i < textGraphics.size(); i++ ) {
            BasicGraphic iTextGraphic = (BasicGraphic)textGraphics.get( i );
            r = r.createUnion( iTextGraphic.getBounds() );
        }
        Rectangle intRect = new Rectangle( (int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight() );
        return intRect;
    }

    /**
     * An interface for creating lines of text.  Provide your own implementation if you want.
     */
    public interface LineCreator {
        BasicGraphic createLine( String text, int x, int y );
    }

    static class Shadowed implements LineCreator {
        private Font font;
        private FontMetrics fontMetrics;
        private Color foregroundColor;
        private int dx;
        private int dy;
        private Color backgroundColor;

        public Shadowed( Font font, FontMetrics fontMetrics, Color foregroundColor, int dx, int dy, Color backgroundColor ) {
            this.font = font;
            this.fontMetrics = fontMetrics;
            this.foregroundColor = foregroundColor;
            this.dx = dx;
            this.dy = dy;
            this.backgroundColor = backgroundColor;
        }

        public BasicGraphic createLine( String text, int x, int y ) {
            return new BasicShadowTextGraphic( text, font, fontMetrics, x, y, foregroundColor, dx, dy, backgroundColor );
        }
    }

    static class Basic implements LineCreator {
        private Font font;
        private FontMetrics fontMetrics;
        private Color color;

        public Basic( Font font, FontMetrics fontMetrics, Color color ) {
            this.font = font;
            this.fontMetrics = fontMetrics;
            this.color = color;
        }

        public BasicGraphic createLine( String text, int x, int y ) {
            return new BasicTextGraphic( text, font, color, x, y, fontMetrics );
        }
    }

}