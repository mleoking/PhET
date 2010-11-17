package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class PressureSensorNode extends PNode {
    ModelViewTransform2D transform;
    private PressureSensor pressureSensor;
    private final Property<Units.Unit> units;

    public PressureSensorNode( final ModelViewTransform2D transform, final PressureSensor pressureSensor, final Pool pool, Property<Units.Unit> units ) {
        this.transform = transform;
        this.pressureSensor = pressureSensor;
        this.units = units;
        double hotSpotRadius = 3;
        addChild( new PhetPPath( new Ellipse2D.Double( -hotSpotRadius, -hotSpotRadius, hotSpotRadius * 2, hotSpotRadius * 2 ), Color.red ) );
        final PText textNode = new PText( getText() ) {{
            setFont( new PhetFont( 18, true ) );
        }};
        addChild( textNode );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            private Point2D.Double relativeGrabPoint;

            public void mousePressed( PInputEvent event ) {
                updateGrabPoint( event );
            }

            private void updateGrabPoint( PInputEvent event ) {
                Point2D viewStartingPoint = event.getPositionRelativeTo( getParent() );
                Point2D viewCoordinateOfObject = transform.modelToView( pressureSensor.getX(), pressureSensor.getY() );
                relativeGrabPoint = new Point2D.Double( viewStartingPoint.getX() - viewCoordinateOfObject.getX(), viewStartingPoint.getY() - viewCoordinateOfObject.getY() );
            }

            public void mouseDragged( PInputEvent event ) {
                if ( relativeGrabPoint == null ) {
                    updateGrabPoint( event );
                }
                final Point2D newDragPosition = event.getPositionRelativeTo( getParent() );
                Point2D modelLocation = transform.viewToModel( newDragPosition.getX() - relativeGrabPoint.getX(),
                                                               newDragPosition.getY() - relativeGrabPoint.getY() );
                //TODO: refactor
                if ( pool != null ) {
                    pressureSensor.setPosition( modelLocation.getX(), Math.max( modelLocation.getY(), pool.getMinY() ) );//not allowed to go to negative Potential Energy
                    if ( pressureSensor.getPosition().getY() < 0 ) {
                        pressureSensor.setPosition( MathUtil.clamp( pool.getMinX(), modelLocation.getX(), pool.getMaxX() ), pressureSensor.getY() );//todo: use pool dimensions
                    }
                }
                else {
                    pressureSensor.setPosition( modelLocation.getX(), modelLocation.getY() );//not allowed to go to negative Potential Energy
                    if ( pressureSensor.getPosition().getY() < 0 ) {
                        pressureSensor.setPosition( modelLocation.getX(), pressureSensor.getY() );//todo: use pool dimensions
                    }
                }
            }

            public void mouseReleased( PInputEvent event ) {
                relativeGrabPoint = null;
            }
        } );
        pressureSensor.addPositionObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( pressureSensor.getPosition() ) );
            }
        } );
        final SimpleObserver updateText = new SimpleObserver() {
            public void update() {
                textNode.setText( getText() );
                textNode.setOffset( -textNode.getFullBounds().getWidth() / 2, -textNode.getFullBounds().getHeight() );
            }
        };
        pressureSensor.addPressureObserver( updateText );
        units.addObserver( updateText );
    }

    private String getText() {
        if ( Double.isNaN( pressureSensor.getPressure() ) ) {
            return "? " + units.getValue().getAbbreviation();
        }
        else {
            return "" + units.getValue().getDecimalFormat().format( units.getValue().siToUnit( pressureSensor.getPressure() ) ) + " " + units.getValue().getAbbreviation();
        }
    }
}
