/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.util.Arrays;

/**
 * PhetMultiLineTextGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class PhetMultiLineTextGraphic extends CompositePhetGraphic {
    private LineCreator lineCreator;
    private String[] text;
    private FontMetrics fontMetrics;

    public PhetMultiLineTextGraphic( Component component, Font font, String[] text, Color color ) {
        this( component, font, text, new Basic( component, font, color ) );
    }

    public PhetMultiLineTextGraphic( Component component, Font font, String[] text, Color color, int dx, int dy, Color backgroundColor ) {
        this( component, font, text, new Shadowed( component, font, color, dx, dy, backgroundColor ) );
    }

    public PhetMultiLineTextGraphic( Component component, Font font, String[] text, LineCreator lineCreator ) {
        super( component );
        this.text = text;
        this.lineCreator = lineCreator;
        this.fontMetrics = component.getFontMetrics( font );
        init();
    }

    /**
     * @deprecated
     */
    public PhetMultiLineTextGraphic( Component component, String[] text, Font font, int x, int y, Color color ) {
        this( component, text, font, x, y, new Basic( component, font, color ) );
    }

    /**
     * @deprecated
     */
    public PhetMultiLineTextGraphic( Component component, String text, Font font, int x, int y, Color foreground, int dx, int dy, Color background ) {
        this( component, new String[]{text}, font, x, y, foreground, dx, dy, background );
    }

    /**
     * @deprecated
     */
    public PhetMultiLineTextGraphic( Component component, String[] text, Font font, int x, int y, Color foreground, int dx, int dy, Color background ) {
        this( component, text, font, x, y, new Shadowed( component, font, foreground, dx, dy, background ) );
    }

    /**
     * @deprecated
     */
    public PhetMultiLineTextGraphic( Component component, String[] text, Font font, int x, int y, LineCreator lineCreator ) {
        super( component );
        this.text = text;
        this.lineCreator = lineCreator;
        this.fontMetrics = component.getFontMetrics( font );
        init();
        setLocation( x, y );
    }

    private void init() {
        clear();
        int currentY = 0;
        for( int i = 0; i < text.length; i++ ) {
            String s = text[i];
            PhetGraphic g = lineCreator.createLine( s, 0, currentY );
            addGraphic( g );
            currentY += fontMetrics.getDescent() + fontMetrics.getLeading() + fontMetrics.getAscent();
        }
    }

    public Point getLeftCenter() {
        return RectangleUtils.getLeftCenter( getBounds() );
    }

    public Point getRightCenter() {
        return RectangleUtils.getRightCenter( getBounds() );
    }

    public void setText( String[] text ) {
        if( Arrays.asList( this.text ).equals( Arrays.asList( text ) ) ) {
            return;
        }
        this.text = text;
        init();
        setBoundsDirty();
        autorepaint();
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
            PhetShadowTextGraphic pstg = new PhetShadowTextGraphic( component, font, text, foregroundColor, dx, dy, backgroundColor );
            pstg.setLocation( x, y );
            return pstg;
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


    ////////////////////////////////////////////
    // Persistence support
    //
    /**
     * Provided for Java Beans conformance
     */
    public PhetMultiLineTextGraphic() {
    }

}