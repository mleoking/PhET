// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler that keeps the rotates the object, but ensures the the mouse does not get disassociated from the node
 * (so the relative grab point on the node is always correct).
 *
 * @author Sam Reid
 * @see edu.colorado.phet.common.piccolophet.event.RelativeDragHandler
 * @see edu.colorado.phet.motionseries.graphics.RotationHandler
 */
public class RotationDragHandler extends PBasicInputEventHandler {
    private final ModelViewTransform transform;//Transform from model coordinates to stage
    private final Property<ImmutableVector2D> center;//Way to read and write the property in model coordinates
    private final PNode node;//The node this handler is applied to
    private Property<Double> angle;//Settable model angle
    private double relativeAngle;//Angle that the object was grabbed at

    //Constructs a RelativeDragHandler without a constraint, so that drag behavior is unbounded.
    public RotationDragHandler( PNode node, ModelViewTransform transform, Property<Double> angle, Property<ImmutableVector2D> center ) {
        this.node = node;
        this.transform = transform;
        this.center = center;
        this.angle = angle;
    }

    public void mousePressed( PInputEvent event ) {
        updateGrabPoint( event );
    }

    //Update the relative grabbed point within the PNode--the relative angle of the grab
    private void updateGrabPoint( PInputEvent event ) {
        relativeAngle = angle.get() - getPointerModelAngle( event );
    }

    //Convert a PInputEvent to the angle grabbed within the model
    private double getPointerModelAngle( PInputEvent event ) {
        Point2D viewStartingPoint = event.getPositionRelativeTo( node.getParent() );
        return new ImmutableVector2D( transform.viewToModel( viewStartingPoint ) ).minus( center.get() ).getAngle();
    }

    //Drag the node to the constrained location
    public void mouseDragged( PInputEvent event ) {
        double a = getPointerModelAngle( event ) + relativeAngle;

        //Clamp the angle to be in a good canonical range, since it is assumed in SugarDispenser to determine when to open the lid
        while ( a < Math.PI ) { a += Math.PI * 2; }
        while ( a > Math.PI ) { a -= Math.PI * 2; }
        angle.set( a );
    }

    //Forget the relative grab point to reset for next drag sequence
    public void mouseReleased( PInputEvent event ) {
        relativeAngle = 0.0;
    }
}
