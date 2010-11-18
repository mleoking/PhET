package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class VelocitySensorNode extends PNode {
    private final VelocitySensor pressureSensor;

    public VelocitySensorNode( final ModelViewTransform2D transform, final VelocitySensor velocitySensor ) {
        this.pressureSensor = velocitySensor;
        double hotSpotRadius = PressureSensorNode.hotSpotRadius;
        addChild( new PhetPPath( new Ellipse2D.Double( -hotSpotRadius, -hotSpotRadius, hotSpotRadius * 2, hotSpotRadius * 2 ), Color.red ) );
        final PText child = new PText( getText() ) {{
            setFont( new PhetFont( 18, true ) );
        }};

        final ArrowNode arrowNode = new ArrowNode( new Point2D.Double(), new Point2D.Double( 0, 1 ), 10, 10, 5, 0.5, true ) {{
            setPaint( Color.red );
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( Color.black );
        }};

        addChild( arrowNode );
        addChild( child );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            private Point2D.Double relativeGrabPoint;

            public void mousePressed( PInputEvent event ) {
                updateGrabPoint( event );
            }

            private void updateGrabPoint( PInputEvent event ) {
                Point2D viewStartingPoint = event.getPositionRelativeTo( getParent() );
                Point2D viewCoordinateOfObject = transform.modelToView( velocitySensor.getX(), velocitySensor.getY() );
                relativeGrabPoint = new Point2D.Double( viewStartingPoint.getX() - viewCoordinateOfObject.getX(), viewStartingPoint.getY() - viewCoordinateOfObject.getY() );
            }

            public void mouseDragged( PInputEvent event ) {
                if ( relativeGrabPoint == null ) {
                    updateGrabPoint( event );
                }
                final Point2D newDragPosition = event.getPositionRelativeTo( getParent() );
                Point2D modelLocation = transform.viewToModel( newDragPosition.getX() - relativeGrabPoint.getX(),
                                                               newDragPosition.getY() - relativeGrabPoint.getY() );
                velocitySensor.setPosition( modelLocation.getX(), modelLocation.getY() );
            }

            public void mouseReleased( PInputEvent event ) {
                relativeGrabPoint = null;
            }
        } );
        velocitySensor.addPositionObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( velocitySensor.getPosition() ) );
            }
        } );
        velocitySensor.addVelocityObserver( new SimpleObserver() {
            public void update() {
                child.setText( getText() );

                child.setOffset( -child.getFullBounds().getWidth() / 2, 0 );//Center the text under the the hot spot

                ImmutableVector2D velocity = velocitySensor.getVelocity().getValue();
                ImmutableVector2D viewVelocity = new ImmutableVector2D( transform.modelToViewDifferentialDouble( velocity.toPoint2D() ) );
                double velocityScale = 0.2;
                Point2D tip = viewVelocity.getScaledInstance( velocityScale ).toPoint2D();
                Point2D tail = viewVelocity.getScaledInstance( -1 * velocityScale ).toPoint2D();
                arrowNode.setTipAndTailLocations( tip, tail );
            }
        } );
    }

    private String getText() {
        return "v = " + new DecimalFormat( "0.00" ).format( pressureSensor.getVelocity().getValue().getMagnitude() );
    }
}
