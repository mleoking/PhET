/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.view.graphics.BoundedGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 10:45:57 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class MultiLineComponentTextGraphic implements BoundedGraphic {
    private ArrayList textGraphics = new ArrayList();
    private LineCreator lineCreator;
    private Component component;
    private String[] text;
    private int x;
    private int y;
    private FontMetrics fontMetrics;

    public MultiLineComponentTextGraphic( Component component, String[] text, Font font, int x, int y, Color color ) {
        this.component = component;
        this.text = text;
        this.x = x;
        this.y = y;
        lineCreator = new Basic( component, font, color );
        this.fontMetrics = component.getFontMetrics( font );
        init();
    }

    public MultiLineComponentTextGraphic( Component component, String[] text, Font font, int x, int y, Color foreground, int dx, int dy, Color background ) {
        this.component = component;
        this.text = text;
        this.x = x;
        this.y = y;
        lineCreator = new Shadowed( component, font, foreground, dx, dy, background );
        this.fontMetrics = component.getFontMetrics( font );
        init();
    }

    private void init() {
        for( int i = 0; i < text.length; i++ ) {
            String s = text[i];
            ITextGraphic g = lineCreator.createLine( s, x, y );
            textGraphics.add( g );
            y += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        }
    }

    public void setPosition( int x, int y ) {
        for( int i = 0; i < textGraphics.size(); i++ ) {
            ITextGraphic iTextGraphic = (ITextGraphic)textGraphics.get( i );
            iTextGraphic.setPosition( x, y );
            y += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        }
    }

    static class Shadowed implements LineCreator {
        private Font font;
        private Color foregroundColor;
        private int dx;
        private int dy;
        private Color backgroundColor;
        private Component component;

        public Shadowed( Component component, Font font, Color foregroundColor, int dx, int dy, Color backgroundColor ) {
            this.component = component;
            this.font = font;
            this.foregroundColor = foregroundColor;
            this.dx = dx;
            this.dy = dy;
            this.backgroundColor = backgroundColor;
        }

        public ITextGraphic createLine( String text, int x, int y ) {
            return new ComponentShadowTextGraphic( text, font, x, y, foregroundColor, dx, dy, backgroundColor, component );
        }
    }

    static class Basic implements LineCreator {
        private Font font;
        private Color color;
        private Component component;

        public Basic( Component component, Font font, Color color ) {
            this.component = component;
            this.font = font;
            this.color = color;
        }

        public ITextGraphic createLine( String text, int x, int y ) {
            return new ComponentTextGraphic( text, font, x, y, component, color );
        }
    }

    interface LineCreator {
        ITextGraphic createLine( String text, int x, int y );
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < textGraphics.size(); i++ ) {
            ITextGraphic itg = (ITextGraphic)textGraphics.get( i );
            itg.paint( g );
        }
    }

    private ITextGraphic textGraphicAt( int i ) {
        return (ITextGraphic)textGraphics.get( i );
    }

    public Rectangle getBounds() {
        Rectangle2D r = textGraphicAt( 0 ).getBounds();
        for( int i = 1; i < textGraphics.size(); i++ ) {
            ITextGraphic iTextGraphic = (ITextGraphic)textGraphics.get( i );
            r = r.createUnion( iTextGraphic.getBounds() );
        }
        Rectangle intRect = new Rectangle( (int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight() );
        return intRect;
    }

    public boolean contains( int x, int y ) {
        for( int i = 0; i < textGraphics.size(); i++ ) {
            ITextGraphic iTextGraphic = (ITextGraphic)textGraphics.get( i );
            if( iTextGraphic.contains( x, y ) ) {
                return true;
            }
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getLeftSide() {
        return RectangleUtils.getLeftSide( getBounds() );
    }

    public Point getEast() {
        return RectangleUtils.getEastSide( getBounds() );
    }
}