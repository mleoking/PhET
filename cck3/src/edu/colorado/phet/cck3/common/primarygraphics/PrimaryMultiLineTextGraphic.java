/** Sam Reid*/
package edu.colorado.phet.cck3.common.primarygraphics;

import edu.colorado.phet.cck3.common.RectangleUtils;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 10:45:57 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class PrimaryMultiLineTextGraphic extends PrimaryGraphic {
    private ArrayList textGraphics = new ArrayList();
    private LineCreator lineCreator;
    private String[] text;
    private int x;
    private int y;
    private FontMetrics fontMetrics;

    public PrimaryMultiLineTextGraphic( Component component, String[] text, Font font, int x, int y, Color color ) {
        super( component );
        this.text = text;
        this.x = x;
        this.y = y;
        lineCreator = new Basic( component, font, color );
        this.fontMetrics = component.getFontMetrics( font );
        init();
    }

    public PrimaryMultiLineTextGraphic( Component component, String[] text, Font font, int x, int y, Color foreground, int dx, int dy, Color background ) {
        super( component );
        this.text = text;
        this.x = x;
        this.y = y;
        lineCreator = new Shadowed( component, font, foreground, dx, dy, background );
        this.fontMetrics = component.getFontMetrics( font );
        init();
    }

    private void init() {
        int currentY = y;
        for( int i = 0; i < text.length; i++ ) {
            String s = text[i];
            PrimaryGraphic g = lineCreator.createLine( s, x, currentY );
            textGraphics.add( g );
            currentY += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        }
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
        for( int i = 0; i < textGraphics.size(); i++ ) {
            IPrimaryTextGraphic iTextGraphic = (IPrimaryTextGraphic)textGraphics.get( i );
            iTextGraphic.setPosition( x, y );
            y += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        }
        setBoundsDirty();
//        repaint();//this duplicates all the children's repaints.
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < textGraphics.size(); i++ ) {
            Graphic itg = (Graphic)textGraphics.get( i );
            itg.paint( g );
        }
    }

    protected Rectangle determineBounds() {
        Rectangle2D r = ( (PrimaryGraphic)textGraphics.get( 0 ) ).getBounds();
        for( int i = 1; i < textGraphics.size(); i++ ) {
            PrimaryGraphic iTextGraphic = (PrimaryGraphic)textGraphics.get( i );
            r = r.createUnion( iTextGraphic.getBounds() );
        }
        Rectangle intRect = new Rectangle( (int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight() );
        return intRect;
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

    public Point getPosition() {
        return new Point( x, y );
    }

    interface LineCreator {
        PrimaryGraphic createLine( String text, int x, int y );
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

        public PrimaryGraphic createLine( String text, int x, int y ) {
            return new PrimaryShadowTextGraphic( text, font, x, y, foregroundColor, dx, dy, backgroundColor, component );
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

        public PrimaryGraphic createLine( String text, int x, int y ) {
            return new PrimaryTextGraphic( component, font, text, color, x, y );
        }
    }

}