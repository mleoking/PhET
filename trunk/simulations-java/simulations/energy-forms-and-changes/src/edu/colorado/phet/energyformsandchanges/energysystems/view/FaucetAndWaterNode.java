// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.energysystems.model.FaucetAndWater;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents a faucet and flowing water in the view.
 *
 * @author John Blanco
 */
public class FaucetAndWaterNode extends PNode {

    public FaucetAndWaterNode( FaucetAndWater faucet, final ModelViewTransform mvt ) {

        final FaucetNode faucetNode = new FaucetNode( EnergyFormsAndChangesSimSharing.UserComponents.faucet, faucet.flowProportion, faucet.enabled, 200, false );
        faucetNode.setOffset( -faucetNode.getGlobalOutputCenter().getX() + mvt.modelToViewDeltaX( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getX() ),
                              -faucetNode.getGlobalOutputCenter().getY() + mvt.modelToViewDeltaY( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getY() ) );
        addChild( faucetNode );
        System.out.println( "faucetNode.getGlobalOutputCenter() = " + faucetNode.getGlobalOutputCenter() );

        faucet.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }
}