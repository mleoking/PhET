// Copyright 2010-2011, University of Colorado

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
import edu.colorado.phet.gravityandorbits.model.CartoonPositionMap;
import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * BodyNode renders one piccolo PNode for a Body, which can be at cartoon or real scale.  It is also draggable, which changes
 * the location of the Body.
 *
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
        body.getCollidedProperty().addObserver( new SimpleObserver() {
            public void update() {
                setVisible( !body.getCollidedProperty().getValue() );
            }
        } );

        bodyRenderer = body.createRenderer( getViewDiameter() );
        addChild( bodyRenderer );

        final CursorHandler cursorHandler = new CursorHandler();

        //Add drag handlers
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
                            ImmutableVector2D x = new CartoonPositionMap( body.getCartoonOffsetScale() ).toReal( newCartoonPosition, body.getParent().getPosition() );
                            body.setPosition( x.getX(), x.getY() );
                        }
                    }
                    body.notifyUserModifiedPosition();
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
        body.getScaledPositionProperty().addObserver( updatePosition );//updates when body's parent moves too
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

    public Image renderImage( int width ) {
        return bodyRenderer.toImage( width, width, new Color( 0, 0, 0, 0 ) );
    }

    public Body getBody() {
        return body;
    }

    public BodyRenderer getBodyRenderer() {
        return bodyRenderer;
    }
}
