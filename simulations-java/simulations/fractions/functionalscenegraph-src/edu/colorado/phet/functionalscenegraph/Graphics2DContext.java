package edu.colorado.phet.functionalscenegraph;

import fj.Effect;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public class Graphics2DContext implements DrawableGraphicsContext {
    private Graphics2D g;

    public Graphics2DContext( final Graphics2D g ) {
        this.g = g;
    }

    @Override public void draw( final Shape shape ) {
        g.draw( shape );
    }

    @Override public void drawString( final String text, final int x, final int y ) {
        g.drawString( text, x, y );
    }

    @Override public void fill( final Shape shape ) {
        g.fill( shape );
    }

    @Override public Paint getPaint() {
        return g.getPaint();
    }

    @Override public void setPaint( final Paint paint ) {
        g.setPaint( paint );
    }

    @Override public void setFont( final Font font ) {
        g.setFont( font );
    }

    @Override public Font getFont() {
        return g.getFont();
    }

    @Override public FontRenderContext getFontRenderContext() {
        return g.getFontRenderContext();
    }

    @Override public AffineTransform getTransform() {
        return g.getTransform();
    }

    @Override public void transform( final AffineTransform transform ) {
        g.transform( transform );
    }

    @Override public void setTransform( final AffineTransform orig ) {
        g.setTransform( orig );
    }

    @Override public void setDragHandler( final Effect<Vector2D> effect ) {
    }
}