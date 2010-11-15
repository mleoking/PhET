/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.util.ShapeUtils;

/**
 * Creates 2D projections of shapes that are related to the 3D capacitor model.
 * All of these shapes are 2D projections of pseudo-3D boxes.
 * These shapes are subtracted using constructive area geometry to account for 
 * occlusion that occurs in our pseudo-3D view.
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
    
    //----------------------------------------------------------------------------------------
    // unoccluded shapes
    //----------------------------------------------------------------------------------------
    
    /*
     * Top plate
     * @return
     */
    private Shape createTopPlateShape() {
        return createPlateShape( capacitor.getX(), capacitor.getTopPlateCenter().getY(), capacitor.getZ() );
    }
    
    /**
     * Bottom plate
     * @return
     */
    public Shape createBottomPlateShape() {
        return createPlateShape( capacitor.getX(), capacitor.getY() + ( capacitor.getPlateSeparation() / 2 ), capacitor.getZ() );
    }
    
    /*
     * Dielectric
     * @return
     */
    private Shape createDielectricShape() {
        double x = capacitor.getX() + capacitor.getDielectricOffset();
        double y = capacitor.getY() - ( capacitor.getDielectricHeight() / 2 );
        double z = capacitor.getZ();
        return createBoxShape( x, y, z, capacitor.getPlateSideLength(), capacitor.getDielectricHeight(), capacitor.getPlateSideLength() );
    }
    
    /*
     * Volume between the capacitor plates
     * @return
     */
    private Shape createBetweenPlatesShape() {
        double x = capacitor.getX();
        double y = capacitor.getY() - ( capacitor.getPlateSeparation() / 2 );
        double z = capacitor.getZ();
        double width = capacitor.getPlateSideLength();
        double height = capacitor.getPlateSeparation();
        double depth = width;
        return createBoxShape( x, y, z, width, height, depth );
    }
    
    /*
     * Portion of the dielectric that is between the capacitor plates
     * @return
     */
    private Shape createDielectricBetweenPlatesShape() {
        if ( capacitor.getDielectricOffset() >= capacitor.getPlateSideLength() ) {
            return createEmptyShape();
        }
        else {
            return ShapeUtils.intersect( createDielectricShape(), createBetweenPlatesShape() );
        }
    }
    
    /*
     * Air that is between the capacitor plates
     * @return
     */
    private Shape createAirBetweenPlateShape() {
        if ( capacitor.getDielectricOffset() == 0 ) {
            return createEmptyShape();
        }
        else {
            return ShapeUtils.subtract( createBetweenPlatesShape(), createDielectricBetweenPlatesShape() );
        }
    }
    
    //----------------------------------------------------------------------------------------
    // occluded shapes
    //----------------------------------------------------------------------------------------
    
    /**
     * Visible portions of the top plate.
     * Nothing occludes the top plate.
     * @return
     */
    public Shape createTopPlateShapeOccluded() {
        return createTopPlateShape();
    }
    
    /**
     * Visible portions of the bottom plate.
     * May be partially occluded by the top plate and/or dielectric.
     * @return
     */
    public Shape createBottomPlateShapeOccluded() {
        return ShapeUtils.subtract( createBottomPlateShape(), createTopPlateShape(), createDielectricShape() );
    }
    
    /**
     * Visible portion of the dielectric between the plates.
     * May be partially occluded by the top plate.
     */
    public Shape createDielectricBetweenPlatesShapeOccluded() {
        return ShapeUtils.subtract( createDielectricBetweenPlatesShape(), createTopPlateShape() );
    }
    
    /**
     * Visible portion of air between the plates.
     * May be partially occluded by the top plate.
     */
    public Shape createAirBetweenPlatesShapeOccluded() {
        return ShapeUtils.subtract( createAirBetweenPlateShape(), createTopPlateShape() );
    }
    
    //----------------------------------------------------------------------------------------
    // general shapes
    //----------------------------------------------------------------------------------------
    
    /*
     * A capacitor plate, relative to a specified origin.
     */
    private Shape createPlateShape( double x, double y, double z ) {
        return createBoxShape( x, y, z, capacitor.getPlateSideLength(), capacitor.getPlateThickness(), capacitor.getPlateSideLength() );
    }
    
    /*
     * A box, relative to a specific origin.
     */
    private Shape createBoxShape( double x, double y, double z, double width, double height, double depth ) {
        Shape topShape = boxShapeFactory.createTopFace( x, y, z, width, height, depth );
        Shape frontShape = boxShapeFactory.createFrontFace( x, y, z, width, height, depth );
        Shape sideShape = boxShapeFactory.createSideFace( x, y, z, width, height, depth );
        return ShapeUtils.add( topShape, frontShape, sideShape );
    }
    
    /*
     * Encapsulation of empty shape.
     */
    private Shape createEmptyShape() {
        return new Rectangle2D.Double( 0, 0, 0, 0 );
    }
}
