// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
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

        // Create and add the various image nodes.
        addChild( new ModelElementImageNode( WaterPoweredGenerator.WIRE_CURVED_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( WaterPoweredGenerator.WIRE_STRAIGHT_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( WaterPoweredGenerator.HOUSING_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( WaterPoweredGenerator.CONNECTOR_IMAGE, mvt ) );
        final PNode paddlesNode = new ModelElementImageNode( WaterPoweredGenerator.WHEEL_PADDLES_IMAGE, mvt );
        addChild( paddlesNode );
        addChild( new ModelElementImageNode( WaterPoweredGenerator.WHEEL_HUB_IMAGE, mvt ) );
        final PNode wheelTextureNode = new ModelElementImageNode( WaterPoweredGenerator.WHEEL_TEXTURE_IMAGE, mvt );
        addChild( wheelTextureNode );

        // Update the rotation of the wheel image based on model value.
        final Point2D wheelRotationPoint = new Point2D.Double( paddlesNode.getFullBoundsReference().getCenterX(),
                                                               paddlesNode.getFullBoundsReference().getCenterY() );
        generator.getWheelRotationalAngle().addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                double delta = angle - paddlesNode.getRotation();
                paddlesNode.rotateAboutPoint( delta, wheelRotationPoint );
                wheelTextureNode.rotateAboutPoint( delta, wheelRotationPoint );
            }
        } );

        // Hide the paddles when in direct coupling mode.
        generator.directCouplingMode.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean directCouplingMode ) {
                paddlesNode.setVisible( !directCouplingMode );
            }
        } );
    }
}