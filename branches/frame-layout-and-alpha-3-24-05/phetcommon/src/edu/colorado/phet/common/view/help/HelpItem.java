/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * HelpItem
 *
 * @author ?
 * @version $Revision$
 */
public class HelpItem extends PhetGraphic {
    public final static int ABOVE = 1;
    public final static int BELOW = 2;
    public final static int LEFT = 3;
    public final static int CENTER = 4;
    public final static int RIGHT = 5;

    PhetShapeGraphic backgroundGraphic;
    ArrayList shadowTextGraphics = new ArrayList();
    int horizontalAlignment;
    int verticalAlignment;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
    private String text;
    private Point2D.Double location;
    private Color shadowColor;
    private Color foregroundColor;
    boolean inited = false;

    /**
     * @param component
     * @param text
     * @param x
     * @param y
     */
    public HelpItem( Component component, String text, double x, double y ) {
        this( component, text, x, y, CENTER, CENTER );
    }

    /**
     *
     * @param component
     * @param text
     * @param location
     */
    public HelpItem( Component component, String text, Point2D location ) {
        this( component, text, location.getX(), location.getX() );
    }

    /**
     * 
     * @param component
     * @param text
     * @param location
     * @param horizontalAlignment
     * @param verticalAlignment
     */
    public HelpItem( Component component, String text, Point2D location,
                     int horizontalAlignment, int verticalAlignment ) {
        this( component, text, location.getX(), location.getY(), horizontalAlignment, verticalAlignment );
    }

    /**
     * @param text
     * @param x
     * @param y
     * @param horizontalAlignment Specifies if the help item will be displayed to
     *                            the LEFT or RIGHT of the specified x coordinate
     * @param verticalAlignment   Specifies if the help item will be displayed ABOVE
     *                            or BELOW the specified y coordinate
     */
    public HelpItem( Component component, String text, double x, double y,
                     int horizontalAlignment, int verticalAlignment ) {
        super( component );
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.text = text;
        this.location = new Point2D.Double( x, y );
        shadowColor = Color.black;
        foregroundColor = new Color( 156, 156, 0 );
        setIgnoreMouse( true );
    }

    protected Rectangle determineBounds() {
        return getComponent().getBounds();//TODO this should return the correct bounds
    }

    public void setLocation( int x, int y ) {
        location.setLocation( x, y );
        inited = false;
    }

    public static String[] tokenizeString( String inputText ) {
        StringTokenizer st = new StringTokenizer( inputText, "\n" );
        ArrayList list = new ArrayList();
        while( st.hasMoreTokens() ) {
            String next = st.nextToken();
            list.add( next );
        }
        return (String[])list.toArray( new String[0] );
    }

    public void paint( Graphics2D g ) {
        if( !inited ) {
            init( g );
            inited = true;
        }
        if( backgroundGraphic != null ) {
            backgroundGraphic.paint( g );
        }
        for( int i = 0; i < shadowTextGraphics.size(); i++ ) {
            PhetShadowTextGraphic textGraphic = (PhetShadowTextGraphic)shadowTextGraphics.get( i );
            textGraphic.paint( g );
        }
    }

    public void setShadowColor( Color shadowColor ) {
        this.shadowColor = shadowColor;
        for( int i = 0; i < shadowTextGraphics.size(); i++ ) {
            PhetShadowTextGraphic textGraphic = (PhetShadowTextGraphic)shadowTextGraphics.get( i );
            textGraphic.setShadowColor( shadowColor );
        }
    }

    public void setForegroundColor( Color foregroundColor ) {
        this.foregroundColor = foregroundColor;
        for( int i = 0; i < shadowTextGraphics.size(); i++ ) {
            PhetShadowTextGraphic shadowTextGraphic = (PhetShadowTextGraphic)shadowTextGraphics.get( i );
            shadowTextGraphic.setColor( foregroundColor );
        }
    }

    private void init( Graphics2D g ) {
        FontMetrics fontMetrics = g.getFontMetrics( font );

        shadowTextGraphics = new ArrayList();
        String[] sa = tokenizeString( text );

        int x = 0;
        double maxStrLen = 0;
        switch( horizontalAlignment ) {
            case HelpItem.LEFT:
                maxStrLen = getMaxStrLen( sa, g, fontMetrics );
                x = (int)( location.getX() - maxStrLen - fontMetrics.getStringBounds( " ", g ).getWidth() );
                break;
            case HelpItem.RIGHT:
                x = (int)( location.getX() + fontMetrics.getStringBounds( " ", g ).getWidth() );
                break;
            case HelpItem.CENTER:
                maxStrLen = getMaxStrLen( sa, g, fontMetrics );
                x = (int)( location.getX() - maxStrLen / 2 );
                break;
        }

        double yBase = 0;
        switch( verticalAlignment ) {
            case HelpItem.ABOVE:
                yBase = -sa.length * ( fontMetrics.getHeight() + fontMetrics.getLeading() );
                break;
            case HelpItem.BELOW:
                yBase = 0;
                break;
            case HelpItem.CENTER:
                yBase = -( sa.length + 1 ) * ( fontMetrics.getHeight() + fontMetrics.getLeading() ) / 2;
                break;
        }

        for( int i = 0; i < sa.length; i++ ) {
            int y = (int)yBase + (int)location.getY() + ( i + 1 ) * ( fontMetrics.getHeight() + fontMetrics.getLeading() );
            PhetShadowTextGraphic textGraphic = new PhetShadowTextGraphic( getComponent(), font, sa[i], foregroundColor, 1, 1, shadowColor );
            textGraphic.setLocation( x, y );
            shadowTextGraphics.add( textGraphic );
        }
    }

    private double getMaxStrLen( String[] sa, Graphics2D g, FontMetrics fontMetrics ) {
        double maxStrLen = 0;
        for( int i = 0; i < sa.length; i++ ) {
            String s = sa[i];
            double strLen = fontMetrics.getStringBounds( s, g ).getWidth();
            maxStrLen = strLen > maxStrLen ? strLen : maxStrLen;
        }
        return maxStrLen;
    }
}