// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.event;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler that keeps the dragged object within a specified bounded region,
 * but also makes it so the mouse does not get disassociated from the node (so the relative grab point on the node is always correct).
 *
 * @author Sam Reid
 */
public class RelativeDragHandler extends PBasicInputEventHandler {
    private final ModelViewTransform transform;//Transform from model coordinates to stage
    private final Property<ImmutableVector2D> modelLocation;//Way to read and write the property in model coordinates
    private final Function1<Point2D, Point2D> constraint;////maps a proposed model location to an allowed model location
    private final PNode node;//The node this handler is applied to

    private Point2D.Double relativeGrabPoint;//Coordinates inside the node where it was grabbed

    //Constructs a RelativeDragHandler without a constraint, so that drag behavior is unbounded.
    public RelativeDragHandler( PNode node, ModelViewTransform transform, Property<ImmutableVector2D> modelLocation ) {
        this( node, transform, modelLocation, new Function1.Identity<Point2D>() );
    }

    //Constructs a RelativeDragHandler with the specified constraint which maps from a proposed model point to an allowed model point
    public RelativeDragHandler( PNode node, ModelViewTransform transform, Property<ImmutableVector2D> modelLocation,
                                Function1<Point2D, Point2D> constraint ) {
        this.node = node;
        this.transform = transform;
        this.modelLocation = modelLocation;
        this.constraint = constraint;
    }

    public void mousePressed( PInputEvent event ) {
        updateGrabPoint( event );
    }

    //Update the relative grabbed point within the PNode
    private void updateGrabPoint( PInputEvent event ) {
        Point2D viewStartingPoint = event.getPositionRelativeTo( node.getParent() );
        ImmutableVector2D viewCoordinateOfObject = transform.modelToView( modelLocation.get() );
        relativeGrabPoint = new Point2D.Double( viewStartingPoint.getX() - viewCoordinateOfObject.getX(), viewStartingPoint.getY() - viewCoordinateOfObject.getY() );
    }

    //Drag the node to the constrained location
    public void mouseDragged( PInputEvent event ) {
        //Make sure we started the drag already
        if ( relativeGrabPoint == null ) {
            updateGrabPoint( event );
        }

        //Compute the targeted model point for the drag
        final Point2D newDragPosition = event.getPositionRelativeTo( node.getParent() );
        Point2D modelPt = transform.viewToModel( newDragPosition.getX() - relativeGrabPoint.getX(),
                                                 newDragPosition.getY() - relativeGrabPoint.getY() );

        //Find the constrained point for the targeted model point and apply it
        Point2D constrained = constraint.apply( modelPt );
        this.modelLocation.set( new ImmutableVector2D( constrained ) );
    }

    //Forget the relative grab point to reset for next drag sequence
    public void mouseReleased( PInputEvent event ) {
        relativeGrabPoint = null;
    }
}
