/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics.positioned;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Apr 18, 2003
 * Time: 8:48:13 PM
 * Copyright (c) Apr 18, 2003 by Sam Reid
 */
public class PositionedTextGraphic implements PositionedGraphic {
    Font f;
    String text;
    FontRenderContext frc;
    Color c;

    public PositionedTextGraphic( Font f, String text, Color c ) {
        this.c = c;
        this.f = f;
        this.text = text;
    }

    public void paint( Graphics2D g, int x, int y ) {
        g.setFont( f );
        g.setColor( c );
        g.drawString( text, x, y );
        if( frc == null ) {
            this.frc = g.getFontRenderContext();
        }
    }

    public void setText( String text ) {
        this.text = text;
    }

    public boolean containsRelativePoint( int xrel, int yrel ) {
        if( frc == null ) {
            return false;
        }
        Shape s = f.createGlyphVector( frc, text ).getOutline();
        return s.contains( xrel, yrel );
    }

    public Rectangle2D getStringBounds( FontRenderContext frc ) {
        TextLayout tl = new TextLayout( text, f, frc );
        return tl.getBounds();
//        return f.getStringBounds(text,frc);
    }
}
