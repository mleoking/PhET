// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.barchart;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class is used to draw the vertical axis label of a single Bar in a BarChartNode.
 * It is still under development and subject to change.
 *
 * @author Sam Reid
 */

public class VerticalShadowHTMLNode extends PNode {
    private ShadowHTMLNode shadowHTMLNode;

    public VerticalShadowHTMLNode( Font font, String text, Color color, Color shadowColor ) {
        shadowHTMLNode = new ShadowHTMLNode( text );
        shadowHTMLNode.setColor( color );
        shadowHTMLNode.setShadowColor( shadowColor );
        shadowHTMLNode.setFont( font );

        shadowHTMLNode.translate( -3, -10 );//todo: remove the need for these magic numbers
        shadowHTMLNode.rotate( -Math.PI / 2 );

        addChild( shadowHTMLNode );
    }

    public void setText( String text ) {
        this.shadowHTMLNode.setHtml( text );
    }

    public void setShadowVisible( boolean shadowVisible ) {
        this.shadowHTMLNode.setShadowVisible( shadowVisible );
    }

    public void setForeground( Color color ) {
        this.shadowHTMLNode.setColor( color );
    }
}
