/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
<<<<<<< PhetTextGraphic.java
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
=======
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
>>>>>>> 1.18
 */
package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;

/**
 * PhetTextGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class PhetTextGraphic extends PhetGraphic {
    private Font font;
    private String text = "";
    private Color color;
    private FontMetrics fontMetrics;

    /**
     * Create a PhetTextGraphic at (0,0).  You can now set location easily with setLocation().
     *
     * @param component
     * @param font
     * @param text
     * @param color
     */
    public PhetTextGraphic( Component component, Font font, String text, Color color ) {
        this( component, font, text, color, 0, 0 );
    }

    public PhetTextGraphic( Component component, Font font, String text, Color color, int x, int y ) {
        super( component );
        setFont( font );
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
            int descent = fontMetrics.getDescent();
            g2.drawString( text, 0, -descent );
            super.restoreGraphicsState();
        }
    }

    protected Rectangle determineBounds() {
        if( text == null || text.equals( "" ) ) {
            return null;
        }
        int width = fontMetrics.stringWidth( text );//this ignores antialias and fractional metrics.
        int height = fontMetrics.getHeight();
        Rectangle bounds = new Rectangle( 0, -height, width, height );
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

    /**
     * Provided for Java Beans conformance
     */
    public PhetTextGraphic() {
    }

    public void setFontMetrics( FontMetrics fontMetrics ) {
        this.fontMetrics = fontMetrics;
    }
}
