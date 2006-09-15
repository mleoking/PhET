package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPNode;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:37 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CircuitNode extends PhetPNode {
    private Circuit circuit;

    public CircuitNode( Circuit circuit ) {
        this.circuit = circuit;
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchAdded( Branch branch ) {
                addChild( createNode( branch ) );
            }
        } );
    }

    public WireNode createNode( Branch branch ) {
        if( branch instanceof Wire ) {
            return new WireNode( (Wire)branch );
        }
        else {
            throw new RuntimeException( "Unrecognized branch type: " + branch.getClass() );
        }
    }

}
