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

import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;

/**
 * PhetTextGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class PhetTextGraphic extends PhetGraphic {
    private Font font;
    private String text;
    private Color color;
    private FontMetrics fontMetrics;

    public PhetTextGraphic( Component component, Font font, String text, Color color, int x, int y ) {
        super( component );
        this.font = font;
        this.text = text;
        this.color = color;
        this.fontMetrics = component.getFontMetrics( font );
        resetRegistrationPoint();
        setLocation( x, y );
    }

    private void resetRegistrationPoint() {
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int leading = fontMetrics.getLeading();
        int height = ascent + descent + leading;
        setRegistrationPoint( 0, -height );
    }

    public void paint( Graphics2D g2 ) {
        if( isVisible() ) {
            super.saveGraphicsState( g2 );
            RenderingHints hints = getRenderingHints();
            if( hints != null ) {
                g2.setRenderingHints( hints );
            }
            g2.setFont( font );
            g2.setColor( color );
            g2.transform( getNetTransform() );
            g2.drawString( text, 0, 0 );
            super.restoreGraphicsState();
        }
    }

    protected Rectangle determineBounds() {
        if( text == null || text.equals( "" ) ) {
            return null;
        }
        int width = fontMetrics.stringWidth( text );//this ignores antialias and fractional metrics.
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int leading = fontMetrics.getLeading();
        Point location = new Point( 0, 0 );
        Rectangle bounds = new Rectangle( location.x, location.y - ascent + leading, width, ascent + descent + leading );
        return getNetTransform().createTransformedShape( bounds ).getBounds();
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

    public void setText( String text ) {
        if( this.text.equals( text ) || this.text == text ) {
            return;
        }
        this.text = text;
        setBoundsDirty();
        autorepaint();
    }

    public void setColor( Color color ) {
        this.color = color;//TODO we need to compare to current color to avoid potential autorepaints.
        setBoundsDirty();
        autorepaint();
    }

    public void setFont( Font font ) {
        this.font = font;//TODO we need to compare to current Font to avoid potential autorepaints.
        this.fontMetrics = getComponent().getFontMetrics( font );
        setBoundsDirty();
        autorepaint();
        //TODO should we keep whatever registration point the user may have set?
        resetRegistrationPoint();
    }

}
