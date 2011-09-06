// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines a Piccolo node that represents a model element in the
 * view, and the particular model element itself contains an image that is used
 * as the primary representation.
 *
 * @author John Blanco
 */
public class ImageMassNode extends PNode {
    private final ImageMass mass;
    private final ModelViewTransform mvt;
    protected final PImage imageNode = new PImage();
    private PText massIndicator;

    public ImageMassNode( final ModelViewTransform mvt, final ImageMass mass, PhetPCanvas canvas, BooleanProperty massLabelVisibilityProperty ) {
        this.mass = mass;
        this.mvt = mvt;

        // Add the mass indicator label.  Note that it is positioned elsewhere.
        // TODO: i18n, including order and units!
        massIndicator = new PText( new DecimalFormat( "##" ).format( mass.getMass() ) + " kg" ) {{
            setFont( new PhetFont( 14 ) );
        }};
        addChild( massIndicator );

        // Observe changes to mass indicator visibility.
        massLabelVisibilityProperty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean showMassIndicator ) {
                massIndicator.setVisible( showMassIndicator );
            }
        } );

        // Add the image node.
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
                updatePositionAndAngle();
//                massIndicator.setOffset( mvt.modelToViewX( mass.getPosition().getX() + mass.getCenterOfMassXOffset() ) - massIndicator.getFullBoundsReference().width / 2,
//                                         imageNode.getFullBoundsReference().getMinY() - massIndicator.getFullBoundsReference().height );
//                massIndicator.setOffset( imageNode.getFullBoundsReference().getCenterX() - massIndicator.getFullBoundsReference().width / 2,
//                                         imageNode.getFullBoundsReference().getMinY() - massIndicator.getFullBoundsReference().height );
//                massIndicator.setOffset( mvt.modelToViewX( mass.getPosition().getX() ) - massIndicator.getFullBoundsReference().width / 2,
//                                         imageNode.getFullBoundsReference().getMinY() - massIndicator.getFullBoundsReference().height );
            }
        } );

        // Observe height changes.
        mass.addHeightChangeObserver( new VoidFunction1<Double>() {
            public void apply( Double newHeight ) {
                imageNode.setScale( 1 );
                double scalingFactor = Math.abs( mvt.modelToViewDeltaY( newHeight ) ) / imageNode.getFullBoundsReference().height;
                imageNode.setScale( scalingFactor );
                updatePositionAndAngle();
            }
        } );

        // Observe position changes.
        mass.addPositionChangeObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D newPosition ) {
                updatePositionAndAngle();
            }
        } );

        // Observe rotational angle changes.
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
        // Position the label to be centered above the center of mass, as
        // opposed to the center of the image node.
        massIndicator.setOffset( imageNode.getFullBoundsReference().getCenterX() + mvt.modelToViewDeltaX( mass.getCenterOfMassXOffset() ) - massIndicator.getFullBoundsReference().width / 2,
                                 -( massIndicator.getFullBoundsReference().height * 1.2 ) );
        // Set the position and rotation of the entire node.
        setOffset( mvt.modelToViewX( mass.getPosition().getX() - mass.getCenterOfMassXOffset() ) - imageNode.getFullBoundsReference().width / 2,
                   mvt.modelToViewY( mass.getPosition().getY() ) - imageNode.getFullBoundsReference().height );
        rotateAboutPoint( -mass.getRotationAngle(), getFullBoundsReference().getWidth() / 2 + mvt.modelToViewDeltaX( mass.getCenterOfMassXOffset() ),
                          imageNode.getFullBoundsReference().getHeight() );
    }
}
