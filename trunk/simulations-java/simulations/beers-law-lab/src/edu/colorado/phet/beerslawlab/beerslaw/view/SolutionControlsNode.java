// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Control panel for solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SolutionControlsNode extends ControlPanelNode {

    private PBounds contentBounds;

    public SolutionControlsNode( ArrayList<BeersLawSolution> solutions, Property<BeersLawSolution> currentSolution ) {
        super( new LayoutNode( solutions, currentSolution ) );
    }

    /*
     * WORKAROUND for #3190.
     * Popping up the solute combo box causes the control panel to resize, undesirable behavior.
     * This workaround makes the control panel use its initial size, effectively disabling resizing.
     */
    @Override protected PBounds getControlPanelBounds( PNode content ) {
        if ( contentBounds == null ) {
            contentBounds = content.getFullBounds();
        }
        return contentBounds;
    }

    // This node lays out the control panel.
    private static class LayoutNode extends PNode {
        public LayoutNode( ArrayList<BeersLawSolution> solutions, Property<BeersLawSolution> currentSolution ) {

            // nodes
            SolutionChoiceNode solutionChoiceNode = new SolutionChoiceNode( solutions, currentSolution );
            ConcentrationControlNode concentrationControlNode = new ConcentrationControlNode( currentSolution );

            // rendering order: combo box on top, because it has a popup
            {
                addChild( concentrationControlNode );
                addChild( solutionChoiceNode );
            }

            // layout
            // below solute choice, left justified
            concentrationControlNode.setOffset( concentrationControlNode.getFullBoundsReference().getMinX(),
                                                concentrationControlNode.getFullBoundsReference().getMaxY() + 25 );
        }
    }
}
