/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics2;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 17, 2003
 * Time: 1:46:03 AM
 * Copyright (c) Sep 17, 2003 by Sam Reid
 */
public class TextGraphicOnBackground implements Graphic {
    String text;
    Font font;
    Color color;
    private Color background;
    private int insetX;
    private int insetY;
    int x;
    int y;
    boolean initposition;
    private Rectangle bounds;
    private int textHeight;

    public TextGraphicOnBackground( Font font, Color color, Color background, int insetX, int insetY ) {
        this.font = font;
        this.color = color;
        this.background = background;
        this.insetX = insetX;
        this.insetY = insetY;
    }

    public void paint( Graphics2D g ) {
        if( initposition && text != null ) {
            if( bounds == null ) {
                Rectangle2D abounds = font.getStringBounds( text, g.getFontRenderContext() );
                bounds = new Rectangle( (int)abounds.getX(), (int)abounds.getY(), (int)abounds.getWidth(), (int)abounds.getHeight() );
                bounds.x = -insetX + x;
                bounds.y = -insetY + y - bounds.height;
                bounds.width += insetX * 2;
                bounds.height += insetY * 2;
                textHeight = bounds.height;
            }

            g.setColor( background );
            g.fillRoundRect( bounds.x, bounds.y, bounds.width, bounds.height, insetX, insetY );

            g.setColor( color );
            g.setFont( font );

            g.drawString( text, x, y );
        }
    }

    public int getBoundsHeight() {
        return textHeight;
    }

    public void setText( String text ) {
        this.text = text;
        bounds = null;
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
        initposition = true;
        bounds = null;
    }
}
