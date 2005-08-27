/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.geom.Point2D;

/**
 * Doesn't yet work if there is a transform on the target node itself.
 */

public class BoundedDragHandler extends PBasicInputEventHandler {
    private PNode boundingNode;//work in global coordinate frame for generality.
    private Point2D relativeClickPoint;

    public BoundedDragHandler( PNode boundingNode ) {
        this.boundingNode = boundingNode;
    }

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
//        return event.getPickedNode().localToGlobal( event.getPositionRelativeTo( event.getPickedNode() ) );//TODO this assumes there was a picked node, so must be used only in that context
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
            System.out.println( "newPoint = " + newPoint );
            pickedNode.setGlobalTranslation( newPoint );

            if( !boundingNode.getGlobalFullBounds().contains( event.getPickedNode().getGlobalFullBounds() ) ) {
                double newX = pickedNode.getGlobalTranslation().getX();
                double newY = pickedNode.getGlobalTranslation().getY();
                if( pickedNode.getGlobalFullBounds().getX() < boundingNode.getX() ) {
                    newX = boundingNode.getGlobalFullBounds().getX();
                }
                if( pickedNode.getGlobalFullBounds().getY() < boundingNode.getY() ) {
                    newY = boundingNode.getGlobalFullBounds().getY();
                }
                if( pickedNode.getGlobalFullBounds().getMaxX() > boundingNode.getGlobalFullBounds().getMaxX() ) {
                    newX = boundingNode.getGlobalFullBounds().getMaxX() - pickedNode.getGlobalFullBounds().getWidth();
                }
                if( pickedNode.getGlobalFullBounds().getMaxY() > boundingNode.getGlobalFullBounds().getMaxY() ) {
                    newY = boundingNode.getGlobalFullBounds().getMaxY() - pickedNode.getGlobalFullBounds().getHeight();
                }
                Point2D rollbackPoint = new Point2D.Double( newX, newY );
                System.out.println( "rollbackPoint = " + rollbackPoint );
                event.getPickedNode().setGlobalTranslation( rollbackPoint );
            }
        }
    }

    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        relativeClickPoint = null;
    }
}
