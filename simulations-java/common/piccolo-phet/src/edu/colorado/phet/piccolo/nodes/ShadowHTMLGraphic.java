/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo.nodes;

import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * Draws html text with a drop shadow.
 */

public class ShadowHTMLGraphic extends PNode {
    private HTMLNode htmlGraphic;
    private HTMLNode shadow;

    public ShadowHTMLGraphic( String html ) {
        htmlGraphic = new HTMLNode( html );
        shadow = new HTMLNode( html );
        shadow.setHTMLColor( Color.black );

        addChild( shadow );
        addChild( htmlGraphic );

        setShadowOffset( 1, 1 );
    }

    private void updateBuffer() {
    }

    public void setColor( Color baseColor ) {
        htmlGraphic.setHTMLColor( baseColor );
        updateBuffer();
    }

    public void setShadowColor( Color shadowColor ) {
        shadow.setHTMLColor( shadowColor );
        updateBuffer();
    }

    public void setFont( Font font ) {
        htmlGraphic.setFont( font );
        shadow.setFont( font );
        updateBuffer();
    }

    /**
     * Sets the HTML of this node from a set of lines, adding linebreaks.
     *
     * @param text
     */
    public void setHtml( String[]text ) {
        String str = "<html>";
        for( int i = 0; i < text.length; i++ ) {
            str += text[i];
            if( i < text.length - 1 ) {
                str += "<br>";
            }
        }
        setHtml( str );
    }

    public void setHtml( String html ) {
        htmlGraphic.setHTML( html );
        shadow.setHTML( html );
        updateBuffer();
    }

    public void setShadowOffset( double dx, double dy ) {
        shadow.setOffset( dx, dy );
    }

}