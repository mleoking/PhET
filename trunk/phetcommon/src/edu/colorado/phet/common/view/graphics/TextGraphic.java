/**
 * Class: TextGraphic
 * Package: edu.colorado.phet.common.view.graphics
 * Author: Another Guy
 * Date: May 11, 2004
 */
package edu.colorado.phet.common.view.graphics;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class TextGraphic implements BoundedGraphic {
    private String text;
    private Font font;
    float x;
    float y;
    Paint paint;
    private FontRenderContext fontRenderContext;

    public TextGraphic( String text, Font font, float x, float y ) {
        this( text, font, x, y, Color.black );
    }

    public TextGraphic( String text, Font font, float x, float y, Paint paint ) {
        this.text = text;
        this.font = font;
        this.x = x;
        this.y = y;
        this.paint = paint;
    }

    public void paint( Graphics2D g ) {
        this.fontRenderContext = g.getFontRenderContext();
        g.setPaint( paint );
        g.setFont( font );
        g.drawString( text, x, y );
    }

    public boolean contains( int x, int y ) {
        if( fontRenderContext == null ) {
            return false;
        }
        else {
            Rectangle2D bounds = font.getStringBounds( text, fontRenderContext );
            return bounds.contains( x, y );
        }
    }
}
