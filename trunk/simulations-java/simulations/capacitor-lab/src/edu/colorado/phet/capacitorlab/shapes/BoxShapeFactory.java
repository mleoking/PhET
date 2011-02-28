// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.common.phetcommon.math.Dimension3D;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Creates 2D projections of shapes that are related to the 3D boxes.
 * Shapes are in the view coordinate frame, everything else is in model coordinates.
 * Shapes for all faces corresponds to a box with its origin in the center of the top face.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxShapeFactory {
    
    private final CLModelViewTransform3D mvt;
    
    public BoxShapeFactory( CLModelViewTransform3D mvt ) {
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
    
    public Shape createTopFace( Dimension3D size ) {
        return createTopFace( 0, 0, 0, size.getWidth(), size.getHeight(), size.getDepth() );
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
    
    public Shape createFrontFace( Dimension3D size ) {
        return createFrontFace( 0, 0, 0, size.getWidth(), size.getHeight(), size.getDepth() );
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
    
    public Shape createSideFace( Dimension3D size ) {
        return createSideFace( 0, 0, 0, size.getWidth(), size.getHeight(), size.getDepth() );
    }
    
    /*
     * A complete box, relative to a specific origin.
     */
    public Shape createBoxShape( double x, double y, double z, double width, double height, double depth ) {
        Shape topShape = createTopFace( x, y, z, width, height, depth );
        Shape frontShape = createFrontFace( x, y, z, width, height, depth );
        Shape sideShape = createSideFace( x, y, z, width, height, depth );
        return ShapeUtils.add( topShape, frontShape, sideShape );
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
