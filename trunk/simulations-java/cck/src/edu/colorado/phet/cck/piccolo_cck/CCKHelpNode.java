package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.CCKStrings;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.umd.cs.piccolo.PNode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 1:07:29 AM
 * Copyright (c) Oct 5, 2006 by Sam Reid
 */

public class CCKHelpNode extends PhetPNode {
    private CCKSimulationPanel cckSimulationPanel;
    private ICCKModule module;

    public CCKHelpNode( CCKSimulationPanel cckSimulationPanel, ICCKModule module ) {
        this.cckSimulationPanel = cckSimulationPanel;
        this.module = module;
        JunctionHelpNode junctionHelpNode = new JunctionHelpNode( cckSimulationPanel, module );
        addChild( junctionHelpNode );

        BranchHelpNode branchHelpNode = new BranchHelpNode( cckSimulationPanel, module );
        addChild( branchHelpNode );

        ToolboxHelpNode toolboxHelpNode = new ToolboxHelpNode( cckSimulationPanel, module );
        addChild( toolboxHelpNode );
    }

    static class ToolboxHelpNode extends HelpBalloon {
        private CCKSimulationPanel cckSimulationPanel;
        private ICCKModule module;

        public ToolboxHelpNode( CCKSimulationPanel cckSimulationPanel, ICCKModule module ) {
            super( cckSimulationPanel, CCKStrings.toHTML( "CCKHelp.ToolboxHelp" ), RIGHT_BOTTOM, 30 );
            this.cckSimulationPanel = cckSimulationPanel;
            this.module = module;
            pointAt( cckSimulationPanel.getToolboxNodeSuite(), cckSimulationPanel );
        }
    }

    static class BranchHelpNode extends HelpBalloon {
        private CCKSimulationPanel cckSimulationPanel;
        private ICCKModule module;
        private BranchNode followedBranch;
        private PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateFollow();
            }
        };

        private void updateFollow() {
            pointAt( followedBranch.getGlobalFullBounds().getCenter2D() );
        }

        public BranchHelpNode( CCKSimulationPanel cckSimulationPanel, ICCKModule module ) {
            super( cckSimulationPanel, CCKStrings.toHTML( "CCKHelp.ComponentHelp" ), BOTTOM_LEFT, 40 );
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
            if( getVisible() ) {
//                int index = cckSimulationPanel.getCircuitNode().getNumBranchNodes() > 1 ? 1 : 0;
                int index = 0;
                final BranchNode branchNode = cckSimulationPanel.getCircuitNode().getBranchNode( index );
                setFollowedBranch( branchNode );
            }
        }

        private void setFollowedBranch( BranchNode branchNode ) {
            if( this.followedBranch != branchNode ) {
                if( this.followedBranch != null ) {
                    followedBranch.removePropertyChangeListener( listener );
                }
                this.followedBranch = branchNode;
                followedBranch.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
                updateFollow();
            }
        }
    }

    static class JunctionHelpNode extends HelpBalloon {
        private CCKSimulationPanel cckSimulationPanel;
        private ICCKModule module;
        private JunctionNode followedJunction;
        private PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateFollow();
            }
        };

        private void updateFollow() {
            pointAt( followedJunction.getGlobalFullBounds().getCenter2D() );
        }

        public JunctionHelpNode( CCKSimulationPanel cckSimulationPanel, ICCKModule module ) {
            super( cckSimulationPanel, CCKStrings.toHTML( "CCKHelp.JunctionHelp" ), RIGHT_BOTTOM, 30 );
            this.cckSimulationPanel = cckSimulationPanel;
            this.module = module;
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
            setVisible( cckSimulationPanel.getCircuitNode().getNumJunctionNodes() > 0 );
            if( getVisible() ) {
                int index = 0;
                final JunctionNode targetNode = cckSimulationPanel.getCircuitNode().getJunctionNode( index );
                setFollowedJunction( targetNode );
            }
        }

        private void setFollowedJunction( JunctionNode targetNode ) {
            if( followedJunction != targetNode ) {
                if( followedJunction != null ) {
                    followedJunction.removePropertyChangeListener( listener );
                }
                this.followedJunction = targetNode;
                followedJunction.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
                updateFollow();
            }
        }
    }
}
