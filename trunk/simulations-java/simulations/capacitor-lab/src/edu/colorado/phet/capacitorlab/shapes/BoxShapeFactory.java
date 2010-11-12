/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Creates 2D projections of shapes that are related to the 3D boxes.
 * All coordinates are in model world coordinate frame.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxShapeFactory {
    
    public BoxShapeFactory() {
        
    }
    
    /**
     * Top face is a parallelogram.
     * Origin at lower-left corner.
     * Path specified using clockwise traversal.
     * 
     *          p0 -------------- p1
     *          /                /
     *         /                /
     *       p3 --------------p2
     */
    public Shape ceateTopFace( double width, double height, double depth ) {
        return null;//XXX
    }
    
    /**
     * Front face is a rectangle.
     * Path specified using clockwise traversal.
     * 
     *    p0 --------------- p1
     *     |                 |
     *     |                 |
     *    p3 --------------- p2
     */
    public Shape createFrontFace( double width, double height, double depth ) {
        return null;//XXX
    }
    
    /**
     * Side face is a parallelogram.
     * Path specified using clockwise traversal.
     * 
     *              p1
     *             / |
     *            /  |
     *           /   |
     *          /   p2
     *         p0   /
     *         |   /
     *         |  /
     *         | /
     *         p3
     */
    public Shape createSideFace( double width, double height, double depth ) {
        return null;//XXX
    }
    
    /*
     * A capacitor plate face is generally defined by 4 points.
     */
    private Shape createFace( Point2D p0, Point2D p1, Point2D p2, Point2D p3 ) {
        GeneralPath path = new GeneralPath();
        path.reset();
        path.moveTo( (float) p0.getX(), (float) p0.getY() );
        path.lineTo( (float) p1.getX(), (float) p1.getY() );
        path.lineTo( (float) p2.getX(), (float) p2.getY() );
        path.lineTo( (float) p3.getX(), (float) p3.getY() );
        path.closePath();
        return path;
    }
}
