package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.piccolo.PhetPNode;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Sep 27, 2006
 * Time: 1:39:13 PM
 * Copyright (c) Sep 27, 2006 by Sam Reid
 */

public class ReadoutSetNode extends PhetPNode {
    private ICCKModule module;
    private Circuit circuit;

    public ReadoutSetNode( ICCKModule module, Circuit circuit ) {
        this.module = module;
        this.circuit = circuit;
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void junctionRemoved( Junction junction ) {
                removeJunctionReadout( junction );
            }

            public void branchRemoved( Branch branch ) {
                removeBranchReadout( branch );
            }

            public void junctionAdded( Junction junction ) {
                addJunctionReadout( junction );
            }

            public void branchAdded( Branch branch ) {
                addBranchReadout( branch );
            }
        } );
    }

    private void removeJunctionReadout( Junction junction ) {
    }

    private void addBranchReadout( Branch branch ) {
        ReadoutNode readoutNode = new ReadoutNode( module, branch, module.getSimulationPanel(), new DecimalFormat( "0.00" ) );
        addChild( readoutNode );
    }

    private void addJunctionReadout( Junction junction ) {
    }

    private void removeBranchReadout( Branch branch ) {
    }
}
