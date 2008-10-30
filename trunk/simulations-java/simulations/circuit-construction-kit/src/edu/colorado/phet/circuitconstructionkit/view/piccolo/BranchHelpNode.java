package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.umd.cs.piccolo.PNode;

public class BranchHelpNode extends HelpBalloon {
    private CCKSimulationPanel cckSimulationPanel;
    private CCKModule module;
    private BranchNode followedBranch;
    private PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            updateFollow();
        }
    };

    private void updateFollow() {
        pointAt( followedBranch.getGlobalFullBounds().getCenter2D() );
    }

    public BranchHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module, String text ) {
        super( cckSimulationPanel, text, BOTTOM_LEFT, 40 );
        this.cckSimulationPanel = cckSimulationPanel;
        this.module = module;
        module.getCircuit().addCircuitListener( new CircuitListenerAdapter() {

            public void branchRemoved( Branch branch ) {
                update();
            }

            public void branchAdded( Branch branch ) {
                update();
            }
        } );
    }

    private void update() {
        setVisible( cckSimulationPanel.getCircuitNode().getNumBranchNodes() > 0 );
        if ( getVisible() ) {
//                int index = cckSimulationPanel.getCircuitNode().getNumBranchNodes() > 1 ? 1 : 0;
            int index = 0;
            final BranchNode branchNode = cckSimulationPanel.getCircuitNode().getBranchNode( index );
            setFollowedBranch( branchNode );
        }
    }

    private void setFollowedBranch( BranchNode branchNode ) {
        if ( this.followedBranch != branchNode ) {
            if ( this.followedBranch != null ) {
                followedBranch.removePropertyChangeListener( listener );
            }
            this.followedBranch = branchNode;
            followedBranch.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
            updateFollow();
        }
    }
}
