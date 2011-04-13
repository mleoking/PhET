// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
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
 * BodyNode renders one piccolo PNode for a Body, which can be at cartoon or real scale.  It is also draggable, which changes
 * the location of the Body.
 *
 * @author Sam Reid
 */
public class BodyNode extends PNode {
    private final Property<ModelViewTransform> modelViewTransform;
    private final Body body;
    private final PNode arrowIndicator; //REVIEW this can be converted to a local variable
    private final BodyRenderer bodyRenderer;

    //REVIEW inconsistent naming conventions for Property, eg modelViewTransform and mousePositionProperty
    //REVIEW describe mousePositionProperty and labelAngle
    public BodyNode( final Body body, final Property<ModelViewTransform> modelViewTransform,
                     final Property<ImmutableVector2D> mousePositionProperty, final PComponent parentComponent, final double labelAngle ) {
        this.modelViewTransform = modelViewTransform;
        this.body = body;
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
                    final Dimension2D delta = modelViewTransform.getValue().viewToModelDelta( event.getDeltaRelativeTo( getParent() ) );
                    body.translate( new Point2D.Double( delta.getWidth(), delta.getHeight() ) );
                    body.notifyUserModifiedPosition();
                }

                @Override
                public void mouseReleased( PInputEvent event ) {
                    body.setUserControlled( false );
                }
            } );
        }
        //REVIEW I would use another mouse handler rather than overloading cursorHandler here.
        //REVIEW naming: updatePosition sounds like a function name, how about positionObserver?
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
                //REVIEW doc. what's going on here? Why are you feeding cursorHandler manufactured events?
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

        //REVIEW naming: updatePosition sounds like a function name, how about diameterObserver?
        final SimpleObserver updateDiameter = new SimpleObserver() {
            public void update() {
                bodyRenderer.setDiameter( getViewDiameter() );
            }
        };
        body.getDiameterProperty().addObserver( updateDiameter );
        modelViewTransform.addObserver( updateDiameter );

        //REVIEW this should be a class, why would you want to inline this much initialization? It's noisy for the reader.
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
            final PropertyChangeListener updateVisibility = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    setVisible( bodyRenderer.getGlobalFullBounds().getWidth() <= 10 );
                }
            };
            bodyRenderer.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updateVisibility );
            updateVisibility.propertyChange( null );
        }};
        addChild( arrowIndicator );
    }

    private ImmutableVector2D getPosition( Property<ModelViewTransform> modelViewTransform, Body body ) {
        return modelViewTransform.getValue().modelToView( body.getPosition() );
    }

    private double getViewDiameter() {
        double viewDiameter = modelViewTransform.getValue().modelToViewDeltaX( body.getDiameter() );
        return Math.max( viewDiameter, 2 );
    }

    public Image renderImage( int width ) {
        return body.createRenderer( width ).toImage( width, width, new Color( 0, 0, 0, 0 ) );
    }

    public Body getBody() {
        return body;
    }

    //REVIEW why doesn't renderImage use bodyRenderer? Is there a different between this renderer and the one used in renderImage?
    public BodyRenderer getBodyRenderer() {
        return bodyRenderer;
    }
}