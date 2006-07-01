/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:25:26 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltageRootNode extends PNode {
    private TravoltageBodyNode travoltageBodyNode;

    public TravoltageRootNode() {
        travoltageBodyNode = new TravoltageBodyNode();
        addChild( travoltageBodyNode );
        addChild( new ElectronSetNode() );
        addChild( new SparkNode() );
    }

    public TravoltageBodyNode getTravoltageBodyNode() {
        return travoltageBodyNode;
    }
}
