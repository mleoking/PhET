// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.bendinglight.BendingLightApplication;
import edu.colorado.phet.bendinglight.model.Laser;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipY;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Graphic that depicts how the laser may be moved (in one direction).  It is only shown when the cursor is over the laser and is non-interactive.
 *
 * @author Sam Reid
 */
public class RotationDragHandle extends PNode {

    public RotationDragHandle( final ModelViewTransform transform, final Laser laser, final double deltaAngleDegrees, final BooleanProperty showDragHandles,
                               final Function1<Double, Boolean> notAtMax//Function that determines whether the laser is already at the max angle (if at the max angle then that drag handle disappears)
    ) {
        //Temporary property to help determine whether the drag handle should be shown
        CompositeProperty<Boolean> notAtMaximum = new CompositeProperty<Boolean>( new Function0<Boolean>() {
            public Boolean apply() {
                return notAtMax.apply( laser.getAngle() );
            }
        } ,laser.emissionPoint,laser.pivot);

        //Show the drag handle if the "show drag handles" is true and if the laser isn't already at the max angle.
        final And showArrow = showDragHandles.and( notAtMaximum );
        showArrow.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( showArrow.get() );
            }
        } );
        final BufferedImage image = flipY( flipX( BendingLightApplication.RESOURCES.getImage( "laser.png" ) ) );

        //Update the PNode
        final SimpleObserver update = new SimpleObserver() {
            public void update() {
                removeAllChildren();
                final PNode counterClockwiseDragArrow = new PNode() {{
                    //Draw a curved shape with an arc
                    final double distance = transform.modelToViewDeltaX( laser.getDistanceFromPivot() ) + image.getWidth() * 0.85;
                    final Point2D viewOrigin = transform.modelToView( laser.pivot.get() ).toPoint2D();
                    final double laserAngleInDegrees = toDegrees( laser.getAngle() );
                    Arc2D.Double arc = new Arc2D.Double( -distance + viewOrigin.getX(), -distance + viewOrigin.getY(), 2 * distance, 2 * distance, laserAngleInDegrees, deltaAngleDegrees, Arc2D.OPEN );
                    final Shape arrowBody = new BasicStroke( 10, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ).createStrokedShape( arc );

                    //Curve to the tail and tip
                    double EPSILON = 0.1;
                    ImmutableVector2D arrowTail = new ImmutableVector2D( viewOrigin ).plus( parseAngleAndMagnitude( distance, toRadians( -laserAngleInDegrees - deltaAngleDegrees * ( 1 - EPSILON ) ) ) );
                    ImmutableVector2D arrowTip = new ImmutableVector2D( viewOrigin ).plus( parseAngleAndMagnitude( distance, toRadians( -laserAngleInDegrees - deltaAngleDegrees * 1.1 ) ) );

                    //Draw the arrowhead
                    final Shape arrowHead = new Arrow( arrowTail.toPoint2D(), arrowTip.toPoint2D(), 20, 20, 0, 1.0, false ).getShape();
                    Area arrowArea = new Area( arrowBody ) {{
                        add( new Area( arrowHead ) );
                    }};

                    //Add the graphic
                    addChild( new PhetPPath( arrowArea, Color.green, new BasicStroke( 1 ), Color.black ) );

                    //Only the laser body is draggable, not the arrow itself
                    setPickable( false );
                    setChildrenPickable( false );
                }};
                addChild( counterClockwiseDragArrow );
            }
        };
        laser.emissionPoint.addObserver( update );//Update the shape when the laser moves
    }

}
