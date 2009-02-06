package edu.colorado.phet.ohm1d.common.paint;

import java.awt.*;

public class TextPainter implements Painter {
    String text;
    float x;
    float y;
    Font f;
    Color c;

    public TextPainter( String text, float x, float y, Font f, Color c ) {
        this.c = c;
        this.text = text;
        this.x = x;
        this.y = y;
        this.f = f;
    }

    public void setPosition( double x, double y ) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public void paint( Graphics2D g ) {
        g.setColor( c );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        if ( f != null ) {
            g.setFont( f );
        }
        g.drawString( text, x, y );
    }

    public void setText( String text ) {
        this.text = text;
    }
}


