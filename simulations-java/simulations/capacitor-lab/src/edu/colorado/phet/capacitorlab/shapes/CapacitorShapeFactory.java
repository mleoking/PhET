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
     * Creates the bounding shape of the top plate.
     * @return
     */
    public Shape createTopPlateShape() {
        return createPlateShape( capacitor.getTopPlateCenter() );
    }
    
    /**
     * Creates the bounding shape of the bottom plate.
     * @return
     */
    private Shape createBottomPlateShape() {
        Point3D origin = new Point3D.Double( capacitor.getX(), capacitor.getY() + ( capacitor.getPlateSeparation() / 2 ), capacitor.getZ() );
        return createPlateShape( origin );
    }
    
    /**
     * Creates the bounding shape of the dielectric.
     * @return
     */
    private Shape createDielectricShape() {
        Point3D origin = new Point3D.Double( capacitor.getX() + capacitor.getDielectricOffset(), capacitor.getY() - ( capacitor.getDielectricHeight() / 2 ), capacitor.getZ() );
        return createBoxShape( capacitor.getPlateSideLength(), capacitor.getDielectricHeight(), capacitor.getPlateSideLength(), origin );
    }
    
    /**
     * Creates the bounding shape of the visible portions of the bottom plate.
     * The bottom plate may be partially occluded by the top plate and/or dielectric.
     * @return
     */
    public Shape createBottomPlateShapeOccluded() {
        Shape bottomPlateShape = createBottomPlateShape();
        Shape topPlateShape = createTopPlateShape();
        Shape dielectricShape = createDielectricShapeOccluded();
        // Subtract any portion of the top plate and dielectric that overlaps the bottom plate.
        Area area = new Area( bottomPlateShape );
        area.subtract( new Area( topPlateShape ) );
        area.subtract( new Area( dielectricShape ) );
        return area;
    }
    
    /**
     * Creates the bounding shape of the the visible portions of the dielectric.
     * The dielectric may be partially occluded by the top plate.
     */
    public Shape createDielectricShapeOccluded() {
        Shape dielectricShape = createDielectricShape();
        Shape topShape = createTopPlateShape();
        // Subtract any portion of the top plate that overlaps the dielectric.
        Area area = new Area( dielectricShape );
        area.subtract( new Area( topShape ) );
        return area;
    }
    
    /*
     * Creates a plate shape relative to a specified origin.
     */
    private Shape createPlateShape( Point3D origin ) {
        return createBoxShape( capacitor.getPlateSideLength(), capacitor.getPlateThickness(), capacitor.getPlateSideLength(), origin );
    }
    
    /*
     * Creates a box shape relative to a specific origin.
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
}
