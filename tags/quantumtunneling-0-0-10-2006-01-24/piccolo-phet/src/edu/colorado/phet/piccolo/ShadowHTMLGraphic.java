/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
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

        setShadowOffset( 1, 1 );
    }

    private void updateBuffer() {
    }

    public void setColor( Color baseColor ) {
        htmlGraphic.setColor( baseColor );
        updateBuffer();
    }

    public void setShadowColor( Color shadowColor ) {
        shadow.setColor( shadowColor );
        updateBuffer();
    }

    public void setFont( Font font ) {
        htmlGraphic.setFont( font );
        shadow.setFont( font );
        updateBuffer();
    }

    public void setHtml( String html ) {
        htmlGraphic.setHtml( html );
        shadow.setHtml( html );
        updateBuffer();
    }

    public void setShadowOffset( int dx, int dy ) {
        shadow.setOffset( dx, dy );
    }

}