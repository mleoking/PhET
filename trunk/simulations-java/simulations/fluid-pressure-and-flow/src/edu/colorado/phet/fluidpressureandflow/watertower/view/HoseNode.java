// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.watertower.model.Hose;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.JOIN_MITER;

/**
 * Piccolo node that shows the hose and allows the user to change the angle that water is sprayed out
 *
 * @author Sam Reid
 */
public class HoseNode extends PNode {
    public HoseNode( final ModelViewTransform transform, final Hose hose ) {
        addChild( new PhetPPath() );

        addChild( new PhetPPath( Color.green, new BasicStroke( 1 ), Color.darkGray ) {{
            hose.attachmentPoint.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D point ) {
                    final DoubleGeneralPath p = new DoubleGeneralPath( hose.attachmentPoint.get().getX(), hose.attachmentPoint.get().getY() + hose.holeSize / 2 ) {{

                        //Curve to a point up and right of the hole and travel all the way to the ground
                        ImmutableVector2D controlPointA1 = hose.attachmentPoint.get().plus( 5, 0 );
                        ImmutableVector2D controlPointA2 = controlPointA1.plus( -1, 0 );
                        ImmutableVector2D targetA = new ImmutableVector2D( controlPointA2.getX(), 0 );
                        curveTo( controlPointA1, controlPointA2, targetA );

                        //Make a U-shape underground to point
                        //First, curve down and to the right
                        ImmutableVector2D controlPointB1 = targetA.plus( 0, -2 );
                        ImmutableVector2D targetB = targetA.plus( 2, -2 );
                        quadTo( controlPointB1, targetB );

                        //Curve up to the surface
                        ImmutableVector2D controlPointC1 = targetB.plus( 2, 0 );
                        quadTo( controlPointC1, hose.outputPoint );
                    }};

                    //Width of the hose in stage coordinates
                    final float hoseWidth = (float) Math.abs( transform.modelToViewDeltaY( hose.holeSize ) );

                    //Wrapping in an area gets rid of a kink when the water tower is low
                    setPathTo( new Area( new BasicStroke( hoseWidth, CAP_BUTT, JOIN_MITER ).createStrokedShape( transform.modelToView( p.getGeneralPath() ) ) ) );
                }
            } );
        }} );
        hose.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        //Show an arrow that lets the user change the angle of the hose
        //Fill with an invisible color so that it is see through but still draggable
        addChild( new PhetPPath( new Color( 0, 0, 0, 0 ), new BasicStroke( 1 ), Color.darkGray ) {{
            hose.angle.addObserver( new VoidFunction1<Double>() {
                public void apply( Double angle ) {
                    final ImmutableVector2D origin = new ImmutableVector2D( transform.modelToView( hose.outputPoint.toPoint2D() ) );
                    final ImmutableVector2D modelDirection = parseAngleAndMagnitude( 50, angle );
                    final ImmutableVector2D viewDirection = new ImmutableVector2D( modelDirection.getX(), -modelDirection.getY() );
                    setPathTo( new Arrow( origin.toPoint2D(), viewDirection.plus( origin ).toPoint2D(), 10, 10, 5 ).getShape() );
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
                    final double desiredAngle = hose.angle.get() + delta;
                    final double newAngle = MathUtil.clamp( 0, desiredAngle, Math.PI / 2 );
                    hose.angle.set( newAngle );
                    previousAngle = angle;
                }
            } );
        }} );
    }
}