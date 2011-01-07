// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class RelativeDragHandler extends PBasicInputEventHandler {
    private Point2D.Double relativeGrabPoint;
    private ModelViewTransform transform;
    private final Property<ImmutableVector2D> modelLocation;
    private final Function1<Point2D, Point2D> constraint;
    private final PNode node;

    public RelativeDragHandler( PNode node, ModelViewTransform transform, Property<ImmutableVector2D> modelLocation ) {
        this( node, transform, modelLocation, new Function1.Identity<Point2D>() );
    }

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

    private void updateGrabPoint( PInputEvent event ) {
        Point2D viewStartingPoint = event.getPositionRelativeTo( node.getParent() );
        ImmutableVector2D viewCoordinateOfObject = transform.modelToView( modelLocation.getValue() );
        relativeGrabPoint = new Point2D.Double( viewStartingPoint.getX() - viewCoordinateOfObject.getX(), viewStartingPoint.getY() - viewCoordinateOfObject.getY() );
    }

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

    public void mouseReleased( PInputEvent event ) {
        relativeGrabPoint = null;
    }
}
