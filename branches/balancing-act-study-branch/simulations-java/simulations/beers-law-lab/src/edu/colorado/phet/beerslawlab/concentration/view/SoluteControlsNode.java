// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.concentration.model.Solute;
import edu.colorado.phet.beerslawlab.concentration.model.Solute.SoluteForm;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Control panel for solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SoluteControlsNode extends ControlPanelNode {

    private PBounds contentBounds;

    public SoluteControlsNode( final ArrayList<Solute> solutes, final Property<Solute> currentSolute, final Property<SoluteForm> soluteForm ) {
        super( new PNode() {{
            // nodes
            SoluteChoiceNode soluteChoiceNode = new SoluteChoiceNode( solutes, currentSolute );
            SoluteFormChoiceNode soluteFormChoiceNode = new SoluteFormChoiceNode( soluteForm );

            // rendering order: combo box on top, because it has a popup
            this.addChild( soluteFormChoiceNode );
            this.addChild( soluteChoiceNode );

            // layout: below solute choice, left justified
            soluteFormChoiceNode.setOffset( soluteChoiceNode.getFullBoundsReference().getMinX(),
                                            soluteChoiceNode.getFullBoundsReference().getMaxY() + 10 );
        }} );
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
}
