/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.TextGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 2, 2004
 * Time: 3:15:07 PM
 * Copyright (c) Jun 2, 2004 by Sam Reid
 */
public class ShadowTextGraphic implements Graphic {
    TextGraphic foreground;
    TextGraphic background;

    public ShadowTextGraphic( Font font, String text, int dx, int dy, double x, double y, Color foregroundColor, Color backgroundColor ) {
        foreground = new TextGraphic( text, font, (float)x, (float)y, foregroundColor );
        background = new TextGraphic( text, font, (float)( x + dx ), (float)( y + dy ), backgroundColor );
    }

    public Rectangle2D getBounds() {
        Rectangle2D f = foreground.getBounds();
        Rectangle2D b = background.getBounds();
        if( f == null || b == null ) {
            return null;
        }
        return f.createUnion( b );
    }

    public void paint( Graphics2D g ) {
        background.paint( g );
        foreground.paint( g );
    }

    public void setState( Font font, String text, int dx, int dy, int x, int y, Color yellow, Color black ) {
        foreground.setFont( font );
        background.setFont( font );
        foreground.setText( text );
        background.setText( text );
        foreground.setLocation( x, y );
        background.setLocation( x + dx, y + dy );
        foreground.setPaint( yellow );
        background.setPaint( black );
    }
}
