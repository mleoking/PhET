/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 2, 2005
 * Time: 12:07:41 AM
 * Copyright (c) Aug 2, 2005 by Sam Reid
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

    public void setColor( Color baseColor ) {
        htmlGraphic.setColor( baseColor );
    }

    public void setShadowColor( Color yellow ) {
        shadow.setColor( yellow );
    }

    public void setFont( Font font ) {
        htmlGraphic.setFont( font );
        shadow.setFont( font );
    }
}
