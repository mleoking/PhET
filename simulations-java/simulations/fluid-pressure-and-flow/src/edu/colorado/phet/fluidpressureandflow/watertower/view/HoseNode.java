// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
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

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.NOZZLE;
import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.JOIN_MITER;

/**
 * Piccolo node that shows the hose and allows the user to change the angle that water is sprayed out
 *
 * @author Sam Reid
 */
public class HoseNode extends PNode {
    private ModelViewTransform transform;
    private Hose hose;
    public final PImage nozzleImageNode;

    public HoseNode( final ModelViewTransform transform, final Hose hose ) {
        this.transform = transform;
        this.hose = hose;

        //Width of the hose in stage coordinates
        final double hoseWidth = (float) Math.abs( transform.modelToViewDeltaY( hose.holeSize ) ) * 1.5;

        //Create the node that shows the nozzle and allows the user to rotate it
        nozzleImageNode = new PImage( BufferedImageUtils.rescaleYMaintainAspectRatio( NOZZLE, (int) transform.modelToViewDeltaY( -hose.nozzleHeight ) ) ) {{
            hose.angle.addObserver( new VoidFunction1<Double>() {
                public void apply( Double angle ) {
                    final ImmutableVector2D origin = getViewOutputPoint();
                    setTransform( new AffineTransform() );
                    setOffset( origin.toPoint2D().getX() - getFullBounds().getWidth() / 2, origin.toPoint2D().getY() );
                    rotateAboutPoint( Math.PI / 2 - hose.angle.get(), getFullBounds().getWidth() / 2, 0 );
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
                    return new ImmutableVector2D( hose.outputPoint.toPoint2D(), transform.viewToModel( event.getPositionRelativeTo( getParent() ) ) ).getAngle();
                }

                //Drag the prism to rotate it
                public void mouseDragged( PInputEvent event ) {
                    double angle = getAngle( event );
                    final double delta = angle - previousAngle;
                    double desiredAngle = hose.angle.get() + delta;

                    //If the user drags the nozzle clockwise, it can jump to >2PI or Less than -1, which causes problems, so filter out this bogus data
                    while ( desiredAngle > 6 ) { desiredAngle = desiredAngle - Math.PI * 2; }
                    while ( desiredAngle < -1 ) { desiredAngle = desiredAngle + Math.PI * 2; }

                    //Then ensure the angle is between 0 and 90 degrees
                    final double newAngle = MathUtil.clamp( 0, desiredAngle, Math.PI / 2 );
                    hose.angle.set( newAngle );
                    previousAngle = angle;
                }
            } );
        }};

        addChild( new PhetPPath( Color.green, new BasicStroke( 1 ), Color.darkGray ) {{
            new RichSimpleObserver() {
                @Override public void update() {
                    final DoubleGeneralPath p = new DoubleGeneralPath( hose.attachmentPoint.get().getX(), hose.attachmentPoint.get().getY() + hose.holeSize / 2 ) {{

                        //Curve to a point up and right of the hole and travel all the way to the ground
                        ImmutableVector2D controlPointA1 = hose.attachmentPoint.get().plus( 5, 0 );
                        ImmutableVector2D controlPointA2 = controlPointA1.plus( -1, 0 );
                        ImmutableVector2D targetA = new ImmutableVector2D( controlPointA2.getX(), -1 );
                        curveTo( controlPointA1, controlPointA2, targetA );

                        //When hose is pointing up, control point should be down and to the right to prevent sharp angles
                        //When hose is pointing up, control point should be up and to the left to prevent sharp angles
                        double delta = Math.cos( hose.angle.get() - Math.PI / 2 );
                        final ImmutableVector2D intermediateDestination = new ImmutableVector2D( ( getNozzleInputModelPoint().getX() + controlPointA2.getX() ) / 2 + delta,
                                                                                                 ( -6 + getNozzleInputModelPoint().getY() ) / 2 - delta );
                        curveTo( new ImmutableVector2D( controlPointA2.getX() + 1, -6 ),
                                 new ImmutableVector2D( controlPointA2.getX() + 2, -6 ),
                                 intermediateDestination );

                        //Curve up to the meet the nozzle.  Using a quad here ensures that this pipe takes a smooth and straight path into the nozzle
                        ImmutableVector2D controlPointC1 = getNozzleInputModelPoint().minus( parseAngleAndMagnitude( 2, hose.angle.get() ) );
                        quadTo( controlPointC1, getNozzleInputModelPoint() );
                    }};

                    //Wrapping in an area gets rid of a kink when the water tower is low
                    setPathTo( new Area( new BasicStroke( (float) hoseWidth, CAP_BUTT, JOIN_MITER ).createStrokedShape( transform.modelToView( p.getGeneralPath() ) ) ) );
                }
            }.observe( hose.attachmentPoint, hose.angle );
        }} );
        hose.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        addChild( nozzleImageNode );

//        //Show an arrow that lets the user change the angle of the hose
//        //Fill with an invisible color so that it is see through but still draggable
//        addChild( new PhetPPath( new Color( 0, 0, 0, 0 ), new BasicStroke( 1 ), Color.darkGray ) {{
//            hose.angle.addObserver( new VoidFunction1<Double>() {
//                public void apply( Double angle ) {
//                    final ImmutableVector2D origin = getViewOutputPoint();
//                    setPathTo( new Arrow( origin.toPoint2D(), getViewDirection().times( 50 ).plus( origin ).toPoint2D(), 10, 10, 5 ).getShape() );
//                }
//            } );
//            addInputEventListener( new CursorHandler() );
//
//            //Make it possible to drag the angle of the hose
//            //Copied from PrismNode
//            addInputEventListener( new PBasicInputEventHandler() {
//                private double previousAngle;
//
//                //Store the original angle since rotations are computed as deltas between each event
//                public void mousePressed( PInputEvent event ) {
//                    previousAngle = getAngle( event );
//                }
//
//                //Find the angle about the center of rotation
//                private double getAngle( PInputEvent event ) {
//                    return new ImmutableVector2D( hose.outputPoint.toPoint2D(), transform.viewToModel( event.getPositionRelativeTo( getParent() ) ) ).getAngle();
//                }
//
//                //Drag the prism to rotate it
//                public void mouseDragged( PInputEvent event ) {
//                    double angle = getAngle( event );
//                    final double delta = angle - previousAngle;
//                    final double desiredAngle = hose.angle.get() + delta;
//                    final double newAngle = MathUtil.clamp( 0, desiredAngle, Math.PI / 2 );
//                    hose.angle.set( newAngle );
//                    previousAngle = angle;
//                }
//            } );
//        }} );
    }

    private ImmutableVector2D getViewOutputPoint() {
        return new ImmutableVector2D( transform.modelToView( hose.outputPoint.toPoint2D() ) );
    }

    private ImmutableVector2D getNozzleInputModelPoint() {
        return parseAngleAndMagnitude( hose.nozzleHeight, hose.angle.get() + Math.PI ).plus( hose.outputPoint );
    }
}