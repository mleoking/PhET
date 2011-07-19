// Copyright 2002-2011, University of Colorado

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
package edu.colorado.phet.common.phetgraphics.view.phetgraphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * PhetTextGraphic
 * <p/>
 * NOTE! This class has some serious problems with justification and
 * registration points.  If these problems were fixed, lots of code
 * would be broken (including the charts package), so we chose not
 * to fix the problems.  Be forewarned that it may be difficult for
 * you to accurately position a PhetTextGraphic.  You should use
 * PhetTextGraphic2 instead.
 *
 * @author ?
 * @version $Revision$
 */
public class PhetTextGraphic extends PhetGraphic {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------
    public static final int NORTH_WEST = 1;
    public static final int NORTH = 2;
    public static final int NORTH_EAST = 3;
    public static final int EAST = 4;
    public static final int SOUTH_EAST = 5;
    public static final int SOUTH = 6;
    public static final int SOUTH_WEST = 7;
    public static final int WEST = 8;
    public static final int CENTER = 9;

    private Font font;
    private String text = "";
    private Color color;
    private FontMetrics fontMetrics;
    // Default justification
    private int justification = NORTH_WEST;

    /**
     * Create a PhetTextGraphic at (0,0).  You can now set location easily with setLocation().
     *
     * @param component
     * @param font
     * @param text
     * @param color
     * @deprecated use PhetTextGraphic2
     */
    public PhetTextGraphic( Component component, Font font, String text, Color color ) {
        this( component, font, text, color, 0, 0 );
    }

    /**
     * @param component
     * @param font
     * @param text
     * @param color
     * @param x
     * @param y
     * @deprecated use PhetTextGraphic2
     */
    public PhetTextGraphic( Component component, Font font, String text, Color color, int x, int y ) {
        super( component );
        setFont( font );
        this.text = text;
        this.color = color;
        this.fontMetrics = component.getFontMetrics( font );
        resetRegistrationPoint();
        setLocation( x, y );
    }

    //----------------------------------------------------------------
    // Setters and getters 
    //----------------------------------------------------------------

    /**
     * Locates the reference point for the text graphic by specifying a compass point on a rectangle
     * that bounds the font's tallest character, the text's first and last characters, and the text's
     * baseline, or its center, against which the text is to be justified.
     *
     * @param justification
     */
    public void setJustification( int justification ) {
        this.justification = justification;
        int characterHeight = fontMetrics.getAscent();
        Point p = new Point();
        switch( justification ) {
            case NORTH_WEST:
                p.setLocation( 0, -characterHeight );
                break;
            case NORTH:
                p.setLocation( (int) ( getBounds().getWidth() / 2 ), -characterHeight );
                break;
            case NORTH_EAST:
                p.setLocation( (int) getBounds().getWidth(), -characterHeight );
                break;
            case EAST:
                p.setLocation( (int) getBounds().getWidth(), -getHeight() + characterHeight / 2 );
                break;
            case SOUTH_EAST:
                p.setLocation( (int) getBounds().getWidth(), -getHeight() + characterHeight );
                break;
            case SOUTH:
                p.setLocation( (int) ( getBounds().getWidth() / 2 ), -getHeight() + characterHeight );
                break;
            case SOUTH_WEST:
                p.setLocation( 0, -getHeight() + characterHeight );
                break;
            case WEST:
                p.setLocation( 0, -getHeight() + characterHeight / 2 );
                break;
            case CENTER:
                p.setLocation( (int) ( getBounds().getWidth() / 2 ), -getHeight() + characterHeight / 2 );
                break;
            default:
                throw new RuntimeException( "Invalid justification specified" );
        }
        setRegistrationPoint( p );
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
        if ( this.text.equals( text ) || this.text == text ) {
            return;
        }
        this.text = text;
        setBoundsDirty();
        setJustification( justification );
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
        setJustification( justification );
        autorepaint();
    }

    /**
     * Sets the registration point so the text is located by the upper left corner of its bounding box
     */
    private void resetRegistrationPoint() {
        setRegistrationPoint( 0, -fontMetrics.getAscent() );
    }

    //----------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------

    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            super.saveGraphicsState( g2 );
            super.updateGraphicsState( g2 );
            g2.setFont( font );
            g2.setColor( color );
            g2.transform( getNetTransform() );
            int descent = fontMetrics.getDescent();
            g2.drawString( text, 0, -descent );
            super.restoreGraphicsState();
        }
    }

    protected Rectangle determineBounds() {
        if ( text == null || text.equals( "" ) ) {
            return null;
        }
        int width = fontMetrics.stringWidth( text );//this ignores antialias and fractional metrics.
        int height = fontMetrics.getHeight();
        Rectangle bounds = new Rectangle( 0, -height, width, height );
        return getNetTransform().createTransformedShape( bounds ).getBounds();
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
