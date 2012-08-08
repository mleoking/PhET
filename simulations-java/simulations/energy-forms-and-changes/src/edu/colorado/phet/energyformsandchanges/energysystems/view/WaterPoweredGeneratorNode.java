// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.energysystems.model.WaterPoweredGenerator;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the water-powered generator in the view.
 *
 * @author John Blanco
 */
public class WaterPoweredGeneratorNode extends PNode {

    public WaterPoweredGeneratorNode( final WaterPoweredGenerator generator, final ModelViewTransform mvt ) {

        // Create and add the various image nodes.
        addChild( new ModelElementImageNode( WaterPoweredGenerator.BACKGROUND_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( WaterPoweredGenerator.CONNECTOR_IMAGE, mvt ) );
        final PNode wheelImageNode = new ModelElementImageNode( WaterPoweredGenerator.WHEEL_IMAGE, mvt );
        addChild( wheelImageNode );

        // Update the rotation of the wheel image based on model value.
        final Point2D wheelRotationPoint = new Point2D.Double( wheelImageNode.getFullBoundsReference().getCenterX(),
                                                               wheelImageNode.getFullBoundsReference().getCenterY() );
        generator.getWheelRotationalAngle().addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                double delta = angle - wheelImageNode.getRotation();
                wheelImageNode.rotateAboutPoint( delta, wheelRotationPoint );
            }
        } );

        // Update the overall offset based on the model position.
        generator.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }
}