/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.test;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 8:54:51 PM
 * Copyright (c) May 26, 2006 by Sam Reid
 */
public class ModelNode extends PhetPNode {
    private PNode node;

    public ModelNode( PNode node ) {
        super( node );
        this.node = node;
    }

    public ModelNode( PNode node, double width ) {
        this( node );
        setModelWidth( width );
    }

    //this setter maintains aspect ratio of the underlying node. (as opposed to setWidth())
    public void setModelWidth( double width ) {
        setScale( width / node.getFullBounds().getWidth() );
    }
}
