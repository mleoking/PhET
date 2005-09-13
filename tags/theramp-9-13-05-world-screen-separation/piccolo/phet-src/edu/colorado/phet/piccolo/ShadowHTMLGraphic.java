/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * Draws html text with a drop shadow.
 */

public class ShadowHTMLGraphic extends PNode {
    private HTMLGraphic htmlGraphic;
    private HTMLGraphic shadow;

    public ShadowHTMLGraphic( String html ) {
        htmlGraphic = new HTMLGraphic( html );
        shadow = new HTMLGraphic( html );
        shadow.setColor( Color.black );

        addChild( shadow );
        addChild( htmlGraphic );

        shadow.setOffset( 1, 1 );
    }

    private void updateBuffer() {
    }

    public void setColor( Color baseColor ) {
        htmlGraphic.setColor( baseColor );
        updateBuffer();
    }

    public void setShadowColor( Color yellow ) {
        shadow.setColor( yellow );
        updateBuffer();
    }

    public void setFont( Font font ) {
        htmlGraphic.setFont( font );
        shadow.setFont( font );
        updateBuffer();
    }
}
