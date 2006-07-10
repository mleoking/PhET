/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

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

        legNode = new LegNode();
        legNode.setOffset( 134, 231.0 );

        addChild( armNode );
        addChild( legNode );
        armNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                removeChild( armNode );
                removeChild( legNode );
                addChild( legNode );
                addChild( armNode );
            }
        } );
        legNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                removeChild( armNode );
                removeChild( legNode );
                addChild( armNode );
                addChild( legNode );
            }
        } );
    }

    public LegNode getLegNode() {
        return legNode;
    }

    public ArmNode getArmNode() {
        return armNode;
    }
}
