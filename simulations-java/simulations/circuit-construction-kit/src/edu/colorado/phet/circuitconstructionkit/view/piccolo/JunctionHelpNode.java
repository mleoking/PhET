package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.Junction;

public class JunctionHelpNode extends TrackingHelpNode {

    public JunctionHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module, String text ) {
        super( cckSimulationPanel, module, text, RIGHT_BOTTOM );
        module.getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void junctionAdded( Junction junction ) {
                update();
            }

            public void junctionRemoved( Junction junction ) {
                update();
            }
        } );
        update();
    }

    private void update() {
        setVisible( getCckSimulationPanel().getCircuitNode().getNumJunctionNodes() > 0 );
        if ( getVisible() ) {
            int index = 0;
            final JunctionNode targetNode = getCckSimulationPanel().getCircuitNode().getJunctionNode( index );
            setFollowedNode( targetNode );
        }
    }

}
