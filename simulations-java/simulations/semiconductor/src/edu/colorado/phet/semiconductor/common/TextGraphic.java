/*, 2003.*/
package edu.colorado.phet.semiconductor.common;

import java.awt.*;

import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;

/**
 * User: Sam Reid
 * Date: Mar 1, 2004
 * Time: 2:04:28 PM
 */
public class TextGraphic implements Graphic {
    float x;
    float y;
    Font font;
    Color color;
    private String text;

    public TextGraphic( double x, double y, Font font, Color color, String text ) {
        this.text = text;
        this.x = (float) x;
        this.y = (float) y;
        this.font = font;
        this.color = color;
    }

    public void paint( Graphics2D g ) {
        g.setColor( color );
        g.setFont( font );
        g.drawString( text, x, y );
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
    }

}
