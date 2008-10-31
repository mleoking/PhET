package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.piccolophet.PhetPNode;

public class RightClickHelpNode extends PhetPNode {
    private boolean userRightClicked = false;
    private CCKModule module;
    private TrackingHelpNode branchHelpNode;
    private CCKSimulationPanel cckSimulationPanel;
    private TrackingHelpNode junctionHelpNode;
    private boolean dragging = false;
//    private boolean everBeenMoreThanOneWire = false;

    public RightClickHelpNode( CCKSimulationPanel cckSimulationPanel, final CCKModule module ) {
        this.module = module;
        this.cckSimulationPanel = cckSimulationPanel;
        String text = PhetUtilities.isMacintosh() ? CCKStrings.getString( "CCKHelp.right-click-help-mac" ) : CCKStrings.getString( "CCKHelp.right-click-help" );

        branchHelpNode = new TrackingHelpNode( cckSimulationPanel, module, text, TrackingHelpNode.BOTTOM_CENTER );
        addChild( branchHelpNode );

        junctionHelpNode = new TrackingHelpNode( cckSimulationPanel, module, text, TrackingHelpNode.RIGHT_BOTTOM );
        addChild( junctionHelpNode );

        cckSimulationPanel.addMouseListener( new MouseAdapter() {
//            public void mouseReleased( MouseEvent e ) {
//                dragging = false;
//                update();
//
//            }

            public void mousePressed( MouseEvent e ) {
                if ( e.isMetaDown() ) {//check for right click
                    userRightClicked = true;
                    dragging = true;
                    update();
                }
            }
        } );
//        cckSimulationPanel.addMouseMotionListener( new MouseMotionListener() {
//            public void mouseDragged( MouseEvent e ) {
//                dragging = true;
//                update();
//            }
//
//            public void mouseMoved( MouseEvent e ) {
//                dragging = false;
//                update();
//            }
//        } );
        module.getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void selectionChanged() {
                update();
            }

//            public void branchAdded( Branch branch ) {
//                everBeenMoreThanOneWire = everBeenMoreThanOneWire || module.getCircuit().getBranches().length >= 2 || ( module.getCircuit().getBranches().length == 1 && !( branch instanceof Wire ) );
//            }
        } );
        update();
    }

    private void update() {
        BranchNode follow = getFirstSelectedBranch();
        branchHelpNode.setVisible( false );
        if ( follow != null ) {
            boolean branchHelpVisible = !userRightClicked && isConnectedToSomething( follow ) && isNonWireSelected() && !dragging;
            branchHelpNode.setVisible( branchHelpVisible );
            branchHelpNode.setFollowedNode( follow );
        }
        else {
            branchHelpNode.setVisible( false );
        }

        JunctionNode followJunction = getFirstSelectedJunction();
        junctionHelpNode.setVisible( false );
        if ( followJunction != null ) {
            boolean junctionHelpVisible = !userRightClicked && isConnectedToTwoThings( followJunction ) & isJunctionSelected() && !dragging;
            junctionHelpNode.setVisible( junctionHelpVisible );
            junctionHelpNode.setFollowedNode( followJunction );
        }
        else {
            junctionHelpNode.setVisible( false );
        }
    }

    private boolean isConnectedToTwoThings( JunctionNode followJunction ) {
        return module.getCircuit().getAdjacentBranches( followJunction.getJunction() ).length >= 2;
    }

    private boolean isConnectedToSomething( BranchNode follow ) {
        return module.getCircuit().getNeighbors( follow.getBranch() ).length > 0;
    }

//    private boolean hasThereEverBeenMoreThanOneWire() {
//        return everBeenMoreThanOneWire;
//    }

    private JunctionNode getFirstSelectedJunction() {
        for ( int i = 0; i < cckSimulationPanel.getCircuitNode().getNumJunctionNodes(); i++ ) {
            JunctionNode node = cckSimulationPanel.getCircuitNode().getJunctionNode( i );
            if ( node.getJunction().isSelected() ) {
                return node;
            }
        }
        return null;
    }

    private BranchNode getFirstSelectedBranch() {
        for ( int i = 0; i < cckSimulationPanel.getCircuitNode().getNumBranchNodes(); i++ ) {
            BranchNode branchNode = cckSimulationPanel.getCircuitNode().getBranchNode( i );
            if ( branchNode.getBranch().isSelected() ) {
                return branchNode;
            }
        }
        return null;
    }

    private boolean isJunctionSelected() {
        return module.getCircuit().getSelectedJunctions().length > 0;
    }

    private boolean isNonWireSelected() {
        Branch[] b = module.getCircuit().getSelectedBranches();
        for ( int i = 0; i < b.length; i++ ) {
            Branch branch = b[i];
            if ( !( branch instanceof Wire ) || true ) {
                return true;
            }
        }
        return false;
    }
}
