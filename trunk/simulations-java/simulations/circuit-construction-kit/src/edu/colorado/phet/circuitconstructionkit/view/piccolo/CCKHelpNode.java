package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 1:07:29 AM
 */

public class CCKHelpNode extends PhetPNode {
    private CCKSimulationPanel cckSimulationPanel;
    private CCKModule module;

    public CCKHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module ) {
        this.cckSimulationPanel = cckSimulationPanel;
        this.module = module;
        JunctionHelpNode junctionHelpNode = new JunctionHelpNode( cckSimulationPanel, module );
        addChild( junctionHelpNode );

        BranchHelpNode branchHelpNode = new BranchHelpNode( cckSimulationPanel, module, CCKStrings.getHTML( "CCKHelp.ComponentHelp" ) );
        addChild( branchHelpNode );

        ToolboxHelpNode toolboxHelpNode = new ToolboxHelpNode( cckSimulationPanel, module );
        addChild( toolboxHelpNode );
    }

    static class ToolboxHelpNode extends HelpBalloon {
        private CCKSimulationPanel cckSimulationPanel;
        private CCKModule module;

        public ToolboxHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module ) {
            super( cckSimulationPanel, CCKStrings.getHTML( "CCKHelp.ToolboxHelp" ), RIGHT_BOTTOM, 30 );
            this.cckSimulationPanel = cckSimulationPanel;
            this.module = module;
            pointAt( cckSimulationPanel.getToolboxNodeSuite(), cckSimulationPanel );
        }
    }

    static class JunctionHelpNode extends HelpBalloon {
        private CCKSimulationPanel cckSimulationPanel;
        private CCKModule module;
        private JunctionNode followedJunction;
        private PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateFollow();
            }
        };

        private void updateFollow() {
            pointAt( followedJunction.getGlobalFullBounds().getCenter2D() );
        }

        public JunctionHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module ) {
            super( cckSimulationPanel, CCKStrings.getHTML( "CCKHelp.JunctionHelp" ), RIGHT_BOTTOM, 30 );
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
            if ( getVisible() ) {
                int index = 0;
                final JunctionNode targetNode = cckSimulationPanel.getCircuitNode().getJunctionNode( index );
                setFollowedJunction( targetNode );
            }
        }

        private void setFollowedJunction( JunctionNode targetNode ) {
            if ( followedJunction != targetNode ) {
                if ( followedJunction != null ) {
                    followedJunction.removePropertyChangeListener( listener );
                }
                this.followedJunction = targetNode;
                followedJunction.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
                updateFollow();
            }
        }
    }
}
