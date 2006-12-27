/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics2;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 17, 2003
 * Time: 1:46:03 AM
 * Copyright (c) Sep 17, 2003 by Sam Reid
 */
public class TextGraphic implements Graphic {
    String text;
    Font font;
    Color color;
    int x;
    int y;
    boolean initposition;

    public TextGraphic( Font font, Color color ) {
        this.font = font;
        this.color = color;
    }

    public void paint( Graphics2D g ) {
        if( initposition && text != null ) {
            g.setColor( color );
            g.setFont( font );
            g.drawString( text, x, y );
        }
    }

    public void setText( String text ) {
        this.text = text;
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
        initposition = true;
    }
}
