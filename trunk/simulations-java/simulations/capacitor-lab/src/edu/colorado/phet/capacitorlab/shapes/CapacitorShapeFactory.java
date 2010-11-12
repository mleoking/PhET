/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.Capacitor;

/**
 * Creates 2D projections of shapes that are related to the 3D capacitor model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorShapeFactory {
    
    private final Capacitor capacitor;
    
    public CapacitorShapeFactory( Capacitor capacitor ) {
        this.capacitor = capacitor;
    }
    
    public Shape createTopPlateShape() {
        return null;//XXX
    }
    
    public Shape createBottomPlateShape() {
        return null;//XXX
    }
    
    public Shape createSpaceBetweenPlatesShape() {
        return null;//XXX
    }
    
    /*
     * Front face of a capacitor plate is a rectangle.
     * Path specified using clockwise traversal.
     * 
     *    p1 ----------- p2
     *     |              |
     *     |              |
     *    p0 ----------- p3
     */
    public Shape createPlateFrontFace() {
        final double width = capacitor.getPlateSideLength();
        final double height = capacitor.getPlateThickness();
        Point2D p0 = new Point2D.Double( -width / 2, height );
        Point2D p1 = new Point2D.Double( -width / 2, 0 );
        Point2D p2 = new Point2D.Double( width / 2, 0 );
        Point2D p3 = new Point2D.Double( width / 2, height );
        return createPlateFace( p0, p1, p2, p3 );
    }
    
    /*
     * A capacitor plate face is generally defined by 4 points.
     */
    private Shape createPlateFace( Point2D p0, Point2D p1, Point2D p2, Point2D p3 ) {
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
