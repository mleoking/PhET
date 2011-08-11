// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * This class defines a Piccolo node that represents a model element in the
 * view, and the particular model element that it represents contains an image
 * that is used in the representation.
 *
 * @author John Blanco
 */
public class ImageMassNode extends PNode {
    private final ImageMass mass;
    private final ModelViewTransform mvt;
    private PBounds unrotatedBounds = new PBounds();

    public ImageMassNode( final ModelViewTransform mvt, final ImageMass mass, PhetPCanvas canvas ) {
        this.mass = mass;
        this.mvt = mvt;

        final PImage imageNode = new PImage();
        unrotatedBounds.setRect( imageNode.getFullBoundsReference() );
        addChild( imageNode );

        // Observe image changes.
        mass.addImageChangeObserver( new VoidFunction1<BufferedImage>() {
            public void apply( BufferedImage image ) {
                imageNode.setScale( 1 );
                imageNode.setImage( image );
                double scalingFactor = Math.abs( mvt.modelToViewDeltaY( mass.getHeight() ) ) / imageNode.getFullBoundsReference().height;
                if ( scalingFactor > 2 || scalingFactor < 0.5 ) {
                    System.out.println( getClass().getName() + " - Warning: Scaling factor is too large or small, drawing size should be adjusted.  Scaling factor = " + scalingFactor );
                }
                imageNode.setScale( scalingFactor );
                unrotatedBounds.setRect( imageNode.getFullBoundsReference() );
                updatePositionAndAngle();
            }
        } );

        // Observe height changes.
        mass.addHeightChangeObserver( new VoidFunction1<Double>() {
            public void apply( Double newHeight ) {
                imageNode.setScale( 1 );
                double scalingFactor = Math.abs( mvt.modelToViewDeltaY( newHeight ) ) / imageNode.getFullBoundsReference().height;
                imageNode.setScale( scalingFactor );
                unrotatedBounds.setRect( imageNode.getFullBoundsReference() );
                updatePositionAndAngle();
            }
        } );

        // Register for notification of position changes.
        mass.addPositionChangeObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D newPosition ) {
                updatePositionAndAngle();
            }
        } );

        // Register for notifications of rotational angle changes
        mass.addRotationalAngleChangeObserver( new VoidFunction1<Double>() {
            public void apply( Double newAngle ) {
                updatePositionAndAngle();
            }
        } );

        // Make the cursor change on mouse over.
        addInputEventListener( new CursorHandler() );

        // Add the mouse event handler.
        addInputEventListener( new MassDragHandler( mass, this, canvas, mvt ) );
    }

    private void updatePositionAndAngle() {
        setRotation( 0 );
        setOffset( mvt.modelToViewX( mass.getPosition().getX() - mass.getCenterOfMassXOffset() ) - getFullBoundsReference().width / 2,
                   mvt.modelToViewY( mass.getPosition().getY() ) - getFullBoundsReference().height );
        rotateAboutPoint( -mass.getRotationAngle(), getFullBoundsReference().getWidth() / 2 + mvt.modelToViewDeltaX( mass.getCenterOfMassXOffset() ),
                          getFullBoundsReference().getHeight() );
    }
}
