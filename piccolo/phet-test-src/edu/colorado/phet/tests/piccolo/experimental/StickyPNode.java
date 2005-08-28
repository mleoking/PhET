/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.experimental;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * User: Sam Reid
 * Date: Aug 20, 2005
 * Time: 6:16:07 PM
 * Copyright (c) Aug 20, 2005 by Sam Reid
 */

public class StickyPNode extends PNode {
    private PNode pNode;
    private boolean recursing = false;

    public StickyPNode( PNode pNode ) {
        this.pNode = pNode;
        addChild( pNode );
    }

    protected void paint( PPaintContext paintContext ) {
        if( !recursing ) {
            recursing = true;
            setScale( getScale() / paintContext.getScale() );
            recursing = false;
        }
        if( paintContext.getScale() == 1.0 ) {
            super.paint( paintContext );
        }
    }
}
