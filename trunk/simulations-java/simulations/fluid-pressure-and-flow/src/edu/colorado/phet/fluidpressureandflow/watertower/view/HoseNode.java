// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.watertower.model.Hose;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.*;
import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.Cursor.N_RESIZE_CURSOR;
import static java.lang.Math.PI;

/**
 * Piccolo node that shows the hose and allows the user to change the angle that water is sprayed out
 *
 * @author Sam Reid
 */
public class HoseNode extends PNode {
    public final PImage nozzleImageNode;
    private final BooleanProperty showDragHandles = new BooleanProperty( true );

    private static final boolean debugModelPosition = false;

    public HoseNode( final ModelViewTransform transform, final Hose hose ) {

        //Width of the hose in stage coordinates
        final double hoseViewWidth = (float) Math.abs( transform.modelToViewDeltaY( hose.holeSize ) ) * 1.5;

        //Create the node that shows the nozzle and allows the user to rotate it
        nozzleImageNode = new PImage( BufferedImageUtils.rescaleYMaintainAspectRatio( NOZZLE, (int) transform.modelToViewDeltaY( -Hose.NOZZLE_HEIGHT ) ) ) {{

            //When the hose angle or output point change, update the location and orientation of the nozzle image
            new RichSimpleObserver() {
                @Override public void update() {
                    final ImmutableVector2D origin = new ImmutableVector2D( transform.modelToView( hose.outputPoint.get().toPoint2D() ) );
                    setTransform( new AffineTransform() );
                    setOffset( origin.toPoint2D().getX() - getFullBounds().getWidth() / 2, origin.toPoint2D().getY() );
                    rotateAboutPoint( PI / 2 - hose.angle.get(), getFullBounds().getWidth() / 2, 0 );
                }
            }.observe( hose.angle, hose.outputPoint );

        }};

        PNode hoseUpHandle = new PNode() {{
            addChild( new PImage( PIPE_HANDLE_1 ) );
            setScale( 0.78 );
            new RichSimpleObserver() {
                @Override public void update() {
                    final Point2D.Double viewPoint = transform.modelToView( new HoseGeometry( hose ).getHandlePoint() ).toPoint2D();
                    setOffset( viewPoint.getX() - getFullBounds().getWidth() / 2, viewPoint.getY() - getFullBounds().getHeight() - hoseViewWidth / 2 );
                }
            }.observe( hose.angle, hose.y );

            //Make it possible to T-shaped drag handle to change the elevation
            addInputEventListener( new CursorHandler( Cursor.getPredefinedCursor( N_RESIZE_CURSOR ) ) );
            addInputEventListener( new SimSharingDragHandler( UserComponents.hoseHandle, true ) {
                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    final double newY = getNewYValue( event, transform, hose );
                    hose.y.set( newY );
                    showDragHandles.set( false );
                }

                @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                    return super.getParametersForAllEvents( event ).with( ParameterKeys.y, getNewYValue( event, transform, hose ) );
                }
            } );
        }};
        addChild( hoseUpHandle );

        //Knob on the side of the nozzle for rotating it
        PNode nozzleRotationKnob = new PNode() {{
            addChild( new PImage( BufferedImageUtils.flipX( KNOB ) ) );
            new RichSimpleObserver() {
                @Override public void update() {
                    final ImmutableVector2D tail = transform.modelToView( hose.getNozzleInputPoint().plus( hose.getUnitDirectionVector() ) );
                    double angle = -hose.angle.get() + Math.PI / 2;
                    setOffset( tail.getX(), tail.getY() );
                    setRotation( angle );
                    translate( hoseViewWidth / 2, -10 );
                }
            }.observe( hose.angle, hose.outputPoint, hose.y );

            //Listen for whether the user is interacting with the nozzle.  This is true if the user moused over the nozzle or if they are dragging it.
            addInputEventListener( new PBasicInputEventHandler() {
                boolean entered = false;
                boolean pressed = false;

                @Override public void mouseEntered( PInputEvent event ) {
                    entered = true;
                }

                @Override public void mouseExited( PInputEvent event ) {
                    entered = false;
                }

                @Override public void mousePressed( PInputEvent event ) {
                    pressed = true;
                }

                @Override public void mouseReleased( PInputEvent event ) {
                    pressed = false;
                }
            } );
            addInputEventListener( new CursorHandler() );

            //Make it possible to drag the angle of the hose
            //Copied from PrismNode
            addInputEventListener( new SimSharingDragHandler( UserComponents.hoseNozzle, true ) {
                private double previousAngle;

                //Store the original angle since rotations are computed as deltas between each event
                @Override protected void startDrag( final PInputEvent event ) {
                    super.startDrag( event );
                    previousAngle = getAngle( event );
                }

                //Find the angle about the center of rotation
                private double getAngle( PInputEvent event ) {
                    return new ImmutableVector2D( hose.outputPoint.get().toPoint2D(), transform.viewToModel( event.getPositionRelativeTo( getParent() ) ) ).getAngle();
                }

                @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                    return super.getParametersForAllEvents( event ).with( ParameterKeys.angle, getNewAngle( getAngle( event ) ) );
                }

                //Drag the nozzle to rotate it
                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    double angle = getAngle( event );
                    hose.angle.set( getNewAngle( angle ) );
                    previousAngle = angle;
                }

                private double getNewAngle( final double angle ) {
                    final double delta = angle - previousAngle;
                    double desiredAngle = hose.angle.get() + delta;

                    //If the user drags the nozzle clockwise, it can jump to >2PI or Less than -1, which causes problems, so filter out this bogus data
                    while ( desiredAngle > 6 ) { desiredAngle = desiredAngle - PI * 2; }
                    while ( desiredAngle < -1 ) { desiredAngle = desiredAngle + PI * 2; }

                    //Then ensure the angle is between 0 and 90 degrees
                    return MathUtil.clamp( 0, desiredAngle, PI / 2 );
                }
            } );
        }};
        addChild( nozzleRotationKnob );

        //Utility for experimentally finding good model positions for the drag handles
        if ( debugModelPosition ) {
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseMoved( PInputEvent event ) {
                    Point2D pos = event.getPositionRelativeTo( getParent() );
                    System.out.println( "transform.viewToModel( pos ) = " + transform.viewToModel( pos ) );
                }
            } );
        }

        //The graphic for the hose itself
        addChild( new PhetPPath( Color.green, new BasicStroke( 1 ), Color.darkGray ) {{
            new RichSimpleObserver() {
                @Override public void update() {
                    final DoubleGeneralPath p = new DoubleGeneralPath() {{
                        final HoseGeometry hoseGeometry = new HoseGeometry( hose );
                        moveTo( hoseGeometry.startPoint );

                        //Curve amount is the distance that control points are placed from the destinations, should be higher when there is more distance to cover, but clamped at 1 to not get too curvy
                        double curveAmount = MathUtil.clamp( -1, hoseGeometry.bottomLeft.getY() - hoseGeometry.rightOfTower.getY(), 1 );

                        //If the curve amount is small, just make a linear segment--otherwise there is an unusual looking kink in the geometry
                        if ( Math.abs( curveAmount ) < 0.75 ) {
                            lineTo( hoseGeometry.bottomLeft );
                        }
                        else {
                            //Curve to the right and down
                            lineTo( hoseGeometry.rightOfTower.plus( -1, 0 ) );
                            quadTo( hoseGeometry.rightOfTower, hoseGeometry.rightOfTower.plus( 0, curveAmount ) );

                            //Curve down to the right
                            lineTo( hoseGeometry.bottomLeft.plus( 0, -curveAmount ) );
                            quadTo( hoseGeometry.bottomLeft, hoseGeometry.bottomLeft.plus( 1, 0 ) );
                        }

                        //line toward the prePoint (just before the nozzle), and continue to the nozzle input
                        lineTo( hoseGeometry.prePoint.plus( -1, 0 ) );
                        quadTo( hoseGeometry.prePoint, hoseGeometry.nozzleInput );
                    }};

                    //Wrapping in an area gets rid of a kink when the water tower is low
                    setPathTo( new Area( new BasicStroke( (float) hoseViewWidth, CAP_BUTT, JOIN_MITER ).createStrokedShape( transform.modelToView( p.getGeneralPath() ) ) ) );
                }
            }.observe( hose.attachmentPoint, hose.angle, hose.y, hose.attachmentPoint );
        }} );
        hose.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
        addChild( nozzleImageNode );
    }

    private double getNewYValue( final PInputEvent event, final ModelViewTransform transform, final Hose hose ) {
        double modelDelta = transform.viewToModelDeltaY( event.getDeltaRelativeTo( getParent() ).getHeight() );
        return MathUtil.clamp( 0, hose.y.get() + modelDelta, 30 );
    }
}