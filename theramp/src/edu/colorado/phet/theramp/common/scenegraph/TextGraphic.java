/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.awt.font.FontRenderContext;

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
        graphics2D.drawString( text, 0, 0 );
        this.fontRenderContext = graphics2D.getFontRenderContext();
        this.font = graphics2D.getFont();
        super.restore( graphics2D );
    }

    public boolean contains( double x, double y ) {
        return font != null && fontRenderContext != null && font.getStringBounds( text, fontRenderContext ).contains( x, y );
    }

    public double getWidth() {
        return font.getStringBounds( text, fontRenderContext ).getBounds2D().getWidth();
    }

    public double getHeight() {
        return font.getStringBounds( text, fontRenderContext ).getBounds2D().getHeight();
    }


}
