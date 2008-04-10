/* Copyright 2008, University of Colorado */
package edu.colorado.phet.common.piccolophet.nodes.barchart;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class is used by Energy Skate Park (and possibly other sims). 
 * It is still under development and subject to change.
 * @author Sam Reid
 */

public class VerticalTextNode extends PNode {
    private Font font;
    private String text;

    public VerticalTextNode( Font font, String text, Color color, Color outline ) {
        this.font = font;
        this.text = text;
        ShadowHTMLNode phetTextNode = new ShadowHTMLNode( text );//, font, color, 1, 1, outline );
        phetTextNode.setColor( color );
        phetTextNode.setShadowColor( outline );
        phetTextNode.setFont( font );

        double h = phetTextNode.getFullBounds().getHeight();
//        System.out.println( "h = " + h );
        phetTextNode.translate( -3, -10 );
        phetTextNode.rotate( -Math.PI / 2 );

        addChild( phetTextNode );
    }

    public String getText() {
        return text;
    }
}
