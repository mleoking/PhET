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

/**
 * PhetTextGraphic is a graphic that draws single-line text.
 *
 * @author ?
 * @version $Revision$
 */
public class PhetTextGraphic2 extends PhetGraphic {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    /// Justification values
    public static final int NONE = 0;
    public static final int NORTH_WEST = 1;
    public static final int NORTH = 2;
    public static final int NORTH_EAST = 3;
    public static final int EAST = 4;
    public static final int SOUTH_EAST = 5;
    public static final int SOUTH = 6;
    public static final int SOUTH_WEST = 7;
    public static final int WEST = 8;
    public static final int CENTER = 9;

    // Defaults
    private static final Font DEFAULT_FONT = new Font( "Lucida Sans", Font.PLAIN, 12 );
    private static final String DEFAULT_TEXT = "";
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_JUSTIFICATION = NONE;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private Font font;
    private String text;
    private Color color;
    private FontMetrics fontMetrics;
    private int justification;

    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------

    /**
     * Creates a PhetTextGraphic at a specified location.
     *
     * @param component
     * @param font
     * @param text
     * @param color
     * @param x
     * @param y
     */
    public PhetTextGraphic2( Component component, Font font, String text, Color color, int x, int y ) {
        super( component );
        setFont( font ); // also sets fontMetrics
        this.text = text;
        this.color = color;
        setJustification( DEFAULT_JUSTIFICATION );
        setLocation( x, y );
    }

    /**
     * Creates a PhetTextGraphic at (0,0).
     * You can now set location easily with setLocation().
     *
     * @param component
     * @param font
     * @param text
     * @param color
     */
    public PhetTextGraphic2( Component component, Font font, String text, Color color ) {
        this( component, font, text, color, 0, 0 );
    }

    /**
     * Creates a default PhetTextGraphic at (0,0).
     *
     * @param component
     */
    public PhetTextGraphic2( Component component ) {
        this( component, DEFAULT_FONT, DEFAULT_TEXT, DEFAULT_COLOR );
    }

    /**
     * Provides for Java Bean conformance, should not be used by clients.
     */
    public PhetTextGraphic2() {
    }

    //----------------------------------------------------------------
    // Setters and getters 
    //----------------------------------------------------------------

    public void setText( String text ) {
        this.text = text;
        setJustification( justification );
        setBoundsDirty();
        autorepaint();
    }

    public String getText() {
        return text;
    }

    public void setColor( Color color ) {
        this.color = color;
        setBoundsDirty();
        autorepaint();
    }

    public Color getColor() {
        return color;
    }

    public void setFont( Font font ) {
        this.font = font;
        this.fontMetrics = getComponent().getFontMetrics( font );
        setJustification( justification );
        setBoundsDirty();
        autorepaint();
    }

    public Font getFont() {
        return font;
    }

    /**
     * Provides for Java Bean conformance, should not be used by clients.
     */
    public void setFontMetrics( FontMetrics fontMetrics ) {
        this.fontMetrics = fontMetrics;
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    /**
     * Convenience routine for setting the reference point.
     * <p/>
     * Locates the reference point by specifying a compass point
     * on a rectangle.  The top, left and right edges of the rectangle
     * correspond to the graphic's bounds. The bottom edge of the
     * rectangle is the text's baseline!
     * <p/>
     * By default, justification is NONE. If you set the justification
     * to something other than NONE, then the graphic will be
     * re-justified whenever you change the text or font.
     *
     * @param justification
     */
    public void setJustification( int justification ) {
        this.justification = justification;
        int ascentHeight = fontMetrics.getAscent();
        switch( justification ) {
            case NONE:
                // Do nothing, so we don't whack a custom registration point.
                break;
            case NORTH_WEST:
                setRegistrationPoint( 0, 0 );
                break;
            case NORTH:
                setRegistrationPoint( getWidth() / 2, 0 );
                break;
            case NORTH_EAST:
                setRegistrationPoint( getWidth(), 0 );
                break;
            case SOUTH_EAST:
                setRegistrationPoint( getWidth(), ascentHeight ); // on the baseline
                break;
            case SOUTH:
                setRegistrationPoint( getWidth() / 2, ascentHeight ); // on the baseline
                break;
            case SOUTH_WEST:
                setRegistrationPoint( 0, ascentHeight ); // on the baseline
                break;
            case EAST:
                setRegistrationPoint( getWidth(), ascentHeight / 2 );
                break;
            case WEST:
                setRegistrationPoint( 0, ascentHeight / 2 );
                break;
            case CENTER:
                setRegistrationPoint( getWidth() / 2, ascentHeight / 2 );
                break;
            default:
                throw new IllegalArgumentException( "invalid justification: " + justification );
        }
    }

    //----------------------------------------------------------------
    // PhetGraphic implementation
    //----------------------------------------------------------------

    public void paint( Graphics2D g2 ) {
        if( isVisible() ) {
            super.saveGraphicsState( g2 );
            super.updateGraphicsState( g2 );
            g2.setFont( font );
            g2.setColor( color );
            g2.transform( getNetTransform() );
            g2.drawString( text, 0, fontMetrics.getAscent() );
            super.restoreGraphicsState();
        }
    }

    protected Rectangle determineBounds() {
        if( text == null || text.equals( "" ) ) {
            return null;
        }
        int width = fontMetrics.stringWidth( text );//this ignores antialias and fractional metrics.
        int height = fontMetrics.getHeight();
        Rectangle bounds = new Rectangle( 0, 0, width, height );
        return getNetTransform().createTransformedShape( bounds ).getBounds();
    }
}