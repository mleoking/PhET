// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.intro.model.Thermometer;
import edu.colorado.phet.energyformsandchanges.intro.model.UserMovableModelElement;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Piccolo node that represents a thermometer in the view.
 *
 * @author John Blanco
 */
public class ThermometerNode extends PComposite {

    // Constants that define the size and relative position of the triangle.
    // These values will need tweaking if the images used for the thermometer
    // are changed.
    private static final double TRIANGLE_SIDE_SIZE = 22; // In screen coordinates, which is close to pixels.
    private static final Dimension2D TRIANGLE_TIP_OFFSET_FROM_THERMOMETER_CENTER = new PDimension( -40, 47 );

    /**
     * Constructor.
     *
     * @param thermometer
     * @param mvt
     */
    public ThermometerNode( final Thermometer thermometer, final ModelViewTransform mvt, final PNode toolBoxNode ) {

        // Root node, all children should be added to this.
        PNode rootNode = new PNode();

        // Create and add nodes that will act as layers.
        PNode backLayer = new PNode();
        rootNode.addChild( backLayer );
        PNode middleLayer = new PNode();
        rootNode.addChild( middleLayer );
        PNode frontLayer = new PNode();
        rootNode.addChild( frontLayer );

        // Add the images for the front and back of the thermometer.
        {
            final double imageScale = 0.85; // Tweak factor for sizing the thermometers.
            backLayer.addChild( new PImage( EnergyFormsAndChangesResources.Images.THERMOMETER_BACK ) {{
                setScale( imageScale );
            }} );
            frontLayer.addChild( new PImage( EnergyFormsAndChangesResources.Images.THERMOMETER_FRONT ) {{
                setScale( imageScale );
            }} );
        }

        // Add the triangle that represents the point where the thermometer
        // touches the element whose temperature is being measured.  The
        // position of the thermometer in model space corresponds to the point
        // on the left side of this triangle.
        ImmutableVector2D triangleLeftmostPoint = new ImmutableVector2D( backLayer.getFullBoundsReference().getCenterX() + TRIANGLE_TIP_OFFSET_FROM_THERMOMETER_CENTER.getWidth(),
                                                                         backLayer.getFullBoundsReference().getCenterY() + TRIANGLE_TIP_OFFSET_FROM_THERMOMETER_CENTER.getHeight() );
        final ImmutableVector2D triangleUpperRightPoint = triangleLeftmostPoint.getAddedInstance( new ImmutableVector2D( TRIANGLE_SIDE_SIZE, 0 ).getRotatedInstance( Math.PI / 5 ) );
        final ImmutableVector2D triangleLowerRightPoint = triangleLeftmostPoint.getAddedInstance( new ImmutableVector2D( TRIANGLE_SIDE_SIZE, 0 ).getRotatedInstance( -Math.PI / 5 ) );
        DoubleGeneralPath trianglePath = new DoubleGeneralPath( triangleLeftmostPoint ) {{
            lineTo( triangleUpperRightPoint );
            lineTo( triangleLowerRightPoint );
            closePath();
        }};
        middleLayer.addChild( new PhetPPath( trianglePath.getGeneralPath(), PhetColorScheme.RED, new BasicStroke( 2 ), Color.BLACK ) );

        // Add the root node, but enclose it in a ZeroOffsetNode, so that the
        // whole node follows the Piccolo convention of having the upper left
        // be at point (0, 0).
        addChild( new ZeroOffsetNode( rootNode ) );

        // Add the cursor handler.
        addInputEventListener( new CursorHandler( CursorHandler.HAND ) );

        // Add the drag handler.
        addInputEventListener( new DragHandler( thermometer, this, mvt ) );

        // Update the offset if and when the model position changes.
        thermometer.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D position ) {
                setOffset( mvt.modelToViewX( position.getX() ),
                           mvt.modelToViewY( position.getY() ) - ( getFullBoundsReference().height / 2 + TRIANGLE_TIP_OFFSET_FROM_THERMOMETER_CENTER.getHeight() ) );
            }
        } );

        // Add a listener that detects the situation where the user has
        // released this thermometer over the tool box and, in response,
        // resets the position.
        thermometer.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                if ( ThermometerNode.this.getFullBoundsReference().intersects( toolBoxNode.getFullBoundsReference() ) ) {
                    // The user has released the thermometer node over the tool
                    // box, so return the thermometer to its original position.
                    thermometer.position.reset();
                }
            }
        } );
    }

    /**
     * Drag handler for thermometers.  This constrains the thermometer to the
     * play area.
     */
    private static class DragHandler extends RelativeDragHandler {

        private final UserMovableModelElement modelElement;

        public DragHandler( UserMovableModelElement modelElement, PNode node, ModelViewTransform mvt ) {
            super( node, mvt, modelElement.position, new ThermometerLocationConstraint( mvt ) );
            this.modelElement = modelElement;
        }

        @Override public void mousePressed( PInputEvent event ) {
            super.mousePressed( event );
            modelElement.userControlled.set( true );
        }

        @Override public void mouseReleased( PInputEvent event ) {
            super.mouseReleased( event );
            modelElement.userControlled.set( false );
        }
    }

    // Function that constrains the valid locations for the thermometers.
    private static class ThermometerLocationConstraint implements Function1<Point2D, Point2D> {

        private final ModelViewTransform mvt;

        private ThermometerLocationConstraint( ModelViewTransform mvt ) {
            this.mvt = mvt;
        }

        public Point2D apply( Point2D proposedModelPos ) {
            // TODO: Needs to be enhanced to handle shape of object.
            double constrainedXPos = MathUtil.clamp( mvt.viewToModelX( 0 ), proposedModelPos.getX(), mvt.viewToModelX( EFACIntroCanvas.STAGE_SIZE.getWidth() ) );
            double constrainedYPos = MathUtil.clamp( mvt.viewToModelY( EFACIntroCanvas.STAGE_SIZE.getHeight() ), proposedModelPos.getY(), mvt.viewToModelY( 0 ) );
            return new Point2D.Double( constrainedXPos, constrainedYPos );
        }
    }
}
