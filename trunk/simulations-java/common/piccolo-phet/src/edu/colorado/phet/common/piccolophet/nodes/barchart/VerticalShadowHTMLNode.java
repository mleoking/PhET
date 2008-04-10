/* Copyright 2008, University of Colorado */
package edu.colorado.phet.common.piccolophet.nodes.barchart;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class is used to draw the vertical axis label of a single Bar in a BarChartNode.
 * It is still under development and subject to change.
 *
 * @author Sam Reid
 */

public class VerticalShadowHTMLNode extends PNode {
    private String text;

    public VerticalShadowHTMLNode( Font font, String text, Color color, Color outline ) {
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
