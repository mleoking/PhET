// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.FaucetAndWater;
import edu.colorado.phet.energyformsandchanges.energysystems.model.WaterDrop;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Piccolo node that represents a faucet and flowing water in the view.
 *
 * @author John Blanco
 */
public class FaucetAndWaterNode extends PositionableFadableModelElementNode {

    private static final double FAUCET_NODE_HORIZONTAL_LENGTH = 700; // In screen coords, which are close to pixels.

    public FaucetAndWaterNode( final FaucetAndWater faucet, final ModelViewTransform mvt ) {
        super( faucet, mvt );

        // Create the faucet.
        final FaucetNode faucetNode = new FaucetNode( EnergyFormsAndChangesSimSharing.UserComponents.faucet,
                                                      1,
                                                      faucet.flowProportion,
                                                      faucet.getObservableActiveState(),
                                                      FAUCET_NODE_HORIZONTAL_LENGTH,
                                                      40,
                                                      false );
        faucetNode.setScale( 0.9 ); // Make it a little smaller than default.  Looks better in this sim.
        faucetNode.setOffset( -faucetNode.getGlobalOutputCenter().getX() + mvt.modelToViewDeltaX( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getX() ),
                              -faucetNode.getGlobalOutputCenter().getY() + mvt.modelToViewDeltaY( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getY() ) );
        // Create the water.
        final PPath waterNode = new PhetPPath( EFACConstants.WATER_COLOR_OPAQUE );
        waterNode.setOffset( -mvt.modelToViewX( 0 ) + mvt.modelToViewDeltaX( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getX() ),
                             -mvt.modelToViewY( 0 ) + mvt.modelToViewDeltaY( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getY() ) );

        // Create the water, which consists of a set of water drops.
        final PNode waterLayer = new PNode();
        faucet.waterDrops.addElementAddedObserver( new VoidFunction1<WaterDrop>() {
            public void apply( final WaterDrop addedWaterDrop ) {
                final PNode waterDropNode = new WaterDropNode( addedWaterDrop, mvt );
                waterLayer.addChild( waterDropNode );
                faucet.waterDrops.addElementRemovedObserver( new VoidFunction1<WaterDrop>() {
                    public void apply( WaterDrop removedWaterDrop ) {
                        if ( removedWaterDrop == addedWaterDrop ) {
                            faucet.waterDrops.removeElementAddedObserver( this );
                            waterLayer.removeChild( waterDropNode );
                        }
                    }
                } );
            }
        } );

        // Create the energy chunk layer.
        PNode energyChunkLayer = new EnergyChunkLayer( faucet.energyChunkList, faucet.getObservablePosition(), mvt );

        // Add the nodes in the order that creates the desired layering.
        addChild( waterLayer );
        addChild( energyChunkLayer );
        addChild( faucetNode );
    }
}