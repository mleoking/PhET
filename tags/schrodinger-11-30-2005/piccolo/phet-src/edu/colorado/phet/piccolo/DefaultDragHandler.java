/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.geom.Point2D;

/**
 * Drag behavior that ensures the dragged object stays inside the specified bounds.
 * The bounds must be rectangular, and this fails with rotated bounds.
 */

public class DefaultDragHandler extends PBasicInputEventHandler {
    private Point2D relativeClickPoint;

    public void mousePressed( PInputEvent event ) {
        setRelativeClickPoint( event );
    }

    private void setRelativeClickPoint( PInputEvent event ) {
        PNode node = event.getPickedNode();
        Point2D nodeLoc = node.getGlobalTranslation();
        Point2D clickLoc = getGlobalClickPoint( event );
        relativeClickPoint = new Point2D.Double( clickLoc.getX() - nodeLoc.getX(), clickLoc.getY() - nodeLoc.getY() );
    }

    private Point2D getGlobalClickPoint( PInputEvent event ) {
        return event.getPickedNode().localToGlobal( event.getPositionRelativeTo( event.getPickedNode() ) );
    }

    public void mouseDragged( PInputEvent event ) {
        if( relativeClickPoint == null ) {
            setRelativeClickPoint( event );
        }
        else {
            PNode pickedNode = event.getPickedNode();

            Point2D pt = getGlobalClickPoint( event );
            Point2D newPoint = new Point2D.Double( pt.getX() - relativeClickPoint.getX(), pt.getY() - relativeClickPoint.getY() );
            pickedNode.setGlobalTranslation( newPoint );
        }
    }

    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        relativeClickPoint = null;
    }
}
