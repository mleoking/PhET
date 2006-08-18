/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;

/**
 * Draws piccolo text with a shadow.
 */

public class ShadowPText extends PNode {
    private PText foreground;
    private PText background;

    public ShadowPText() {
        this( "" );
    }

    public ShadowPText( String text ) {
        foreground = new PText( text );
        background = new PText( text );
        addChild( background );
        addChild( foreground );
        background.setOffset( 1, 1 );
    }

    public void setTextPaint( Paint paint ) {
        foreground.setTextPaint( paint );
    }

    public void setFont( Font font ) {
        foreground.setFont( font );
        background.setFont( font );
    }

    public void setText( String s ) {
        foreground.setText( s );
        background.setText( s );
    }

    public void setShadowOffset( double dx, double dy ) {
        background.setOffset( dx, dy );
    }

    public void setShadowColor( Color color ) {
        background.setTextPaint( color );
    }
}
