// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view.manipulator;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for an arbitrary point, represented as a Vector2D.
 * Used exclusively in "Place 3 Points" game challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointDragHandler extends SimSharingDragHandler {

    private final LineManipulatorNode manipulatorNode;
    private final ModelViewTransform mvt;
    private final Property<Vector2D> point;
    private final Property<Vector2D>[] otherPoints;
    private final Property<DoubleRange> x1Range, y1Range;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    /**
     * Constructor
     *
     * @param userComponent   sim-sharing component identifier
     * @param componentType   sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt             transform between model and view coordinate frames
     * @param point           the point being manipulated
     * @param otherPoints     other points that the manipulated point can't match
     * @param x1Range
     * @param y1Range
     */
    public PointDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                             LineManipulatorNode manipulatorNode, ModelViewTransform mvt,
                             Property<Vector2D> point,
                             Property<Vector2D>[] otherPoints,
                             Property<DoubleRange> x1Range, Property<DoubleRange> y1Range ) {
        super( userComponent, componentType, true /* sendDragMessages */ );
        this.manipulatorNode = manipulatorNode;
        this.mvt = mvt;
        this.point = point;
        this.otherPoints = otherPoints;
        this.x1Range = x1Range;
        this.y1Range = y1Range;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( point.get().x );
        clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( point.get().y );
    }

    @Override protected void drag( PInputEvent event ) {
        super.drag( event );

        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );

        // constrain to range, snap to grid
        double x = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset ), x1Range.get() ) );
        double y = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ), y1Range.get() ) );
        Vector2D p = new Vector2D( x, y );

        // is this point the same as one of the others?
        boolean same = false;
        for ( Property<Vector2D> pOther : otherPoints ) {
            if ( p.equals( pOther.get() ) ) {
                same = true;
                break;
            }
        }

        // if the point is unique, set it
        if ( !same ) {
            point.set( new Vector2D( x, y ) );
        }
    }
}
