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
    public TravoltageBodyNode() {
        addChild( new TravoltageBodyBackgroundNode() );
        ArmNode armNode = new ArmNode();
        armNode.setOffset( 194, 118 );
        addChild( armNode );

        LegNode legNode = new LegNode();
//        legNode.setOffset( 131.0, 231.0 );
        legNode.setOffset( 134, 231.0 );
        addChild( legNode );
    }
}
