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
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.watertower.model.Hose;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.createPolar;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.NOZZLE;
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
    private Hose hose;
    public final PImage nozzleImageNode;
    private final BooleanProperty showDragHandles = new BooleanProperty( true );

    //Rotation handles should only be shown if the user is moused over (or dragging) the nozzle, or if the user hasn't dragged it yet
    private final BooleanProperty showRotationHandles = new BooleanProperty( true );
    private final BooleanProperty mouseOverNozzle = new BooleanProperty( false );
    private final And showCounterClockwiseArrow;
    private final And showClockwiseArrow;

    private boolean debugModelPosition = false;

    public HoseNode( final ModelViewTransform transform, final Hose hose ) {
        this.hose = hose;

        //Rotation handles should only be shown if the user is moused over (or dragging) the nozzle, or if the user hasn't dragged it yet
        showCounterClockwiseArrow = ( showRotationHandles.or( mouseOverNozzle ) ).and( hose.angle.lessThan( Math.PI / 2 ) );
        showClockwiseArrow = ( showRotationHandles.or( mouseOverNozzle ) ).and( hose.angle.greaterThan( 0.0 ) );

        //Width of the hose in stage coordinates
        final double hoseWidth = (float) Math.abs( transform.modelToViewDeltaY( hose.holeSize ) ) * 1.5;

        //Create the node that shows the nozzle and allows the user to rotate it
        nozzleImageNode = new PImage( BufferedImageUtils.rescaleYMaintainAspectRatio( NOZZLE, (int) transform.modelToViewDeltaY( -hose.nozzleHeight ) ) ) {{

            //When the hose angle or output point change, update the location and orientation of the nozzle image
            new RichSimpleObserver() {
                @Override public void update() {
                    final ImmutableVector2D origin = new ImmutableVector2D( transform.modelToView( hose.outputPoint.get().toPoint2D() ) );
                    setTransform( new AffineTransform() );
                    setOffset( origin.toPoint2D().getX() - getFullBounds().getWidth() / 2, origin.toPoint2D().getY() );
                    rotateAboutPoint( PI / 2 - hose.angle.get(), getFullBounds().getWidth() / 2, 0 );
                }
            }.observe( hose.angle, hose.outputPoint );

            //Listen for whether the user is interacting with the nozzle.  This is true if the user moused over the nozzle or if they are dragging it.
            addInputEventListener( new PBasicInputEventHandler() {
                boolean entered = false;
                boolean pressed = false;

                @Override public void mouseEntered( PInputEvent event ) {
                    entered = true;
                    mouseOverNozzle.set( entered || pressed );
                }

                @Override public void mouseExited( PInputEvent event ) {
                    entered = false;
                    mouseOverNozzle.set( entered || pressed );
                }

                @Override public void mousePressed( PInputEvent event ) {
                    pressed = true;
                    mouseOverNozzle.set( entered || pressed );
                }

                @Override public void mouseReleased( PInputEvent event ) {
                    pressed = false;
                    mouseOverNozzle.set( entered || pressed );
                }
            } );
            addInputEventListener( new CursorHandler() );

            //Make it possible to drag the angle of the hose
            //Copied from PrismNode
            addInputEventListener( new PBasicInputEventHandler() {
                private double previousAngle;

                //Store the original angle since rotations are computed as deltas between each event
                public void mousePressed( PInputEvent event ) {
                    previousAngle = getAngle( event );
                }

                //Find the angle about the center of rotation
                private double getAngle( PInputEvent event ) {
                    return new ImmutableVector2D( hose.outputPoint.get().toPoint2D(), transform.viewToModel( event.getPositionRelativeTo( getParent() ) ) ).getAngle();
                }

                //Drag the nozzle to rotate it
                public void mouseDragged( PInputEvent event ) {
                    showRotationHandles.set( false );
                    double angle = getAngle( event );
                    final double delta = angle - previousAngle;
                    double desiredAngle = hose.angle.get() + delta;

                    //If the user drags the nozzle clockwise, it can jump to >2PI or Less than -1, which causes problems, so filter out this bogus data
                    while ( desiredAngle > 6 ) { desiredAngle = desiredAngle - PI * 2; }
                    while ( desiredAngle < -1 ) { desiredAngle = desiredAngle + PI * 2; }

                    //Then ensure the angle is between 0 and 90 degrees
                    final double newAngle = MathUtil.clamp( 0, desiredAngle, PI / 2 );
                    hose.angle.set( newAngle );
                    previousAngle = angle;
                }
            } );
        }};

        //Length of arrow for drag handles
        final double dragArrowLength = 35;

        //Show translate handles on the hose, position experimentally sampled since it is difficult to find the position in bezier and cubic curves using the java api
        //Make it update when the user rotates the nozzle
        PNode dragHandles = new PNode() {{
            new RichSimpleObserver() {
                @Override public void update() {
                    removeAllChildren();

                    //Experimentally determined heuristics for where to put the drag handle since it is difficult to find the position in bezier and cubic curves using the java api
                    final ImmutableVector2D dragArrowTail = transform.modelToView( getIntermediateDestination() );
                    double offsetX = -10;
                    double offsetY = 10;

                    //Make it longer when the nozzle rotates since the hose would hide the arrow when the nozzle points right
                    addChild( new TranslationDragHandle( dragArrowTail.plus( offsetX, offsetY ), new ImmutableVector2D( 0, -dragArrowLength * 1.2 - dragArrowLength * Math.cos( hose.angle.get() ) * 0.8 ), showDragHandles ) );
                }
            }.observe( hose.angle );
        }};
        addChild( dragHandles );

        //Show rotate handles on base of the the nozzle, that move when the nozzle moves
        PNode rotationHandles = new PNode() {{
            new RichSimpleObserver() {
                @Override public void update() {
                    removeAllChildren();

                    final ImmutableVector2D tail = transform.modelToView( hose.getNozzleInputPoint().plus( hose.getUnitDirectionVector().times( 0.13 * hose.nozzleHeight ) ) );
                    ImmutableVector2D direction = createPolar( dragArrowLength, -hose.angle.get() + Math.PI / 2 );
                    addChild( new TranslationDragHandle( tail, direction, showCounterClockwiseArrow ) );
                    addChild( new TranslationDragHandle( tail, direction.times( -1 ), showClockwiseArrow ) );
                }
            }.observe( hose.angle, hose.outputPoint, hose.y );
        }};
        addChild( rotationHandles );

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
                    final DoubleGeneralPath p = new DoubleGeneralPath( hose.attachmentPoint.get().getX(), hose.attachmentPoint.get().getY() + hose.holeSize / 2 ) {{

                        //Move the hose up by this much, but clamp before it gets too high because it can cause non-smoothness
                        double up = Math.min( 14, hose.y.get() );

                        //Curve to a point up and right of the hole and travel all the way to the ground
                        ImmutableVector2D controlPointA1 = hose.attachmentPoint.get().plus( 5, 0 );
                        ImmutableVector2D controlPointA2 = controlPointA1.plus( -1, 0 );
                        ImmutableVector2D targetA = new ImmutableVector2D( controlPointA2.getX(), -1 ).plus( 0, up );
                        curveTo( controlPointA1, controlPointA2, targetA );

                        //When hose is pointing up, control point should be down and to the right to prevent sharp angles
                        //When hose is pointing up, control point should be up and to the left to prevent sharp angles
                        final ImmutableVector2D intermediateDestination = getIntermediateDestination();
                        curveTo( new ImmutableVector2D( controlPointA2.getX() + 1, -6 ).plus( 0, up ),
                                 new ImmutableVector2D( controlPointA2.getX() + 2, -6 ).plus( 0, up ),
                                 intermediateDestination.plus( 0, up / 2 ) );

                        //Curve up to the meet the nozzle.  Using a quad here ensures that this pipe takes a smooth and straight path into the nozzle
                        ImmutableVector2D controlPointC1 = hose.getNozzleInputPoint().minus( createPolar( 2, hose.angle.get() ) );
                        quadTo( controlPointC1, hose.getNozzleInputPoint() );
                    }};

                    //Wrapping in an area gets rid of a kink when the water tower is low
                    setPathTo( new Area( new BasicStroke( (float) hoseWidth, CAP_BUTT, JOIN_MITER ).createStrokedShape( transform.modelToView( p.getGeneralPath() ) ) ) );
                }
            }.observe( hose.attachmentPoint, hose.angle, hose.y );

            //Make it possible to drag hose itself to change the elevation
            addInputEventListener( new CursorHandler( Cursor.getPredefinedCursor( N_RESIZE_CURSOR ) ) );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    double modelDelta = transform.viewToModelDeltaY( event.getDeltaRelativeTo( getParent() ).getHeight() );
                    hose.y.set( MathUtil.clamp( 0, hose.y.get() + modelDelta, 30 ) );
                    showDragHandles.set( false );
                }
            } );
        }} );
        hose.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
        addChild( nozzleImageNode );
    }

    //Gets one of the hose control points, for use in showing the drag handle
    private ImmutableVector2D getIntermediateDestination() {

        ImmutableVector2D controlPointA1 = hose.attachmentPoint.get().plus( 5, 0 );
        ImmutableVector2D controlPointA2 = controlPointA1.plus( -1, 0 );

        double delta = Math.cos( hose.angle.get() - PI / 2 );
        return new ImmutableVector2D( ( hose.getNozzleInputPoint().getX() + controlPointA2.getX() ) / 2 + delta, -3 + hose.getNozzleInputPoint().getY() / 2 - delta );
    }
}