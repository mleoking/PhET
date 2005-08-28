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


            System.out.println( "pickedNode.getGlobalFullBounds().getMaxX() = " + pickedNode.getGlobalFullBounds().getMaxX() );
            System.out.println( "boundingNode.getGlobalFullBounds().getMaxX() = " + boundingNode.getGlobalFullBounds().getMaxX() );

            if( !boundingNode.getGlobalFullBounds().contains( event.getPickedNode().getGlobalFullBounds() ) ) {
                double newX = pickedNode.getGlobalTranslation().getX();
                double newY = pickedNode.getGlobalTranslation().getY();

//                AffineTransform affineTransform = new AffineTransform( pickedNode.getTransform() );
//                affineTransform.setTransform( affineTransform.getScaleX(), affineTransform.getShearY(), affineTransform.getShearX(), affineTransform.getScaleY(), 0, 0 );
//                Point2D offset = affineTransform.transform( new Point( 0, 0 ), null );
//                System.out.println( "offset = " + offset );
//                AffineTransform at = new AffineTransform( pickedNode.getGlobalScale(), 0, 0, pickedNode.getGlobalScale(), 0, 0 );

                if( pickedNode.getGlobalFullBounds().getX() < boundingNode.getFullBounds().getX() ) {
                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getX();
                    double y0 = pickedNode.getGlobalFullBounds().getMinX();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX() - 1, pickedNode.getGlobalTranslation().getY() ) );
                    double x1 = pickedNode.getGlobalTranslation().getX();
                    double y1 = pickedNode.getGlobalFullBounds().getMinX();

                    double slope = ( y0 - y1 ) / ( x0 - x1 );
                    double intercept = y0 - slope * x0;

                    double desiredY = boundingNode.getGlobalFullBounds().getMinX();
                    double requiredGlobalOffset = ( desiredY - intercept ) / slope;

                    newX = requiredGlobalOffset;
                }
                if( pickedNode.getGlobalFullBounds().getY() < boundingNode.getFullBounds().getY() ) {

                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getY();
                    double y0 = pickedNode.getGlobalFullBounds().getMinY();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX(), pickedNode.getGlobalTranslation().getY() - 1 ) );
                    double x1 = pickedNode.getGlobalTranslation().getY();
                    double y1 = pickedNode.getGlobalFullBounds().getMinY();

                    double slope = ( y0 - y1 ) / ( x0 - x1 );
                    double intercept = y0 - slope * x0;

                    double desiredY = boundingNode.getGlobalFullBounds().getMinY();
                    double requiredGlobalOffset = ( desiredY - intercept ) / slope;

                    newY = requiredGlobalOffset;
                }
                if( pickedNode.getGlobalFullBounds().getMaxX() > boundingNode.getGlobalFullBounds().getMaxX() ) {
                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getX();
                    double y0 = pickedNode.getGlobalFullBounds().getMaxX();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX() - 1, pickedNode.getGlobalTranslation().getY() ) );
                    double x1 = pickedNode.getGlobalTranslation().getX();
                    double y1 = pickedNode.getGlobalFullBounds().getMaxX();

                    double slope = ( y0 - y1 ) / ( x0 - x1 );
                    double intercept = y0 - slope * x0;

                    double desiredY = boundingNode.getGlobalFullBounds().getMaxX();
                    double requiredGlobalOffset = ( desiredY - intercept ) / slope;

                    newX = requiredGlobalOffset;
                }
                if( pickedNode.getGlobalFullBounds().getMaxY() > boundingNode.getGlobalFullBounds().getMaxY() ) {
                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getY();
                    double y0 = pickedNode.getGlobalFullBounds().getMaxY();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX(), pickedNode.getGlobalTranslation().getY() - 1 ) );
                    double x1 = pickedNode.getGlobalTranslation().getY();
                    double y1 = pickedNode.getGlobalFullBounds().getMaxY();

                    double slope = ( y0 - y1 ) / ( x0 - x1 );
                    double intercept = y0 - slope * x0;

                    double desiredY = boundingNode.getGlobalFullBounds().getMaxY();
                    double requiredGlobalOffset = ( desiredY - intercept ) / slope;

                    newY = requiredGlobalOffset;
                }
                Point2D rollbackPoint = new Point2D.Double( newX, newY );
//                Point2D fullRollbackPoint = new Point2D.Double( newX - offset.getX(), newY - offset.getY() );
                System.out.println( "rollbackPoint = " + rollbackPoint );
                event.getPickedNode().setGlobalTranslation( rollbackPoint );
//                event.getPickedNode().setOffset( rollbackPoint );
            }
        }
    }

    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        relativeClickPoint = null;
    }
}
