/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:15:58 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class TextGraphic extends AbstractGraphic {
    private String text;
    private FontRenderContext fontRenderContext;

    public TextGraphic( String text ) {
        this( text, null );
    }

    public TextGraphic( String text, Color color ) {
        this( text, color, new Font( "Lucida Sans", Font.PLAIN, 14 ) );
    }

    public TextGraphic( String text, Color color, Font font ) {
        this.text = text;
        setColor( color );
        setFont( font );
        fontRenderContext = new FontRenderContext( new AffineTransform(), true, false );
    }

    public void paint( Graphics2D graphics2D ) {
        super.setup( graphics2D );
        this.fontRenderContext = graphics2D.getFontRenderContext();
        Rectangle2D stringBounds = getStringBounds();
        graphics2D.drawString( text, -(float)stringBounds.getX(), -(float)stringBounds.getY() );

        super.restore( graphics2D );
    }

    private Rectangle2D getStringBounds() {
        if( super.getFont() == null || fontRenderContext == null || text == null ) {
            return new Rectangle2D.Double();
        }
        else {
            return getFont().getStringBounds( text, fontRenderContext );
        }
    }


    public Rectangle2D getLocalBounds() {
        if( getFont() == null || fontRenderContext == null || text == null ) {
            return new Rectangle2D.Double();
        }
        else {
            Rectangle2D stringBounds = getFont().getStringBounds( text, fontRenderContext );
            stringBounds = new Rectangle2D.Double( 0, 0, stringBounds.getWidth(), stringBounds.getHeight() );
            return stringBounds;
        }
    }


    public void setText( String text ) {
        this.text = text;
    }
}
