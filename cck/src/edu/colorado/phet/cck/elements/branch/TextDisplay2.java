/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 4, 2003
 * Time: 12:05:58 AM
 * Copyright (c) Sep 4, 2003 by Sam Reid
 */
public class TextDisplay2 implements Graphic {
    String text;
    Point target;
    Point src;

    Font font;
    Color color;
    boolean visible;

    public TextDisplay2() {
        font = new Font( "Lucida Sans", 0, 24 );
        color = Color.black;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public void setLocation( int x, int y ) {
        setLocation( new Point( x, y ) );
    }

    public void setLocation( Point src ) {
        this.src = src;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public void paint( Graphics2D g ) {
        if( !visible ) {
            return;
        }
        g.setFont( font );
        g.setColor( color );
        g.drawString( text, src.x, src.y );
    }
}
