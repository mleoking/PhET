/**
 * Class: HelpItem
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: May 24, 2004
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.graphics.TextGraphic;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class HelpItem implements Graphic {
    public final static int TOP = 1;
    public final static int BOTTOM = 2;
    public final static int LEFT = 3;
    public final static int CENTER = 4;
    public final static int RIGHT = 5;

    ShapeGraphic backgroundGraphic;
    ArrayList textGraphics;
    int horizontalAlignment;
    int verticalAlignment;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
    private String text;
    private Color color = new Color( 156, 156, 0 );
    private Point2D.Double location;

    public HelpItem( String text, double x, double y ) {
        this( text, x, y, CENTER, CENTER );
    }

    public HelpItem( String text, double x, double y,
                       int horizontalAlignment, int verticalAlignment ) {
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.text = text;
        this.location = new Point2D.Double( x, y );
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
        RenderingHints orgHints = g.getRenderingHints();
        GraphicsUtil.setAntiAliasingOn( g );
        placeTextGraphics( g );
        if( backgroundGraphic != null ) {
            backgroundGraphic.paint( g );
        }
        for( int i = 0; i < textGraphics.size(); i++ ) {
            TextGraphic textGraphic = (TextGraphic)textGraphics.get( i );
            textGraphic.paint( g );
        }
        g.setRenderingHints( orgHints );
    }

    private void placeTextGraphics( Graphics2D g ) {
        if( textGraphics == null ) {
            FontMetrics fontMetrics = g.getFontMetrics( font );
            FontRenderContext fontRenderContext;
            textGraphics = new ArrayList();
            String[] sa = tokenizeString( text );
            int x = (int)( location.getX() + fontMetrics.getStringBounds( " ", g ).getWidth());
            for( int i = 0; i < sa.length; i++ ) {
                int y = (int)location.getY() + ( i + 1 ) * ( fontMetrics.getHeight() + fontMetrics.getLeading() );
                TextGraphic textGraphicShadow = new TextGraphic( sa[i], font, (float)x + 1, (float)y + 1, Color.black );
                textGraphics.add( textGraphicShadow );
                TextGraphic textGraphic = new TextGraphic( sa[i], font, (float)x, (float)y, color );
                textGraphics.add( textGraphic );
            }
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( );
        ApparatusPanel ap = new ApparatusPanel();
        frame.setContentPane( ap );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        HelpItem hi = new HelpItem( "Hi there,\nSailor!", 100, 0 );
        ap.addGraphic( hi );
        frame.setSize(  500, 500 );
        frame.setVisible( true );
    }
}
