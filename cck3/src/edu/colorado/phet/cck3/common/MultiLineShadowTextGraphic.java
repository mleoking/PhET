/**
 * Class: HelpItem
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: May 24, 2004
 */
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShadowTextGraphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MultiLineShadowTextGraphic implements Graphic {
    ShapeGraphic backgroundGraphic;
    ArrayList shadowTextGraphics = new ArrayList();
    private Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
    private String text;
    private FontRenderContext fontRenderContext;
    private FontMetrics fontMetrics;
    private Point2D.Double location;
    private Color shadowColor;
    private Color foregroundColor;
    boolean inited = false;
    Rectangle2D bounds2d = new Rectangle2D.Double();
    private boolean boundsDirty = true;

    public MultiLineShadowTextGraphic( String text, double x, double y, FontRenderContext fontRenderContext, FontMetrics fontMetrics ) {
        this.text = text;
        this.fontRenderContext = fontRenderContext;
        this.fontMetrics = fontMetrics;
        this.location = new Point2D.Double( x, y );
        shadowColor = Color.black;
        foregroundColor = new Color( 156, 156, 0 );
        init();
    }

    private String[] tokenizeString( String inputText ) {
        StringTokenizer st = new StringTokenizer( inputText, "\n" );
        ArrayList list = new ArrayList();
        while( st.hasMoreTokens() ) {
            String next = st.nextToken();
            list.add( next );
        }
        return (String[])list.toArray( new String[0] );
    }

    public void paint( Graphics2D g ) {
        if( backgroundGraphic != null ) {
            backgroundGraphic.paint( g );
        }

        for( int i = 0; i < shadowTextGraphics.size(); i++ ) {
            ShadowTextGraphic textGraphic = (ShadowTextGraphic)shadowTextGraphics.get( i );
            if( boundsDirty ) {
                Rectangle2D r1 = textGraphic.getBounds2D();
                if( r1 != null ) {
                    if( i == 0 ) {
                        bounds2d.setFrame( r1 );
                    }
                    else {
                        bounds2d = bounds2d.createUnion( r1 );
                    }
                    boundsDirty = false;
                }
            }
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

    private void init() {
        shadowTextGraphics = new ArrayList();
        String[] sa = tokenizeString( text );
        int x = (int)( location.getX() + font.getStringBounds( " ", fontRenderContext ).getWidth() );
        for( int i = 0; i < sa.length; i++ ) {
            int y = (int)location.getY() + ( i + 1 ) * ( fontMetrics.getHeight() + fontMetrics.getLeading() );
            ShadowTextGraphic textGraphic = new ShadowTextGraphic( font, sa[i], 1, 1, x, y, foregroundColor, shadowColor );
            shadowTextGraphics.add( textGraphic );
        }
    }

    public void relayout() {
        int x = (int)( location.getX() + font.getStringBounds( " ", fontRenderContext ).getWidth() );
        for( int i = 0; i < shadowTextGraphics.size(); i++ ) {
            int y = (int)location.getY() + ( i + 1 ) * ( fontMetrics.getHeight() + fontMetrics.getLeading() );
            ShadowTextGraphic shadowTextGraphic = (ShadowTextGraphic)shadowTextGraphics.get( i );
            shadowTextGraphic.setLocation( x, y );
        }
    }

    public void setLocation( double x, double y ) {
        this.location.setLocation( x, y );
        boundsDirty = true;
        relayout();
    }

    public Rectangle2D getBounds2D() {
        return bounds2d;
    }

    public Rectangle getBounds() {
        return new Rectangle( (int)bounds2d.getX(), (int)bounds2d.getY(), (int)bounds2d.getWidth(), (int)bounds2d.getHeight() );
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

}
