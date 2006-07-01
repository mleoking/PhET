/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:26:24 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltageBodyNode extends PNode {
    private LegNode legNode;
    private ArmNode armNode;

    public TravoltageBodyNode() {
        addChild( new TravoltageBodyBackgroundNode() );
        armNode = new ArmNode();
        armNode.setOffset( 194, 118 );
        addChild( armNode );

        legNode = new LegNode();
        legNode.setOffset( 134, 231.0 );
        addChild( legNode );
    }

    public LegNode getLegNode() {
        return legNode;
    }

    public ArmNode getArmNode() {
        return armNode;
    }
}
