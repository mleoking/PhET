/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PNodeCache;

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

        computeBuffer();

//        addChild( shadow );
//        addChild( htmlGraphic );

        shadow.setOffset( 1, 1 );
    }

    private void computeBuffer() {
        PNode buffer = new PNodeCache();
        buffer.addChild( shadow );
        buffer.addChild( htmlGraphic );
        removeAllChildren();

        addChild( buffer );
    }

    public void setColor( Color baseColor ) {
        htmlGraphic.setColor( baseColor );
        computeBuffer();
    }

    public void setShadowColor( Color yellow ) {
        shadow.setColor( yellow );
        computeBuffer();
    }

    public void setFont( Font font ) {
        htmlGraphic.setFont( font );
        shadow.setFont( font );
        computeBuffer();
    }
}
