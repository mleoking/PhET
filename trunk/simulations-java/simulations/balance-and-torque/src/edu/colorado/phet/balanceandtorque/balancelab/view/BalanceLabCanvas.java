// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.view;

import edu.colorado.phet.balanceandtorque.balancelab.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.intro.view.IntroCanvas;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;

/**
 * Main view class for the balance lab module.
 *
 * @author John Blanco
 */
public class BalanceLabCanvas extends IntroCanvas {

    public BalanceLabCanvas( final BalanceModel model ) {
        super( model );

        // Add the mass kit, which is the place where the user will get the
        // objects that can be placed on the balance.
        final MassKitSelectionNode massKitSelectionNode = new MassKitSelectionNode( new Property<Integer>( 0 ), model, mvt, this );
        ControlPanelNode massKit = new ControlPanelNode( massKitSelectionNode );
        nonMassLayer.addChild( massKit );

        // Lay out the control panels.
        double minDistanceToEdge = 20; // Value chosen based on visual appearance.
        double controlPanelCenterX = Math.min( STAGE_SIZE.getWidth() - massKit.getFullBoundsReference().width / 2 - minDistanceToEdge,
                                               STAGE_SIZE.getWidth() - controlPanel.getFullBoundsReference().width / 2 - minDistanceToEdge );
        massKit.setOffset( controlPanelCenterX - massKit.getFullBoundsReference().width / 2,
                           mvt.modelToViewY( 0 ) - massKit.getFullBoundsReference().height - 10 );
        controlPanel.setOffset( controlPanelCenterX - controlPanel.getFullBoundsReference().width / 2,
                                massKit.getFullBoundsReference().getMinY() - controlPanel.getFullBoundsReference().height - 10 );
    }
}
