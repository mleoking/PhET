/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.piccolophet.nodes;

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