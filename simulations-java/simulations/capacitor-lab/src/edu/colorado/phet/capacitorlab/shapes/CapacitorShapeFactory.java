/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.util.ShapeUtils;

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
     * Creates the bounding shape of the visible portions of the top plate.
     * Nothing occludes the top plate.
     * @return
     */
    public Shape createTopPlateShapeOccluded() {
        return createTopPlateShape();
    }
    
    /**
     * Creates the bounding shape of the visible portions of the bottom plate.
     * The bottom plate may be partially occluded by the top plate and/or dielectric.
     * @return
     */
    public Shape createBottomPlateShapeOccluded() {
        return ShapeUtils.subtract( createBottomPlateShape(), createTopPlateShapeOccluded(), createDielectricShape() );
    }
    
    /*
     * Creates the bounding shape of the visible portion of the dielectric between the plates.
     * This may be partially occluded by the top plate.
     */
    public Shape createDielectricBetweenPlatesShapeOccluded() {
        return ShapeUtils.subtract( createDielectricBetweenPlatesShape(), createTopPlateShapeOccluded() );
    }
    
    /*
     * Creates the bounding shape of the visible portion of the air between the plates.
     * This may be partially occluded by the dielectric and top plate.
     */
    public Shape createAirBetweenPlatesShapeOccluded() {
        return ShapeUtils.subtract( createAirBetweenPlateShape(), createDielectricBetweenPlatesShape(), createTopPlateShapeOccluded() );
    }
    
    /*
     * Creates the bounding shape of the top plate.
     * @return
     */
    private Shape createTopPlateShape() {
        return createPlateShape( capacitor.getX(), capacitor.getTopPlateCenter().getY(), capacitor.getZ() );
    }
    
    /**
     * Creates the bounding shape of the bottom plate.
     * @return
     */
    public Shape createBottomPlateShape() {
        return createPlateShape( capacitor.getX(), capacitor.getY() + ( capacitor.getPlateSeparation() / 2 ), capacitor.getZ() );
    }
    
    /*
     * Creates the bounding shape of the dielectric.
     * @return
     */
    private Shape createDielectricShape() {
        double x = capacitor.getX() + capacitor.getDielectricOffset();
        double y = capacitor.getY() - ( capacitor.getDielectricHeight() / 2 );
        double z = capacitor.getZ();
        return createBoxShape( x, y, z, capacitor.getPlateSideLength(), capacitor.getDielectricHeight(), capacitor.getPlateSideLength() );
    }
    
    /*
     * Creates the bounding shape of the area between the capacitor plates.
     * @return
     */
    private Shape createBetweenPlatesShape() {
        double x = capacitor.getX();
        double y = capacitor.getY() + ( capacitor.getPlateSeparation() / 2 );
        double z = capacitor.getZ();
        double width = capacitor.getPlateSideLength();
        double height = capacitor.getPlateSeparation();
        double depth = width;
        return createBoxShape( x, y, z, width, height, depth );
    }
    
    /*
     * Creates the bounding shape of the portion of the dielectric that is between the capacitor plates.
     * @return
     */
    private Shape createDielectricBetweenPlatesShape() {
        return ShapeUtils.subtract( createDielectricShape(), createBetweenPlatesShape() );
    }
    
    /*
     * Creates the bounding shape of the air that is between the capacitor plates.
     * @return
     */
    private Shape createAirBetweenPlateShape() {
        return ShapeUtils.subtract( createDielectricShape(), createBetweenPlatesShape() );
    }
    
    /*
     * Creates the bounding shape of a plate, relative to a specified origin.
     */
    private Shape createPlateShape( double x, double y, double z ) {
        return createBoxShape( x, y, z, capacitor.getPlateSideLength(), capacitor.getPlateThickness(), capacitor.getPlateSideLength() );
    }
    
    /*
     * Creates the bounding shape of a box, relative to a specific origin.
     */
    private Shape createBoxShape( double x, double y, double z, double width, double height, double depth ) {
        Shape topShape = boxShapeFactory.createTopFace( x, y, z, width, height, depth );
        Shape frontShape = boxShapeFactory.createFrontFace( x, y, z, width, height, depth );
        Shape sideShape = boxShapeFactory.createSideFace( x, y, z, width, height, depth );
        return ShapeUtils.add( topShape, frontShape, sideShape );
    }
}
