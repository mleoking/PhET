// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.event;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag behavior that ensures the dragged object stays inside the specified bounds.
 * The bounds must be rectangular, and this fails with rotated bounds.
 */
public class BoundedDragHandler extends PBasicInputEventHandler {
    private PNode dragNode;
    private PNode boundingNode;
    private Point2D relativeClickPoint;

    public BoundedDragHandler( PNode dragNode, PNode boundingNode ) {
        this.dragNode = dragNode;
        this.boundingNode = boundingNode;
    }

    public void mousePressed( PInputEvent event ) {
        super.mousePressed( event );
        if ( boundingNode.getRoot() == null ) {
            throw new IllegalStateException( "boundingNode has no root node. Did you forget to add boundingNode to the scenegraph?" );
        }
        setRelativeClickPoint( event );
    }

    private void setRelativeClickPoint( PInputEvent event ) {
        Point2D nodeLoc = dragNode.getGlobalTranslation();
        Point2D clickLoc = getGlobalClickPoint( event );
        relativeClickPoint = new Point2D.Double( clickLoc.getX() - nodeLoc.getX(), clickLoc.getY() - nodeLoc.getY() );
    }

    private Point2D getGlobalClickPoint( PInputEvent event ) {
        Point2D pt = event.getPositionRelativeTo( dragNode );
        dragNode.localToGlobal( pt );
        return pt;
    }

    public void mouseDragged( PInputEvent event ) {
        super.mouseDragged( event );
        if ( relativeClickPoint == null ) {
            setRelativeClickPoint( event );
        }
        else {
            PNode pickedNode = dragNode;

            Point2D pt = getGlobalClickPoint( event );
            Point2D newPoint = new Point2D.Double( pt.getX() - relativeClickPoint.getX(), pt.getY() - relativeClickPoint.getY() );
            pickedNode.setGlobalTranslation( newPoint );

            if ( !boundingNode.getGlobalFullBounds().contains( dragNode.getGlobalFullBounds() ) ) {
                double newX = pickedNode.getGlobalTranslation().getX();
                double newY = pickedNode.getGlobalTranslation().getY();
                double inset = 1; //todo: not sure what purpose this servers, but should probably be put in the proper coordinate frame
                if ( pickedNode.getGlobalFullBounds().getX() < boundingNode.getGlobalFullBounds().getX() ) {
                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getX();
                    double y0 = pickedNode.getGlobalFullBounds().getMinX();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX() - inset, pickedNode.getGlobalTranslation().getY() ) );
                    double x1 = pickedNode.getGlobalTranslation().getX();
                    double y1 = pickedNode.getGlobalFullBounds().getMinX();

                    newX = fitLinear( x0, y0, x1, y1, boundingNode.getGlobalFullBounds().getMinX() );
                }
                if ( pickedNode.getGlobalFullBounds().getY() < boundingNode.getGlobalFullBounds().getY() ) {

                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getY();
                    double y0 = pickedNode.getGlobalFullBounds().getMinY();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX(), pickedNode.getGlobalTranslation().getY() - inset ) );
                    double x1 = pickedNode.getGlobalTranslation().getY();
                    double y1 = pickedNode.getGlobalFullBounds().getMinY();

                    newY = fitLinear( x0, y0, x1, y1, boundingNode.getGlobalFullBounds().getMinY() );
                }
                if ( pickedNode.getGlobalFullBounds().getMaxX() > boundingNode.getGlobalFullBounds().getMaxX() ) {
                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getX();
                    double y0 = pickedNode.getGlobalFullBounds().getMaxX();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX() - inset, pickedNode.getGlobalTranslation().getY() ) );
                    double x1 = pickedNode.getGlobalTranslation().getX();
                    double y1 = pickedNode.getGlobalFullBounds().getMaxX();
                    newX = fitLinear( x0, y0, x1, y1, boundingNode.getGlobalFullBounds().getMaxX() );
                }
                if ( pickedNode.getGlobalFullBounds().getMaxY() > boundingNode.getGlobalFullBounds().getMaxY() ) {
                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getY();
                    double y0 = pickedNode.getGlobalFullBounds().getMaxY();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX(), pickedNode.getGlobalTranslation().getY() - inset ) );
                    double x1 = pickedNode.getGlobalTranslation().getY();
                    double y1 = pickedNode.getGlobalFullBounds().getMaxY();
                    newY = fitLinear( x0, y0, x1, y1, boundingNode.getGlobalFullBounds().getMaxY() );
                }
                Point2D offset = new Point2D.Double( newX, newY );
                dragNode.setGlobalTranslation( offset );
            }
        }
    }

    /* There is probably a more readable way to do this.*/
    private double fitLinear( double x0, double y0, double x1, double y1, double minX ) {
        double slope = ( y0 - y1 ) / ( x0 - x1 );
        double intercept = y0 - slope * x0;
        return ( minX - intercept ) / slope;
    }

    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        relativeClickPoint = null;
    }

    public PNode getBoundingNode() {
        return boundingNode;
    }

    public PNode getDragNode() {
        return dragNode;
    }
}