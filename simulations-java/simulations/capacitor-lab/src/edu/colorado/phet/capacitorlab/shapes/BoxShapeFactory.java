/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.ModelViewTransform;

/**
 * Creates 2D projections of shapes that are related to the 3D boxes.
 * All Shapes are in the view coordinate frame.
 * Shapes for all faces corresponds to a box with its origin in the center of the top face.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxShapeFactory {
    
    private final ModelViewTransform mvt;
    
    public BoxShapeFactory( ModelViewTransform mvt ) {
        this.mvt = mvt;
    }
    
    /**
     * Top face is a parallelogram.
     * 
     *          p0 -------------- p1
     *          /                /
     *         /                /
     *       p3 --------------p2
     */
    public Shape createTopFace( double x, double y, double z, double width, double height, double depth ) {
        // points
        Point2D p0 = mvt.modelToView( x - ( width / 2 ), y, z + ( depth / 2 ) );
        Point2D p1 = mvt.modelToView( x + ( width / 2 ), y, z + ( depth / 2 ) );
        Point2D p2 = mvt.modelToView( x + ( width / 2 ), y, z - ( depth / 2 ) );
        Point2D p3 = mvt.modelToView( x - ( width / 2 ), y, z - ( depth / 2 ) );
        // shape
        return createFace( p0, p1, p2, p3 );
    }
    
    public Shape createTopFace( double width, double height, double depth ) {
        return createTopFace( 0, 0, 0, width, height, depth );
    }
    
    /**
     * Front face is a rectangle.
     * 
     *    p0 --------------- p1
     *     |                 |
     *     |                 |
     *    p3 --------------- p2
     */
    public Shape createFrontFace( double x, double y, double z, double width, double height, double depth ) {
        // points
        Point2D p0 = mvt.modelToView( x - ( width / 2 ), y, z - ( depth / 2 ) );
        Point2D p1 = mvt.modelToView( x + ( width / 2 ), y, z - ( depth / 2 ) );
        Point2D p2 = mvt.modelToView( x + ( width / 2 ), y + height, z - ( depth / 2 ) );
        Point2D p3 = mvt.modelToView( x - ( width / 2 ), y + height, z - ( depth / 2 ) );
        // shape
        return createFace( p0, p1, p2, p3 );
    }
    
    public Shape createFrontFace( double width, double height, double depth ) {
        return createFrontFace( 0, 0, 0, width, height, depth );
    }
    
    
    /**
     * Side face is a parallelogram.
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
    public Shape createSideFace( double x, double y, double z, double width, double height, double depth  ) {
        // points
        Point2D p0 = mvt.modelToView( x + ( width / 2 ), y, z - ( depth / 2 ) );
        Point2D p1 = mvt.modelToView( x + ( width / 2 ), y, z + ( depth / 2 ) );
        Point2D p2 = mvt.modelToView( x + ( width / 2 ), y + height, z + ( depth / 2 ) );
        Point2D p3 = mvt.modelToView( x + ( width / 2 ), y + height, z - ( depth / 2 ) );
        // path
        return createFace( p0, p1, p2, p3 );
    }
    
    public Shape createSideFace( double width, double height, double depth ) {
        return createSideFace( 0, 0, 0, width, height, depth );
    }
    
    /*
     * A face is defined by 4 points, specified in view coordinates.
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
