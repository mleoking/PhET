// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.FaucetAndWater;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Piccolo node that represents a faucet and flowing water in the view.
 *
 * @author John Blanco
 */
public class FaucetAndWaterNode extends PositionableFadableModelElementNode {

    private static final double FAUCET_NODE_HORIZONTAL_LENGTH = 700; // In screen coords, which are close to pixels.

    public FaucetAndWaterNode( FaucetAndWater faucet, final ModelViewTransform mvt ) {
        super( faucet, mvt );

        // Create the faucet.
        final FaucetNode faucetNode = new FaucetNode( EnergyFormsAndChangesSimSharing.UserComponents.faucet,
                                                      faucet.flowProportion,
                                                      faucet.getObservableActiveState(),
                                                      FAUCET_NODE_HORIZONTAL_LENGTH,
                                                      false );
        faucetNode.setScale( 0.9 ); // Make it a little smaller than default.  Looks better in this sim.
        faucetNode.setOffset( -faucetNode.getGlobalOutputCenter().getX() + mvt.modelToViewDeltaX( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getX() ),
                              -faucetNode.getGlobalOutputCenter().getY() + mvt.modelToViewDeltaY( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getY() ) );
        // Create the water.
//        final PPath waterNode = new PhetPPath( new BasicStroke( 1 ), ColorUtils.darkerColor( EFACConstants.WATER_COLOR, 0.5 ) );
        final PPath waterNode = new PhetPPath();
        waterNode.setOffset( -mvt.modelToViewX( 0 ) + mvt.modelToViewDeltaX( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getX() ),
                             -mvt.modelToViewY( 0 ) + mvt.modelToViewDeltaY( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getY() ) );

        faucet.waterShape.addObserver( new VoidFunction1<Shape>() {
            public void apply( Shape waterShape ) {

                Shape waterShapeInView = mvt.modelToView( waterShape );
                waterNode.setPathTo( waterShapeInView );
                Rectangle2D waterBounds = waterShapeInView.getBounds2D();
                waterNode.setPaint( new GradientPaint( (float) waterBounds.getX(),
                                                       (float) waterBounds.getY(),
                                                       ColorUtils.darkerColor( EFACConstants.WATER_COLOR, 0.3 ),
                                                       (float) waterBounds.getMaxX(),
                                                       (float) waterBounds.getY(),
                                                       ColorUtils.brighterColor( EFACConstants.WATER_COLOR, 0.5 ) ) );
//                waterNode.setVisible( waterShapeInView.getBounds2D().getWidth() > 1 );
            }
        } );

        // Create the energy chunk layer.
        PNode energyChunkLayer = new EnergyChunkLayer( faucet.energyChunkList, faucet.getObservablePosition(), mvt );

        // Add the nodes in the order that creates the desired layering.
        addChild( waterNode );
        addChild( energyChunkLayer );
        addChild( faucetNode );
    }
}