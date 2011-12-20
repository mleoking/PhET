// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.control;

import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.model.Solute;
import edu.colorado.phet.beerslawlab.model.SoluteForm;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteControlNode extends ControlPanelNode {

    public SoluteControlNode( ArrayList<Solute> solutes, Property<Solute> currentSolute, Property<SoluteForm> soluteForm ) {
        super( new LayoutNode( solutes, currentSolute, soluteForm ) );
    }

    private static class LayoutNode extends PNode {
        public LayoutNode( ArrayList<Solute> solutes, Property<Solute> currentSolute, Property<SoluteForm> soluteForm ) {

            // nodes
            SoluteChoiceNode soluteChoiceNode = new SoluteChoiceNode( solutes, currentSolute );
            SoluteFormChoiceNode soluteFormChoiceNode = new SoluteFormChoiceNode( soluteForm );

            // rendering order: combo box on top, because it has a popup
            {
                addChild( soluteFormChoiceNode );
                addChild( soluteChoiceNode );
            }

            // layout
            {
                // below solute choice, left justified
                soluteFormChoiceNode.setOffset( soluteChoiceNode.getFullBoundsReference().getMinX(),
                                                soluteChoiceNode.getFullBoundsReference().getMaxY() + 10 );
            }
        }
    }
}
