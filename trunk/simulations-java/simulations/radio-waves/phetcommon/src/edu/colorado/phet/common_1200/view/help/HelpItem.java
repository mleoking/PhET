/**
 * Class: HelpItem
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: May 24, 2004
 */
package edu.colorado.phet.common_1200.view.help;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common_1200.view.graphics.Graphic;
import edu.colorado.phet.common_1200.view.graphics.ShadowTextGraphic;

public class HelpItem implements Graphic {
    public final static int ABOVE = 1;
    public final static int BELOW = 2;
    public final static int LEFT = 3;
    public final static int CENTER = 4;
    public final static int RIGHT = 5;

    ArrayList shadowTextGraphics = new ArrayList();
    int horizontalAlignment;
    int verticalAlignment;
    private Font font = new PhetFont( Font.BOLD, 16 );
    private String text;
    private Point2D.Double location;
    private Color shadowColor;
    private Color foregroundColor;
    boolean inited = false;

    public HelpItem( String text, double x, double y ) {
        this( text, x, y, CENTER, CENTER );
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
    public HelpItem( String text, double x, double y,
                     int horizontalAlignment, int verticalAlignment ) {
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.text = text;
        this.location = new Point2D.Double( x, y );
        shadowColor = Color.black;
        foregroundColor = new Color( 156, 156, 0 );
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
        for( int i = 0; i < shadowTextGraphics.size(); i++ ) {
            ShadowTextGraphic textGraphic = (ShadowTextGraphic)shadowTextGraphics.get( i );
            textGraphic.paint( g );
        }
    }

    public void setShadowColor( Color shadowColor ) {
        this.shadowColor = shadowColor;
        for( int i = 0; i < shadowTextGraphics.size(); i++ ) {
            ShadowTextGraphic textGraphic = (ShadowTextGraphic)shadowTextGraphics.get( i );
            textGraphic.setShadowColor( shadowColor );
        }
    }

    public void setForegroundColor( Color foregroundColor ) {
        this.foregroundColor = foregroundColor;
        for( int i = 0; i < shadowTextGraphics.size(); i++ ) {
            ShadowTextGraphic shadowTextGraphic = (ShadowTextGraphic)shadowTextGraphics.get( i );
            shadowTextGraphic.setForegroundColor( foregroundColor );
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
            ShadowTextGraphic textGraphic = new ShadowTextGraphic( font, sa[i], 1, 1, x, y, foregroundColor, shadowColor );
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