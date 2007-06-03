/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.plots.bargraphs;

import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 9:47:54 AM
 */

public class VerticalTextGraphic extends PNode {
    private Font font;
    private String text;

    public VerticalTextGraphic( Font font, String text, Color color, Color outline ) {
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
