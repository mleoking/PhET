package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;

public class BranchHelpNode extends TrackingHelpNode {

    public BranchHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module, String text ) {
        super( cckSimulationPanel, module, text, BOTTOM_LEFT );
        module.getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void branchRemoved( Branch branch ) {
                update();
            }

            public void branchAdded( Branch branch ) {
                update();
            }
        } );
    }

    protected void update() {
        setVisible( getCckSimulationPanel().getCircuitNode().getNumBranchNodes() > 0 );
        if ( getVisible() ) {
            setFollowedNode( getCckSimulationPanel().getCircuitNode().getBranchNode( 0 ) );
        }
    }

}
