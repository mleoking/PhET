// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.theramp.common;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 9:47:54 AM
 */

public class VerticalTextGraphic extends PNode {
    private Font font;
    private String text;

    public VerticalTextGraphic( Font font, String text, Color color, Color outline ) {
        super();
        this.font = font;
        this.text = text;
        ShadowHTMLNode phetTextNode = new ShadowHTMLNode( text );//, font, color, 1, 1, outline );
        phetTextNode.setColor( color );
        phetTextNode.setShadowColor( outline );
        phetTextNode.setFont( font );

        phetTextNode.translate( -3, -10 );
        phetTextNode.rotate( -Math.PI / 2 );

        addChild( phetTextNode );
    }

    public String getText() {
        return text;
    }
}