/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Doesn't yet work if there is a transform on the target node itself.
 */

public class ConstrainedDragHandler extends PBasicInputEventHandler {
    private Rectangle2D globalBounds;//work in global coordinate frame for generality.
    private Point2D relativeClickPoint;

    public ConstrainedDragHandler( Rectangle2D globalBounds ) {
        this.globalBounds = globalBounds;
    }

    public void mousePressed( PInputEvent event ) {
        setRelativeClickPoint( event );
    }

    private void setRelativeClickPoint( PInputEvent event ) {
        PNode node = event.getPickedNode();
        relativeClickPoint = event.getPositionRelativeTo( node );
    }

    private Point2D getGlobalClickPoint( PInputEvent event ) {
        return event.getPickedNode().localToGlobal( event.getPositionRelativeTo( event.getPickedNode() ) );//TODO this assumes there was a picked node, so must be used only in that context
    }

    public void mouseDragged( PInputEvent event ) {
        if( relativeClickPoint == null ) {
            setRelativeClickPoint( event );
        }
        else {
            PNode pickedNode = event.getPickedNode();

            Point2D pt = getGlobalClickPoint( event );
            Point2D newPoint = new Point2D.Double( pt.getX() - relativeClickPoint.getX(), pt.getY() - relativeClickPoint.getY() );
            System.out.println( "newPoint = " + newPoint );
            pickedNode.setGlobalTranslation( newPoint );

            if( !globalBounds.contains( event.getPickedNode().getGlobalFullBounds() ) ) {
                double newX = pickedNode.getGlobalTranslation().getX();
                double newY = pickedNode.getGlobalTranslation().getY();
                if( pickedNode.getGlobalFullBounds().getX() < globalBounds.getX() ) {
                    newX = globalBounds.getX();
                }
                if( pickedNode.getGlobalFullBounds().getY() < globalBounds.getY() ) {
                    newY = globalBounds.getY();
                }
                if( pickedNode.getGlobalFullBounds().getMaxX() > globalBounds.getMaxX() ) {
                    newX = globalBounds.getMaxX() - pickedNode.getGlobalFullBounds().getWidth();
                }
                if( pickedNode.getGlobalFullBounds().getMaxY() > globalBounds.getMaxY() ) {
                    newY = globalBounds.getMaxY() - pickedNode.getGlobalFullBounds().getHeight();
                }
                Point2D rollbackPoint = new Point2D.Double( newX, newY );
                System.out.println( "rollbackPoint = " + rollbackPoint );
                event.getPickedNode().setGlobalTranslation( rollbackPoint );
                //or butt up against the wall.

            }
        }
    }

    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        relativeClickPoint = null;
    }
}
