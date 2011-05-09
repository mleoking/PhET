// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
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
    private final BodyRenderer bodyRenderer;

    public BodyNode( final Body body,
                     final Property<ModelViewTransform> modelViewTransform,
                     final Property<ImmutableVector2D> mousePosition,//Keep track of the mouse position in case a body moves underneath a stationary mouse (in which case the mouse should become a hand cursor)
                     final PComponent parentComponent,
                     final double labelAngle ) {//Angle at which to show the name label, different for different BodyNodes so they don't overlap too much
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
        //TODO: Investigate using another mouse handler rather than overloading cursorHandler here.
        new RichSimpleObserver() {
            public void update() {
                /* we need to determine whether the mouse is over the body both before and after the model change so
                 * that we can toggle the hand pointer over the body.
                 *
                 * otherwise the body can move over the mouse and be dragged without ever seeing the hand pointer
                 */
                boolean isMouseOverBefore = bodyRenderer.getGlobalFullBounds().contains( mousePosition.getValue().toPoint2D() );
                setOffset( getPosition( modelViewTransform, body ).toPoint2D() );
                boolean isMouseOverAfter = bodyRenderer.getGlobalFullBounds().contains( mousePosition.getValue().toPoint2D() );

                //Send mouse entered and mouse exited events when body moves underneath a stationary mouse (in which case the mouse should become a hand cursor)
                if ( parentComponent != null ) {
                    if ( isMouseOverBefore && !isMouseOverAfter ) {
                        cursorHandler.mouseExited( new PInputEvent( null, null ) {
                            @Override public PComponent getComponent() {
                                return parentComponent;
                            }
                        } );
                    }
                    if ( !isMouseOverBefore && isMouseOverAfter ) {
                        cursorHandler.mouseEntered( new PInputEvent( null, null ) {
                            @Override public PComponent getComponent() {
                                return parentComponent;
                            }
                        } );
                    }
                }
            }
        }.observe( body.getPositionProperty(), modelViewTransform );

        new RichSimpleObserver() {
            public void update() {
                bodyRenderer.setDiameter( getViewDiameter() );
            }
        }.observe( body.getDiameterProperty(), modelViewTransform );

        //Points to the sphere with a text indicator and line, for when it is too small to see (in modes with realistic units)
        addChild( createArrowIndicator( body, labelAngle ) );
    }

    //Points to the sphere with a text indicator and line, for when it is too small to see (in modes with realistic units)
    private PNode createArrowIndicator( final Body body, final double labelAngle ) {
        return new PNode() {{
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
    }

    private ImmutableVector2D getPosition( Property<ModelViewTransform> modelViewTransform, Body body ) {
        return modelViewTransform.getValue().modelToView( body.getPosition() );
    }

    private double getViewDiameter() {
        double viewDiameter = modelViewTransform.getValue().modelToViewDeltaX( body.getDiameter() );
        return Math.max( viewDiameter, 2 );
    }

    //Create a new image at the specified width. Use body.createRenderer() instead of bodyRenderer since we must specify a new width value
    public Image renderImage( int width ) {
        return body.createRenderer( width ).toImage( width, width, new Color( 0, 0, 0, 0 ) );
    }

    public Body getBody() {
        return body;
    }

    public BodyRenderer getBodyRenderer() {
        return bodyRenderer;
    }
}