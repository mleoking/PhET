/** University of Colorado, PhET*/
package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;

/**
 * User: University of Colorado, PhET
 * Date: Jun 25, 2004
 * Time: 5:59:29 PM
 * Copyright (c) Jun 25, 2004 by University of Colorado, PhET
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
        fontMetrics = component.getFontMetrics( font );
        setLocation( x, y );
    }

    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            g.setFont( font );
            g.setColor( color );
            g.transform( getTransform() );
            g.drawString( text, 0, 0 );
            try {
                g.transform( getTransform().createInverse() );
            }
            catch( NoninvertibleTransformException e ) {
                e.printStackTrace();
            }
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
        return getTransform().createTransformedShape( bounds ).getBounds();
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
        if( this.text == text ) {
            return;
        }
        this.text = text;
        setBoundsDirty();
        autorepaint();
    }

    public void setColor( Color color ) {
        this.color = color;
        setBoundsDirty();
        autorepaint();
    }

    public void setFont( Font font ) {
        this.font = font;
        setBoundsDirty();
        autorepaint();
    }

}
