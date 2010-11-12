/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.Area;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Creates 2D projections of shapes that are related to the 3D capacitor model.
 * All Shapes are in the global view coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorShapeFactory {
    
    private final Capacitor capacitor;
    private final BoxShapeFactory boxShapeFactory;
    
    public CapacitorShapeFactory( Capacitor capacitor, ModelViewTransform mvt ) {
        this.capacitor = capacitor;
        this.boxShapeFactory = new BoxShapeFactory( mvt );
    }
    
    /**
     * Gets the bounding shape of the the visible portions of the top plate.
     * The entire top plate is always visible.
     * @return
     */
    public Shape createTopPlateShape() {
        return createPlateShape( capacitor.getTopPlateCenter() );
    }
    
    /**
     * Gets the bounding shape of the visible portions of the bottom plate.
     * The bottom plate may be partially occluded by the top plate and/or dielectric.
     * @return
     */
    public Shape createBottomPlateShape() {
        // Get the complete bottom plate shape
        Point3D p = capacitor.getBottomPlateCenter();
        Point3D origin = new Point3D.Double( p.getX(), p.getY() - capacitor.getPlateThickness(), p.getZ() );
        Shape bottomShape = createPlateShape( origin );
        // Top plate shape
        Shape topShape = createTopPlateShape();
        // Dielectric shape
        Shape dielectricShape = createDielectricShape();
        // Subtract any portion of the top plate and dielectric that overlaps the bottom plate.
        Area area = new Area( bottomShape );
        area.subtract( new Area( topShape ) );
        area.subtract( new Area( dielectricShape ) );
        return area;
    }
    
    /*
     * Gets a plate shape relative to a specified origin.
     */
    private Shape createPlateShape( Point3D origin ) {
        return createBoxShape( capacitor.getPlateSideLength(), capacitor.getPlateThickness(), capacitor.getPlateSideLength(), origin );
    }
    
    /*
     * Gets a box shape relative to a specific origin.
     */
    private Shape createBoxShape( double width, double height, double depth, Point3D origin ) {
        Shape topShape = boxShapeFactory.createTopFace( width, height, depth, origin );
        Shape frontShape = boxShapeFactory.createFrontFace( width, height, depth, origin );
        Shape sideShape = boxShapeFactory.createSideFace( width, height, depth, origin );
        Area area = new Area( topShape );
        area.add( new Area( frontShape ) );
        area.add( new Area( sideShape ) );
        return area;
    }
    
    /**
     * Gets the bounding shape of the the visible portions of the dielectric.
     * The dielectric may be partially occluded by the top plate.
     */
    public Shape createDielectricShape() {
        // Get the complete dielectric shape
        Point3D origin = new Point3D.Double( capacitor.getX() + capacitor.getDielectricOffset(), capacitor.getY() - ( capacitor.getDielectricHeight() / 2 ), capacitor.getZ() );
        Shape dielectricShape = createBoxShape( capacitor.getPlateSideLength(), capacitor.getDielectricHeight(), capacitor.getPlateSideLength(), origin );
        // Top plate shape
        Shape topShape = createTopPlateShape();
        // Subtract any portion of the top plate that overlaps the dielectric.
        Area area = new Area( dielectricShape );
        area.subtract( new Area( topShape ) );
        return area;
    }
}
