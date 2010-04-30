/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;


/**
 * Model of a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Capacitor {
    
    private static final double PLATE_HEIGHT = 5;
    
    private final Plate topPlate, bottomPlate;
    private final Dielectric dielectric;
    
    public Capacitor( double plateWidth, double plateSpacing, double dielectricOffset ) {
        this.topPlate = new Plate( plateWidth, PLATE_HEIGHT, plateWidth );
        this.bottomPlate = new Plate( plateWidth, PLATE_HEIGHT, plateWidth );
        this.dielectric = new Dielectric( plateWidth, plateSpacing, plateWidth, dielectricOffset );
    }
    
    public Plate getTopPlate() {
        return topPlate;
    }
    
    public Plate getBottomPlate() {
        return bottomPlate;
    }
    
    public Dielectric getDielectric() {
        return dielectric;
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
    
    public double getPlateHeight() {
        return topPlate.getHeight();
    }
    
    public double getPlateArea() {
        return topPlate.getArea();
    }
    
    public void setPlateSeparation( double distance ) {
        dielectric.setHeight( distance );
    }
    
    public double getPlateSeparation() {
        return dielectric.getHeight();
    }

    public void setDielectricOffset( double offset ) {
        dielectric.setOffset( offset );
    }
    
    public double getDielectricOffset() {
        return dielectric.getOffset();
    }
}
