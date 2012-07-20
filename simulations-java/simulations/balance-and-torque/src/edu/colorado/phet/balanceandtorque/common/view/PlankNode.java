// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.balanceandtorque.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing;
import edu.colorado.phet.balanceandtorque.common.model.Plank;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Graphic for the plank.  Draws tick marks and provides handler for grabbing
 * the plank and setting its position.
 *
 * @author John Blanco
 */
public class PlankNode extends ModelObjectNode {
    private static final Stroke NORMAL_TICK_MARK_STROKE = new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
    private static final Stroke NORMAL_HIGHLIGHT_STROKE = new BasicStroke( 11, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
    private static final Stroke BOLD_TICK_MARK_STROKE = new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
    private static final Stroke BOLD_HIGHLIGHT_STROKE = new BasicStroke( 11, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
    private static final Color HIGHLIGHT_COLOR = Color.WHITE;
    private ModelViewTransform mvt;
    private Plank plank;
    private PhetPCanvas canvas;

    public PlankNode( final ModelViewTransform mvt, final Plank plank, PhetPCanvas canvas ) {
        super( mvt, plank, new Color( 243, 203, 127 ) );
        this.mvt = mvt;
        this.plank = plank;
        this.canvas = canvas;

        // Create a layer for the tick marks and add the code to create and
        // update them.
        final PNode tickMarkLayer = new PNode();
        addChild( tickMarkLayer );
        plank.addShapeObserver( new VoidFunction1<Shape>() {
            public void apply( Shape rotatedPlankShape ) {
                updateTickMarks( tickMarkLayer, plank, mvt );
            }
        } );
        plank.massesOnSurface.addElementAddedObserver( new VoidFunction1<Mass>() {
            public void apply( Mass mass ) {
                updateTickMarks( tickMarkLayer, plank, mvt );
            }
        } );
        plank.massesOnSurface.addElementRemovedObserver( new VoidFunction1<Mass>() {
            public void apply( Mass mass ) {
                updateTickMarks( tickMarkLayer, plank, mvt );
            }
        } );

        // Create a layer for some grabbable "handles" that will allow the
        // user to manually change the angle of the plank.
        if ( plank.getTiltAngle() == 0 ) {// Tilt angle be zero for this to work.
            addTiltHandles();
        }
    }

    /**
     * Add the "tilt handles" that allow the user to tilt the plank.  The tilt
     * handles are not visible to the
     */
    private void addTiltHandles() {
        final PNode handleLayer = new PNode();
        addChild( handleLayer );
        // Use a fully transparent color to make the handles invisible.  This
        // can be made opaque for debugging if needed.
        Color handleColor = new Color( 255, 255, 255, 0 );
        // Only put the handles on the ends of the plank, otherwise things get
        // weird.  Note that the handles are invisible.
        Rectangle2D plankBounds = plank.getShape().getBounds2D();
        final PNode rightHandle = new PhetPPath( new Rectangle2D.Double( mvt.modelToViewDeltaX( plankBounds.getWidth() / 6 ),
                                                                         -mvt.modelToViewDeltaY( plankBounds.getHeight() ),
                                                                         mvt.modelToViewDeltaX( plankBounds.getWidth() / 3 ),
                                                                         -mvt.modelToViewDeltaY( plankBounds.getHeight() ) ),
                                                 handleColor );
        rightHandle.setOffset( mvt.modelToView( plank.getPivotPoint() ) );
        rightHandle.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        handleLayer.addChild( rightHandle );

        final PNode leftHandle = new PhetPPath( new Rectangle2D.Double( -mvt.modelToViewDeltaX( plankBounds.getWidth() / 2 ),
                                                                        -mvt.modelToViewDeltaY( plankBounds.getHeight() ),
                                                                        mvt.modelToViewDeltaX( plankBounds.getWidth() / 3 ),
                                                                        -mvt.modelToViewDeltaY( plankBounds.getHeight() ) ),
                                                handleColor );
        leftHandle.setOffset( mvt.modelToView( plank.getPivotPoint() ) );
        leftHandle.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        handleLayer.addChild( leftHandle );

        plank.addShapeObserver( new VoidFunction1<Shape>() {
            public void apply( Shape rotatedPlankShape ) {
                // Rotate the handles to match the plank's angle.
                rightHandle.setRotation( -plank.getTiltAngle() );
                leftHandle.setRotation( -plank.getTiltAngle() );
            }
        } );

        rightHandle.addInputEventListener( new TiltHandleDragHandler( plank, canvas, mvt ) );
        leftHandle.addInputEventListener( new TiltHandleDragHandler( plank, canvas, mvt ) );
    }

    private void updateTickMarks( PNode tickMarkLayer, Plank plank, ModelViewTransform mvt ) {
        // Update the tick marks by removing them and redrawing them.
        tickMarkLayer.removeAllChildren();
        for ( int i = 0; i < plank.getTickMarks().size(); i++ ) {
            Stroke tickMarkStroke = NORMAL_TICK_MARK_STROKE;
            Stroke highlightStroke = NORMAL_HIGHLIGHT_STROKE;
            if ( i % 2 == 0 ) {
                // Make some marks bold for easier placement of masses.
                // The 'if' clause can be tweaked to put marks in
                // different places.
                tickMarkStroke = BOLD_TICK_MARK_STROKE;
                highlightStroke = BOLD_HIGHLIGHT_STROKE;
            }
            if ( plank.isTickMarkOccupied( plank.getTickMarks().get( i ) ) ) {
                tickMarkLayer.addChild( new PhetPPath( mvt.modelToView( plank.getTickMarks().get( i ) ), highlightStroke, HIGHLIGHT_COLOR ) );
            }
            tickMarkLayer.addChild( new PhetPPath( mvt.modelToView( plank.getTickMarks().get( i ) ), tickMarkStroke, Color.BLACK ) );
        }
    }

    /**
     * Class that defines the handler for the drag handles on the ends of the
     * plank.
     */
    private static class TiltHandleDragHandler extends PDragEventHandler {
        private final Plank plank;
        private final ModelViewTransform mvt;
        private final PhetPCanvas canvas;
        private double dragAngleDelta;

        public TiltHandleDragHandler( Plank plank, PhetPCanvas canvas, ModelViewTransform mvt ) {
            this.plank = plank;
            this.canvas = canvas;
            this.mvt = mvt;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            // The user wants to move this.
            plank.userControlled.set( true );
            dragAngleDelta = plank.getTiltAngle() - getMouseToPlankCenterAngle( event.getCanvasPosition() );
        }

        @Override
        public void mouseDragged( PInputEvent event ) {
            plank.setTiltAngle( getMouseToPlankCenterAngle( event.getCanvasPosition() ) + dragAngleDelta );
            // Send user message indicating that the user dragged the plank.
            SimSharingManager.sendUserMessage( BalanceAndTorqueSimSharing.ModelComponents.plank,
                                               UserComponentTypes.sprite,
                                               UserActions.drag,
                                               new ParameterSet( new Parameter( BalanceAndTorqueSimSharing.ParameterKeys.plankTiltAngle, plank.getTiltAngle() ) ) );
        }

        @Override protected void endDrag( PInputEvent event ) {
            super.endDrag( event );
            // The user is no longer controlling this.
            plank.userControlled.set( false );
        }

        /**
         * Convert the canvas position to the corresponding location in the
         * model.
         */
        private Point2D getModelPosition( Point2D canvasPos ) {
            Point2D worldPos = new Point2D.Double( canvasPos.getX(), canvasPos.getY() );
            canvas.getPhetRootNode().screenToWorld( worldPos );
            return mvt.viewToModel( worldPos );
        }

        double getMouseToPlankCenterAngle( Point2D mouseCanvasPosition ) {
            Point2D modelStartDragPosition = getModelPosition( mouseCanvasPosition );
            MutableVector2D vectorToMouseLocation = new MutableVector2D( modelStartDragPosition.getX() - plank.getPlankSurfaceCenter().getX(),
                                                                         modelStartDragPosition.getY() - plank.getPlankSurfaceCenter().getY() );
            if ( Math.abs( vectorToMouseLocation.getAngle() ) > Math.PI / 2 ) {
                // Do a 180 on the vector to avoid problems with getting the
                // angle from the left side of the rotation point.
                vectorToMouseLocation.rotate( Math.PI );
            }
            return vectorToMouseLocation.getAngle();
        }
    }
}
