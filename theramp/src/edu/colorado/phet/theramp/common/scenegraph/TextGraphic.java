/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.awt.font.FontRenderContext;
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
    private Font font;

    public TextGraphic( String text ) {
        this.text = text;
    }

    public void paint( Graphics2D graphics2D ) {
        super.setup( graphics2D );
        this.fontRenderContext = graphics2D.getFontRenderContext();
        this.font = graphics2D.getFont();
        Rectangle2D stringBounds = getStringBounds();
        graphics2D.drawString( text, -(float)stringBounds.getX(), -(float)stringBounds.getY() );

        super.restore( graphics2D );
    }

    private Rectangle2D getStringBounds() {
        if( font == null || fontRenderContext == null || text == null ) {
            return new Rectangle2D.Double();
        }
        else {
            return font.getStringBounds( text, fontRenderContext );
        }
    }

    public Rectangle2D getLocalBounds() {
        if( font == null || fontRenderContext == null || text == null ) {
            return new Rectangle2D.Double();
        }
        else {
            Rectangle2D stringBounds = font.getStringBounds( text, fontRenderContext );
            stringBounds = new Rectangle2D.Double( 0, 0, stringBounds.getWidth(), stringBounds.getHeight() );
            return stringBounds;
        }
    }

}
