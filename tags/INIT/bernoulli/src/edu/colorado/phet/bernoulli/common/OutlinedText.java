package edu.colorado.phet.bernoulli.common;

import java.awt.*;
import java.awt.font.GlyphVector;

/**
 * User: Sam Reid
 * Date: Sep 2, 2003
 * Time: 1:09:32 AM
 * Copyright (c) Sep 2, 2003 by Sam Reid
 */
public class OutlinedText {
    public void paint( Graphics2D g, String text, int x, int y, Color textColor, Color outlineColor, Stroke outlineStroke, Font font ) {
        g.setFont( font );
        GlyphVector gv = font.createGlyphVector( g.getFontRenderContext(), text );
        g.setStroke( outlineStroke );
        g.setColor( outlineColor );
        g.draw( gv.getOutline( x, y ) );

        g.setColor( textColor );
        g.drawString( text, x, y );
    }
}
