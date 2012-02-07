// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.common.model.Solute;
import edu.colorado.phet.beerslawlab.common.model.Solution;
import edu.colorado.phet.beerslawlab.common.view.SoluteChoiceNode;
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

    public SolutionControlsNode( Solution solution, ArrayList<Solute> solutes, Property<Solute> currentSolute ) {
        super( new LayoutNode( solution, solutes, currentSolute ) );
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

    private static class LayoutNode extends PNode {
        public LayoutNode( Solution solution, ArrayList<Solute> solutes, Property<Solute> currentSolute ) {

            // nodes
            SolutionChoiceNode solutionChoiceNode = new SolutionChoiceNode( solutes, currentSolute );
            ConcentrationControlNode concentrationControlNode = new ConcentrationControlNode( solution );

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
