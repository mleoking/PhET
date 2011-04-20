// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler that keeps the dragged object within a specified bounded region, but also makes it so the mouse does not get disassociated from the node.
 *
 * @author Sam Reid
 */
public class RelativeDragHandler extends PBasicInputEventHandler {
    private final ModelViewTransform transform;
    private final Property<ImmutableVector2D> modelLocation;
    private final Function1<Point2D, Point2D> constraint;
    private final PNode node;

    private Point2D.Double relativeGrabPoint;

    //Constructs a RelativeDragHandler without a constraint, so that drag behavior is unbounded.
    public RelativeDragHandler( PNode node, ModelViewTransform transform, Property<ImmutableVector2D> modelLocation ) {
        this( node, transform, modelLocation, new Function1.Identity<Point2D>() );
    }

    //Constructs a RelativeDragHandler with the specified constraint which maps from a proposed model point to an allowed model point
    public RelativeDragHandler( PNode node, ModelViewTransform transform, Property<ImmutableVector2D> modelLocation,
                                Function1<Point2D, Point2D> constraint ) {//maps a proposed model location to an allowed model location
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
        ImmutableVector2D viewCoordinateOfObject = transform.modelToView( modelLocation.getValue() );
        relativeGrabPoint = new Point2D.Double( viewStartingPoint.getX() - viewCoordinateOfObject.getX(), viewStartingPoint.getY() - viewCoordinateOfObject.getY() );
    }

    //Drag the node to the constrained location
    public void mouseDragged( PInputEvent event ) {
        if ( relativeGrabPoint == null ) {
            updateGrabPoint( event );
        }
        final Point2D newDragPosition = event.getPositionRelativeTo( node.getParent() );
        Point2D modelPt = transform.viewToModel( newDragPosition.getX() - relativeGrabPoint.getX(),
                                                 newDragPosition.getY() - relativeGrabPoint.getY() );
        Point2D constrained = constraint.apply( modelPt );
        this.modelLocation.setValue( new ImmutableVector2D( constrained ) );
    }

    //Forget the relative grab point to reset for next drag sequence
    public void mouseReleased( PInputEvent event ) {
        relativeGrabPoint = null;
    }
}
