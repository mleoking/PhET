package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class BodyNode extends PNode {
    private final Property<ModelViewTransform> modelViewTransform;
    private final Body body;
    private final PNode arrowIndicator;
    private final Property<Scale> scaleProperty;
    private final BodyRenderer bodyRenderer;

    public BodyNode( final Body body, final Property<ModelViewTransform> modelViewTransform, final Property<Scale> scaleProperty,
                     final Property<ImmutableVector2D> mousePositionProperty, final PComponent parentComponent, final double labelAngle ) {
        this.modelViewTransform = modelViewTransform;
        this.body = body;
        this.scaleProperty = scaleProperty;

        bodyRenderer = body.createRenderer( getViewDiameter() );
        addChild( bodyRenderer );

        final CursorHandler cursorHandler = new CursorHandler();
        if ( body.isDraggable() ) {
            addInputEventListener( cursorHandler );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override
                public void mousePressed( PInputEvent event ) {
                    body.setUserControlled( true );
                }

                @Override
                public void mouseDragged( PInputEvent event ) {
                    if ( scaleProperty.getValue() == Scale.REAL ) {
                        final Dimension2D delta = modelViewTransform.getValue().viewToModelDelta( event.getDeltaRelativeTo( getParent() ) );
                        body.translate( new Point2D.Double( delta.getWidth(), delta.getHeight() ) );
                    }
                    else {
                        final Dimension2D cartoonDelta = modelViewTransform.getValue().viewToModelDelta( event.getDeltaRelativeTo( getParent() ) );
                        ImmutableVector2D newCartoonPosition = body.getCartoonPosition().getAddedInstance( cartoonDelta.getWidth(), cartoonDelta.getHeight() );
                        //find the physical coordinates so that the body will have
                        if ( body.getParent() == null ) {
                            body.setPosition( newCartoonPosition.getX(), newCartoonPosition.getY() );
                        }
                        else {

                            //solve for x given cartoonx:
                            //cartoonx = parent.x+(x - parent.x) * scale
                            //cartoonx = parent.x+x*scale - parent.x*scale
                            //(cartoonx -parent.x+parent.x*scale)/scale = x
                            //TODO: convert this program to scala
                            final double scale = body.getCartoonOffsetScale();
                            final ImmutableVector2D parentX = body.getParent().getPosition();
                            ImmutableVector2D x = ( newCartoonPosition.getSubtractedInstance( parentX ).getAddedInstance( parentX.getScaledInstance( scale ) ) );
                            x = x.getScaledInstance( 1.0 / scale );
                            body.setPosition( x.getX(), x.getY() );
                        }
                    }
                }

                @Override
                public void mouseReleased( PInputEvent event ) {
                    body.setUserControlled( false );
                }
            } );
        }
        final SimpleObserver updatePosition = new SimpleObserver() {
            public void update() {
                /* we need to determine whether the mouse is over the body both before and after the model change so
                 * that we can toggle the hand pointer over the body.
                 *
                 * otherwise the body can move over the mouse and be dragged without ever seeing the hand pointer
                 */
                boolean isMouseOverBefore = bodyRenderer.getGlobalFullBounds().contains( mousePositionProperty.getValue().toPoint2D() );
                setOffset( getPosition( modelViewTransform, body ).toPoint2D() );
                boolean isMouseOverAfter = bodyRenderer.getGlobalFullBounds().contains( mousePositionProperty.getValue().toPoint2D() );
                if ( parentComponent != null && body.isDraggable() ) {
                    if ( isMouseOverBefore && !isMouseOverAfter ) {
                        cursorHandler.mouseExited( new PInputEvent( null, null ) {
                            @Override
                            public PComponent getComponent() {
                                return parentComponent;
                            }
                        } );
                    }
                    if ( !isMouseOverBefore && isMouseOverAfter ) {
                        cursorHandler.mouseEntered( new PInputEvent( null, null ) {
                            @Override
                            public PComponent getComponent() {
                                return parentComponent;
                            }
                        } );
                    }
                }
            }
        };
        body.getPositionProperty().addObserver( updatePosition );
        modelViewTransform.addObserver( updatePosition );
        scaleProperty.addObserver( updatePosition );

        final SimpleObserver updateDiameter = new SimpleObserver() {
            public void update() {
                bodyRenderer.setDiameter( getViewDiameter() );
            }
        };
        body.getDiameterProperty().addObserver( updateDiameter );
        scaleProperty.addObserver( updateDiameter );
        modelViewTransform.addObserver( updateDiameter );

        //Points to the sphere with a text indicator and line, for when it is too small to see (in modes with realistic units)
        arrowIndicator = new PNode() {{
            Point2D viewCenter = new Point2D.Double( 0, 0 );
            ImmutableVector2D northEastVector = ImmutableVector2D.parseAngleAndMagnitude( 1, labelAngle );
            Point2D tip = northEastVector.getScaledInstance( 10 ).getDestination( viewCenter );
            final Point2D tail = northEastVector.getScaledInstance( 50 ).getDestination( viewCenter );

            addChild( new ArrowNode( tail, tip, 0, 0, 3 ) {{
                setPaint( Color.yellow );
            }} );
            addChild( new PText( body.getName() ) {{
                setOffset( tail.getX() - getFullBounds().getWidth() / 2 - 5, tail.getY() - getFullBounds().getHeight() - 10 );
                setTextPaint( Color.white );
                setFont( new PhetFont( 18, true ) );
            }} );
        }};
        addChild( arrowIndicator );
        scaleProperty.addObserver( new SimpleObserver() {
            public void update() {
                arrowIndicator.setVisible( scaleProperty.getValue().getShowLabelArrows() );
            }
        } );
    }

    private ImmutableVector2D getPosition( Property<ModelViewTransform> modelViewTransform, Body body ) {
        return modelViewTransform.getValue().modelToView( body.getPosition( scaleProperty.getValue() ) );
    }

    private double getViewDiameter() {
        double viewDiameter = modelViewTransform.getValue().modelToViewDeltaX( body.getDiameter() );
        if ( scaleProperty.getValue() == Scale.CARTOON ) {
            viewDiameter = viewDiameter * body.getCartoonDiameterScaleFactor();
        }
        return Math.max( viewDiameter, 2 );
    }

    public Image sphereNodeToImage() {
        return bodyRenderer.toImage();
    }

    public Body getBody() {
        return body;
    }
}
