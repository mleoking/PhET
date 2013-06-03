// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.view;

import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.view.BasicBalanceCanvas;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;

import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Main view class for the "Balance Lab" module.
 *
 * @author John Blanco
 */
public class BalanceLabCanvas extends BasicBalanceCanvas {

    protected MassKitSelectionNode massKitSelectionNode;

    public BalanceLabCanvas( final BalanceModel model ) {
        super( model );

        // Add the mass kits, which is the place where the user will get the
        // objects that can be placed on the balance.
        massKitSelectionNode = new MassKitSelectionNode( new Property<Integer>( 0 ), model, mvt, this );
        final ControlPanelNode massKit = new ControlPanelNode( massKitSelectionNode );
        nonMassLayer.addChild( massKit );

        // Lay out the control panels.
        double minDistanceToEdge = 20; // Value chosen based on visual appearance.
        double controlPanelCenterX = Math.min( DEFAULT_STAGE_SIZE.getWidth() - massKit.getFullBoundsReference().width / 2 - minDistanceToEdge,
                                               DEFAULT_STAGE_SIZE.getWidth() - controlPanel.getFullBoundsReference().width / 2 - minDistanceToEdge );
        massKit.setOffset( controlPanelCenterX - massKit.getFullBoundsReference().width / 2,
                           mvt.modelToViewY( 0 ) - massKit.getFullBoundsReference().height - 10 );
        controlPanel.setOffset( controlPanelCenterX - controlPanel.getFullBoundsReference().width / 2,
                                massKit.getFullBoundsReference().getMinY() - controlPanel.getFullBoundsReference().height - 10 );
    }

    @Override public void reset() {
        super.reset();
        massKitSelectionNode.reset();
    }
}
