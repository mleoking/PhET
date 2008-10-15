package edu.colorado.phet.common_1200.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 2, 2004
 * Time: 3:15:07 PM
 */
public class ShadowTextGraphic implements Graphic {
    TextGraphic foreground;
    TextGraphic background;
    private int dx;
    private int dy;

    public ShadowTextGraphic( Font font, String text, int dx, int dy, double x, double y, Color foregroundColor, Color backgroundColor ) {
        foreground = new TextGraphic( text, font, (float)x, (float)y, foregroundColor );
        background = new TextGraphic( text, font, (float)( x + dx ), (float)( y + dy ), backgroundColor );
        this.dx = dx;
        this.dy = dy;
    }

    public Rectangle2D getBounds2D() {
        Rectangle2D f = foreground.getBounds2D();
        Rectangle2D b = background.getBounds2D();
        if( f == null || b == null ) {
            return null;
        }
        return f.createUnion( b );
    }

    public void paint( Graphics2D g ) {
        background.paint( g );
        foreground.paint( g );
    }

    public void setState( Font font, String text, int dx, int dy, int x, int y, Color foregroundColor, Color backgroundColor ) {
        foreground.setFont( font );
        background.setFont( font );
        foreground.setText( text );
        background.setText( text );
        foreground.setLocation( x, y );
        background.setLocation( x + dx, y + dy );
        foreground.setPaint( foregroundColor );
        background.setPaint( backgroundColor );
    }

    public void setShadowColor( Color shadowColor ) {
        background.setPaint( shadowColor );
    }

    public void setForegroundColor( Color foregroundColor ) {
        foreground.setPaint( foregroundColor );
    }

    public void setLocation( int x, int y ) {
        foreground.setLocation( x, y );
        background.setLocation( x + dx, y + dy );
    }
}
