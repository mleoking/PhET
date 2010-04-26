/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;


/**
 * Model of a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Capacitor {
    
    private static final double PLATE_WIDTH = 100;
    private static final double PLATE_HEIGHT = 10;
    private static final double PLATE_DEPTH = PLATE_WIDTH;
    private static final double PLATE_SPACING = 50;
    private static final double DIELECTRIC_OFFSET = 0;
    
    private final Plate topPlate, bottomPlate;
    private final Dielectric dielectric;
    
    public Capacitor() {
        this.topPlate = new Plate( PLATE_WIDTH, PLATE_HEIGHT, PLATE_DEPTH );
        this.bottomPlate = new Plate( PLATE_WIDTH, PLATE_HEIGHT, PLATE_DEPTH );
        this.dielectric = new Dielectric( PLATE_WIDTH, PLATE_SPACING, PLATE_DEPTH, DIELECTRIC_OFFSET );
    }
    
    // plates are square
    public void setPlateWidth( double width ) {
        topPlate.setWidthAndDepth( width, width );
        bottomPlate.setWidthAndDepth( width, width );
        dielectric.setWidthAndDepth( width, width );
    }
    
    public double getPlateWidth() {
        return topPlate.getWidth();
    }
    
    public double getPlateDepth() {
        return topPlate.getDepth();
    }
    
    public double getPlateThickness() {
        return topPlate.getThickness();
    }
    
    public double getPlateArea() {
        return topPlate.getArea();
    }
    
    public void setPlateSpacing( double distance ) {
        dielectric.setThickness( distance );
    }
    
    public double getPlateSpacing() {
        return dielectric.getThickness();
    }
}
