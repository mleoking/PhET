// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.energysystems.model.WaterPoweredGenerator;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Piccolo node that represents the water-powered generator in the view.
 *
 * @author John Blanco
 */
public class WaterPoweredGeneratorNode extends PNode {

    public WaterPoweredGeneratorNode( final WaterPoweredGenerator generator, final ModelViewTransform mvt ) {

        // Create the background.
        PImage backgroundImageNode = new PImage( WaterPoweredGenerator.BACKGROUND_IMAGE.getImage() ) {{
            double widthInView = mvt.modelToViewDeltaX( WaterPoweredGenerator.BACKGROUND_IMAGE.getWidth() );
            setScale( widthInView / getFullBoundsReference().width );
            Point2D centerToCenterOffsetInView = mvt.modelToViewDelta( WaterPoweredGenerator.BACKGROUND_IMAGE.getCenterToCenterOffset() ).toPoint2D();
            centerFullBoundsOnPoint( centerToCenterOffsetInView.getX(), centerToCenterOffsetInView.getY() );
        }};
        addChild( backgroundImageNode );

        // Create the wheel.
        final PImage wheelImageNode = new PImage( WaterPoweredGenerator.WHEEL_IMAGE.getImage() ) {{
            double widthInView = mvt.modelToViewDeltaX( WaterPoweredGenerator.WHEEL_IMAGE.getWidth() );
            setScale( widthInView / getFullBoundsReference().width );
            Point2D centerToCenterOffsetInView = mvt.modelToViewDelta( WaterPoweredGenerator.WHEEL_IMAGE.getCenterToCenterOffset() ).toPoint2D();
            centerFullBoundsOnPoint( centerToCenterOffsetInView.getX(), centerToCenterOffsetInView.getY() );
        }};
        addChild( wheelImageNode );

        // Update the rotation of the wheel image based on model value.
        final Point2D wheelRotationPoint = new Point2D.Double( wheelImageNode.getFullBoundsReference().getWidth() / 2,
                                                               wheelImageNode.getFullBoundsReference().getHeight() / 2 );
        generator.getWheelRotationalAngle().addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                double delta = angle - wheelImageNode.getRotation();
                wheelImageNode.rotateAboutPoint( delta, wheelRotationPoint );
            }
        } );

        // Add the nodes in the order that creates the desired layering.
        addChild( backgroundImageNode );
        addChild( wheelImageNode );

        // Update the overall offset based on the model position.
        generator.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }
}