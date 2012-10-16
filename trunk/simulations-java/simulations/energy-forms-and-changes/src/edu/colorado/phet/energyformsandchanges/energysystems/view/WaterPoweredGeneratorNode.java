// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.WaterPoweredGenerator;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the water-powered generator in the view.
 *
 * @author John Blanco
 */
public class WaterPoweredGeneratorNode extends PositionableFadableModelElementNode {

    public WaterPoweredGeneratorNode( final WaterPoweredGenerator generator, final ModelViewTransform mvt ) {
        super( generator, mvt );

        // Create and add the various image nodes and energy chunk layers.
        addChild( new ModelElementImageNode( WaterPoweredGenerator.WIRE_CURVED_IMAGE, mvt ) );
        addChild( new EnergyChunkLayer( generator.electricalEnergyChunks, generator.getObservablePosition(), mvt ) );
        addChild( new ModelElementImageNode( WaterPoweredGenerator.HOUSING_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( WaterPoweredGenerator.CONNECTOR_IMAGE, mvt ) );
        final PNode spokesNode = new ModelElementImageNode( WaterPoweredGenerator.SHORT_SPOKES_IMAGE, mvt );
        addChild( spokesNode );
        final PNode paddlesNode = new ModelElementImageNode( WaterPoweredGenerator.WHEEL_PADDLES_IMAGE, mvt );
        addChild( paddlesNode );
        addChild( new ModelElementImageNode( WaterPoweredGenerator.WHEEL_HUB_IMAGE, mvt ) );
        final PNode wheelTextureNode = new ModelElementImageNode( WaterPoweredGenerator.WHEEL_TEXTURE_IMAGE, mvt );
        addChild( wheelTextureNode );
        addChild( new EnergyChunkLayer( generator.energyChunkList, generator.getObservablePosition(), mvt ) );

        // Update the rotation of the wheel image based on model value.
        final Point2D wheelRotationPoint = new Point2D.Double( paddlesNode.getFullBoundsReference().getCenterX(),
                                                               paddlesNode.getFullBoundsReference().getCenterY() );
        generator.getWheelRotationalAngle().addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                double delta = -angle - paddlesNode.getRotation();
                paddlesNode.rotateAboutPoint( delta, wheelRotationPoint );
                wheelTextureNode.rotateAboutPoint( delta, wheelRotationPoint );
                spokesNode.rotateAboutPoint( delta, wheelRotationPoint );
            }
        } );

        // Hide the paddles and show the spokes when in direct coupling mode.
        generator.directCouplingMode.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean directCouplingMode ) {
                paddlesNode.setVisible( !directCouplingMode );
                spokesNode.setVisible( directCouplingMode );
            }
        } );
    }
}