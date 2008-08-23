/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.phetgraphics;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.Stack;

/**
 * PhetGraphics2D
 * <p/>
 * A decorator for Graphics2D that tracks whether its attribute have been changed. Used to
 * reduce overhead of getting and re-setting RenderingHints, Fonts, and clips.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhetGraphics2D extends Graphics2D {
    private Graphics2D wrappedGraphics;
    private Stack graphicsStates = new Stack();
    private StateDescriptor currentStateDescriptor = new StateDescriptor();

    public PhetGraphics2D( Graphics2D g2 ) {
        this.wrappedGraphics = g2;
    }

    public void pushState() {
        graphicsStates.push( currentStateDescriptor );
        currentStateDescriptor = new StateDescriptor();
    }

    public void popState() {
        currentStateDescriptor.restoreState();
        currentStateDescriptor = (StateDescriptor) graphicsStates.pop();
    }

    public void setAntialias( boolean antialias ) {
        wrappedGraphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );
    }

    public void setStrokePure() {
        wrappedGraphics.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
    }

    private class StateDescriptor {
        Font orgFont;
        RenderingHints orgRenderingHints;
        Shape orgClip;
        Color orgXORMode;
        Color orgBackground;
        Color orgColor;
        Paint orgPaint;
        Composite orgComposite;
        Stroke orgStroke;
        AffineTransform orgTransform;

        void restoreState() {
            if ( orgFont != null ) {
                wrappedGraphics.setFont( orgFont );
            }
            if ( orgRenderingHints != null ) {
                wrappedGraphics.setRenderingHints( orgRenderingHints );
            }
            if ( orgClip != null ) {
                wrappedGraphics.setClip( orgClip );
            }
            if ( orgXORMode != null ) {
                wrappedGraphics.setXORMode( orgXORMode );
            }
            if ( orgBackground != null ) {
                wrappedGraphics.setBackground( orgBackground );
            }
            if ( orgColor != null ) {
                wrappedGraphics.setColor( orgColor );
            }
            if ( orgPaint != null ) {
                wrappedGraphics.setPaint( orgPaint );
            }
            if ( orgComposite != null ) {
                wrappedGraphics.setComposite( orgComposite );
            }
            if ( orgStroke != null ) {
                wrappedGraphics.setStroke( orgStroke );
            }
            if ( orgTransform != null ) {
                wrappedGraphics.setTransform( orgTransform );
            }
        }
    }

    //----------------------------------------------------------------
    // Extended methods
    //----------------------------------------------------------------

    public void setBackground( Color color ) {
        if ( currentStateDescriptor.orgBackground == null ) {
            currentStateDescriptor.orgBackground = getBackground();
        }
        wrappedGraphics.setBackground( color );
    }

    public void setComposite( Composite comp ) {
        if ( currentStateDescriptor.orgComposite == null ) {
            currentStateDescriptor.orgComposite = getComposite();
        }
        wrappedGraphics.setComposite( comp );
    }

    public void setPaint( Paint paint ) {
        if ( currentStateDescriptor.orgPaint == null ) {
            currentStateDescriptor.orgPaint = getPaint();
        }
        wrappedGraphics.setPaint( paint );
    }

    public void setStroke( Stroke s ) {
        if ( currentStateDescriptor.orgColor == null ) {
            currentStateDescriptor.orgColor = getColor();
        }
        wrappedGraphics.setStroke( s );
    }

    public void setTransform( AffineTransform Tx ) {
        if ( currentStateDescriptor.orgTransform == null ) {
            currentStateDescriptor.orgTransform = getTransform();
        }
        wrappedGraphics.setTransform( Tx );
    }

    public void addRenderingHints( Map hints ) {
        if ( currentStateDescriptor.orgRenderingHints == null ) {
            currentStateDescriptor.orgRenderingHints = getRenderingHints();
        }
        wrappedGraphics.addRenderingHints( hints );
    }

    public void setRenderingHints( Map hints ) {
        if ( currentStateDescriptor.orgRenderingHints == null ) {
            currentStateDescriptor.orgRenderingHints = getRenderingHints();
        }
        wrappedGraphics.setRenderingHints( hints );
    }

    public void setRenderingHint( RenderingHints.Key hintKey, Object hintValue ) {
        if ( currentStateDescriptor.orgRenderingHints == null ) {
            currentStateDescriptor.orgRenderingHints = getRenderingHints();
        }
        wrappedGraphics.setRenderingHint( hintKey, hintValue );
    }

    // todo: what to do here?
    public void setPaintMode() {
        wrappedGraphics.setPaintMode();
    }

    public void clipRect( int x, int y, int width, int height ) {
        if ( currentStateDescriptor.orgClip == null ) {
            currentStateDescriptor.orgClip = getClip();
        }
        wrappedGraphics.clipRect( x, y, width, height );
    }

    public void setClip( int x, int y, int width, int height ) {
        if ( currentStateDescriptor.orgClip == null ) {
            currentStateDescriptor.orgClip = getClip();
        }
        wrappedGraphics.setClip( x, y, width, height );
    }

    public void setColor( Color c ) {
        if ( currentStateDescriptor.orgColor == null ) {
            currentStateDescriptor.orgColor = getColor();
        }
        wrappedGraphics.setColor( c );
    }

    // todo: what to do here?
    public void setXORMode( Color c1 ) {
        wrappedGraphics.setXORMode( c1 );
    }

    public void setFont( Font font ) {
        if ( currentStateDescriptor.orgFont == null ) {
            currentStateDescriptor.orgFont = getFont();
        }
        wrappedGraphics.setFont( font );
    }

    public void setClip( Shape clip ) {
        if ( currentStateDescriptor.orgClip == null ) {
            currentStateDescriptor.orgClip = getClip();
        }
        wrappedGraphics.setClip( clip );
    }

    public void rotate( double theta ) {
        if ( currentStateDescriptor.orgTransform == null ) {
            currentStateDescriptor.orgTransform = getTransform();
        }
        wrappedGraphics.rotate( theta );
    }

    public void scale( double sx, double sy ) {
        if ( currentStateDescriptor.orgTransform == null ) {
            currentStateDescriptor.orgTransform = getTransform();
        }
        wrappedGraphics.scale( sx, sy );
    }

    public void shear( double shx, double shy ) {
        if ( currentStateDescriptor.orgTransform == null ) {
            currentStateDescriptor.orgTransform = getTransform();
        }
        wrappedGraphics.shear( shx, shy );
    }

    public void translate( double tx, double ty ) {
        if ( currentStateDescriptor.orgTransform == null ) {
            currentStateDescriptor.orgTransform = getTransform();
        }
        wrappedGraphics.translate( tx, ty );
    }

    public void rotate( double theta, double x, double y ) {
        if ( currentStateDescriptor.orgTransform == null ) {
            currentStateDescriptor.orgTransform = getTransform();
        }
        wrappedGraphics.rotate( theta, x, y );
    }

    public void translate( int x, int y ) {
        if ( currentStateDescriptor.orgTransform == null ) {
            currentStateDescriptor.orgTransform = getTransform();
        }
        wrappedGraphics.translate( x, y );
    }

    public void transform( AffineTransform Tx ) {
        if ( currentStateDescriptor.orgTransform == null ) {
            currentStateDescriptor.orgTransform = getTransform();
        }
        wrappedGraphics.transform( Tx );
    }

    //----------------------------------------------------------------
    // Delegated methods
    //----------------------------------------------------------------

    public Color getBackground() {
        return wrappedGraphics.getBackground();
    }

    public Composite getComposite() {
        return wrappedGraphics.getComposite();
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return wrappedGraphics.getDeviceConfiguration();
    }

    public Paint getPaint() {
        return wrappedGraphics.getPaint();
    }

    public RenderingHints getRenderingHints() {
        return wrappedGraphics.getRenderingHints();
    }

    public void draw( Shape s ) {
        wrappedGraphics.draw( s );
    }

    public void fill( Shape s ) {
        wrappedGraphics.fill( s );
    }

    public Stroke getStroke() {
        return wrappedGraphics.getStroke();
    }

    public FontRenderContext getFontRenderContext() {
        return wrappedGraphics.getFontRenderContext();
    }

    public void drawGlyphVector( GlyphVector g, float x, float y ) {
        wrappedGraphics.drawGlyphVector( g, x, y );
    }

    public AffineTransform getTransform() {
        return wrappedGraphics.getTransform();
    }

    public void drawString( String s, float x, float y ) {
        wrappedGraphics.drawString( s, x, y );
    }

    public void drawString( String str, int x, int y ) {
        wrappedGraphics.drawString( str, x, y );
    }

    public void drawString( AttributedCharacterIterator iterator, float x, float y ) {
        wrappedGraphics.drawString( iterator, x, y );
    }

    public void drawString( AttributedCharacterIterator iterator, int x, int y ) {
        wrappedGraphics.drawString( iterator, x, y );
    }

    public boolean hit( Rectangle rect, Shape s, boolean onStroke ) {
        return wrappedGraphics.hit( rect, s, onStroke );
    }

    public void drawRenderedImage( RenderedImage img, AffineTransform xform ) {
        wrappedGraphics.drawRenderedImage( img, xform );
    }

    public void drawRenderableImage( RenderableImage img, AffineTransform xform ) {
        wrappedGraphics.drawRenderableImage( img, xform );
    }

    public void drawImage( BufferedImage img, BufferedImageOp op, int x, int y ) {
        wrappedGraphics.drawImage( img, op, x, y );
    }

    public Object getRenderingHint( RenderingHints.Key hintKey ) {
        return wrappedGraphics.getRenderingHint( hintKey );
    }

    public boolean drawImage( Image img, AffineTransform xform, ImageObserver obs ) {
        return wrappedGraphics.drawImage( img, xform, obs );
    }

    public void dispose() {
        wrappedGraphics.dispose();
    }

    public void clearRect( int x, int y, int width, int height ) {
        wrappedGraphics.clearRect( x, y, width, height );
    }

    public void drawLine( int x1, int y1, int x2, int y2 ) {
        wrappedGraphics.drawLine( x1, y1, x2, y2 );
    }

    public void drawOval( int x, int y, int width, int height ) {
        wrappedGraphics.drawOval( x, y, width, height );
    }

    public void fillOval( int x, int y, int width, int height ) {
        wrappedGraphics.fillOval( x, y, width, height );
    }

    public void fillRect( int x, int y, int width, int height ) {
        wrappedGraphics.fillRect( x, y, width, height );
    }

    public void copyArea( int x, int y, int width, int height, int dx, int dy ) {
        wrappedGraphics.copyArea( x, y, width, height, dx, dy );
    }

    public void drawArc( int x, int y, int width, int height, int startAngle, int arcAngle ) {
        wrappedGraphics.drawArc( x, y, width, height, startAngle, arcAngle );
    }

    public void drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
        wrappedGraphics.drawRoundRect( x, y, width, height, arcWidth, arcHeight );
    }

    public void fillArc( int x, int y, int width, int height, int startAngle, int arcAngle ) {
        wrappedGraphics.fillArc( x, y, width, height, startAngle, arcAngle );
    }

    public void fillRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
        wrappedGraphics.fillRoundRect( x, y, width, height, arcWidth, arcHeight );
    }

    public void drawPolygon( int xPoints[], int yPoints[], int nPoints ) {
        wrappedGraphics.drawPolygon( xPoints, yPoints, nPoints );
    }

    public void drawPolyline( int xPoints[], int yPoints[], int nPoints ) {
        wrappedGraphics.drawPolyline( xPoints, yPoints, nPoints );
    }

    public void fillPolygon( int xPoints[], int yPoints[], int nPoints ) {
        wrappedGraphics.fillPolygon( xPoints, yPoints, nPoints );
    }

    public Color getColor() {
        return wrappedGraphics.getColor();
    }

    public Font getFont() {
        return wrappedGraphics.getFont();
    }

    public Graphics create() {
        return wrappedGraphics.create();
    }

    public Rectangle getClipBounds() {
        return wrappedGraphics.getClipBounds();
    }

    public Shape getClip() {
        return wrappedGraphics.getClip();
    }

    public void clip( Shape s ) {
        wrappedGraphics.clip( s );
    }

    public FontMetrics getFontMetrics( Font f ) {
        return wrappedGraphics.getFontMetrics( f );
    }

    public boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer ) {
        return wrappedGraphics.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer );
    }

    public boolean drawImage( Image img, int x, int y, int width, int height, ImageObserver observer ) {
        return wrappedGraphics.drawImage( img, x, y, width, height, observer );
    }

    public boolean drawImage( Image img, int x, int y, ImageObserver observer ) {
        return wrappedGraphics.drawImage( img, x, y, observer );
    }

    public boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer ) {
        return wrappedGraphics.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer );
    }

    public boolean drawImage( Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer ) {
        return wrappedGraphics.drawImage( img, x, y, width, height, bgcolor, observer );
    }

    public boolean drawImage( Image img, int x, int y, Color bgcolor, ImageObserver observer ) {
        return wrappedGraphics.drawImage( img, x, y, bgcolor, observer );
    }

}
