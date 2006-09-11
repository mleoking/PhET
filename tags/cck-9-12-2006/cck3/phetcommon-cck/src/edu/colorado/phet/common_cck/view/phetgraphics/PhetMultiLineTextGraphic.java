/** Sam Reid*/
package edu.colorado.phet.common_cck.view.phetgraphics;

import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;

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
public class PhetMultiLineTextGraphic extends PhetGraphic {
    private ArrayList textGraphics = new ArrayList();
    private LineCreator lineCreator;
    private String[] text;
    private int x;
    private int y;
    private FontMetrics fontMetrics;

    public PhetMultiLineTextGraphic( Component component, String[] text, Font font, int x, int y, Color color ) {
        this( component, text, font, x, y, new Basic( component, font, color ) );
    }

    public PhetMultiLineTextGraphic( Component component, String text, Font font, int x, int y, Color foreground, int dx, int dy, Color background ) {
        this( component, new String[]{text}, font, x, y, foreground, dx, dy, background );
    }

    public PhetMultiLineTextGraphic( Component component, String[] text, Font font, int x, int y, Color foreground, int dx, int dy, Color background ) {
        this( component, text, font, x, y, new Shadowed( component, font, foreground, dx, dy, background ) );
    }

    public PhetMultiLineTextGraphic( Component component, String[] text, Font font, int x, int y, LineCreator lineCreator ) {
        super( component );
        this.text = text;
        this.x = x;
        this.y = y;
        this.lineCreator = lineCreator;
        this.fontMetrics = component.getFontMetrics( font );
        init();
    }

    private void init() {
        int currentY = y;
        boolean visible = super.isVisible();
        for( int i = 0; i < text.length; i++ ) {
            String s = text[i];
            PhetGraphic g = lineCreator.createLine( s, x, currentY );
            g.setVisible( visible );
            textGraphics.add( g );
            currentY += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        }
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        for( int i = 0; i < textGraphics.size(); i++ ) {
            IPhetTextGraphic iPrimaryTextGraphic = (IPhetTextGraphic)textGraphics.get( i );
            iPrimaryTextGraphic.setVisible( visible );
        }
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
        for( int i = 0; i < textGraphics.size(); i++ ) {
            IPhetTextGraphic iTextGraphic = (IPhetTextGraphic)textGraphics.get( i );
            iTextGraphic.setPosition( x, y );
            y += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        }
        setBoundsDirty();
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < textGraphics.size(); i++ ) {
            Graphic itg = (Graphic)textGraphics.get( i );
            itg.paint( g );
        }
    }

    protected Rectangle determineBounds() {
        if( text.length == 0 ) {
            return null;
        }
        Rectangle2D r = ( (PhetGraphic)textGraphics.get( 0 ) ).getBounds();
        for( int i = 1; i < textGraphics.size(); i++ ) {
            PhetGraphic iTextGraphic = (PhetGraphic)textGraphics.get( i );
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

    public Point getLeftCenter() {
        return RectangleUtils.getLeftCenter( getBounds() );
    }

    public Point getRightCenter() {
        return RectangleUtils.getRightCenter( getBounds() );
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
        setBoundsDirty();
        repaint();
    }

    public void setText( String text ) {
        setText( new String[]{text} );
    }

    /**
     * An interface for creating lines of text.  Provide your own implementation if you want.
     */
    public interface LineCreator {
        PhetGraphic createLine( String text, int x, int y );
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

        public PhetGraphic createLine( String text, int x, int y ) {
            return new PhetShadowTextGraphic( component, text, font, x, y, foregroundColor, dx, dy, backgroundColor );
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

        public PhetGraphic createLine( String text, int x, int y ) {
            return new PhetTextGraphic( component, font, text, color, x, y );
        }
    }

}