/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;

/**
 * Model of a capacitor.
 * <p>
 * A capacitor consists of 2 parallel, square plates, with a dielectric material between the plates.
 * A capacitor's capacitance is dependent on its geometry and the dielectric material.
 * When the dielectric can be partially inserted, the capacitor must be modeled as 2 parallel capacitors,
 * one of which has the dielectric between its plates, and the other of which has air between its plates.
 * <p>
 * Variable names used in this implementation where chosen to match the specification
 * in the design document, and therefore violate Java naming conventions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Capacitor {
    
    private final EventListenerList listeners;
    
    // immutable properties
    private final Point2D location; // location of the capacitor's geometric center in the 2D plane of the camera (meters)
    private final double plateThickness; // thickness of the plates (meters)
    private final double dielectricGap; // gap between the dielectric and the plates (meters)
    
    // mutable properties
    private double plateSideLength; // length of one side of a plate (meters)
    private double plateSeparation; // distance between the plates (meters)
    private DielectricMaterial dielectricMaterial; // insulator between the plates
    private double dielectricOffset; // offset of dielectric's center from the axis that goes through the center of the 2 plates (meters)

    public Capacitor( Point2D location, double plateSideLength, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        
        listeners = new EventListenerList();
        
        this.location = new Point2D.Double( location.getX(), location.getY() );
        this.plateThickness = CLConstants.PLATE_THICKNESS;
        this.dielectricGap = CLConstants.DIELECTRIC_GAP;
        
        this.plateSideLength = plateSideLength;
        this.plateSeparation = plateSeparation;
        this.dielectricMaterial = dielectricMaterial;
        this.dielectricOffset = dielectricOffset;
    }
    
    /**
     * Gets the capacitor's location in 2D model coordinates.
     * This property does not play a role in the model, but is used by the visual representation.
     * @return location, in meters relative to (0,0)
     */
    public Point2D getLocationReference() {
        return location;
    }
    
    /**
     * Gets the thickness of the plates.
     * This property does not play a role in the model, but is used by the visual representation.
     * @param thickness, in meters
     */
    public double getPlateThickness() {
        return plateThickness;
    }
    
    /**
     * Sets the length of a plate's side. Plates are square, so all sides have equal length.
     * @param plateSideLength meters
     */
    public void setPlateSideLength( double plateSideLength ) {
        if ( plateSideLength <= 0 ) {
            throw new IllegalArgumentException( "plateSideLength must be > 0: " + plateSideLength );
        }
        if ( plateSideLength != this.plateSideLength ) {
            this.plateSideLength = plateSideLength;
            firePlateSizeChanged();
            fireCapacitanceChanged();
        }
    }
    
    /**
     * Gets the length of a plate's side. Plates are square, so all sides have equal length.
     * @param return length, in meters
     */
    public double getPlateSideLength() {
        return plateSideLength;
    }
    
    /**
     * Sets the distance between the 2 parallel plates.
     * @param plateSeparation distance, in meters.
     */
    public void setPlateSeparation( double plateSeparation ) {
        if ( plateSeparation < 0 ) {
            throw new IllegalArgumentException( "plateSeparation must be >= 0: " + plateSeparation );
        }
        if ( plateSeparation != this.plateSeparation ) {
            this.plateSeparation = plateSeparation;
            firePlateSeparationChanged();
            fireCapacitanceChanged();
        }
    }
    
    /**
     * Gets the distance between the 2 parallel plates.
     * return distance, in meters.
     */
    public double getPlateSeparation() {
        return plateSeparation;
    }
    
    /**
     * Sets the dielectric material that is between the plates.
     * @param dielectricMaterial
     */
    public void setDielectricMaterial( DielectricMaterial dielectricMaterial ) {
        if ( dielectricMaterial == null ) {
            throw new IllegalArgumentException( "dielectricMaterial must be non-null" );
        }
        if ( dielectricMaterial != this.dielectricMaterial ) { /* yes, referential equality */
            this.dielectricMaterial = dielectricMaterial;
            fireDielectricMaterialChanged();
            fireCapacitanceChanged();
        }
    }
    
    /**
     * Gets the dielectric material that is between the plates.
     * @return
     */
    public DielectricMaterial getDielectricMaterial() {
        return dielectricMaterial;
    }
    
    /**
     * Convenience method for getting the dielectric constant of the current dielectric material.
     * 
     * @return dielectric constant, dimensionless
     */
    public double getDielectricConstant() {
        return dielectricMaterial.getDielectricConstant();
    }
    
    /**
     * Gets the gap between the dielectric and the plates, identical for both plates.
     * This property does not play a role in the model, but is used by the visual representation.
     * @return the gap, in meters
     */
    public double getDielectricGap() {
        return dielectricGap;
    }
    
    /**
     * Sets the offset of the dielectric.
     * When the dielectric is fully inserted between the plates, its offset is zero.
     * @param dielectricOffset offset, in meters.
     */
    public void setDielectricOffset( double dielectricOffset ) {
        if ( dielectricOffset < 0 ) {
            throw new IllegalArgumentException( "dielectricOffset must be >= 0: " + dielectricOffset );
        }
        if ( dielectricOffset != this.dielectricOffset ) {
            this.dielectricOffset = dielectricOffset;
            fireDielectricOffsetChanged();
            fireCapacitanceChanged();
        }
    }
    
    /**
     * Gets the offset of the dielectric.
     * When the dielectric is fully inserted between the plates, its offset is zero.
     * @return offset, in meters.
     */
    public double getDielectricOffset() {
        return dielectricOffset;
    }
    
    /**
     * Gets the area of one plate's inside surface.
     * @return area in meters^2
     */
    public double getPlateArea() {
        return plateSideLength * plateSideLength;
    }
    
    /**
     * Gets the area of the contact between one of the plates and air.
     * @return area, in meters^2
     */
    public double getAirContactArea() {
        return getPlateArea() - getDielectricContactArea();
    }
    
    /**
     * Gets the area of the contact between one of the plates and the dielectric material.
     * @return area, in meters^2
     */
    public double getDielectricContactArea() {
        double area = getPlateSideLength() * ( getPlateSideLength() - getDielectricOffset() ); // side * front
        if ( area < 0 ) {
            area = 0;
        }
        return area;
    }
    
    /**
     * Gets the total capacitance.
     * For the general case of a moveable dielectric, the capacitor is treated as 2 capacitors in parallel.
     * One of the capacitors has the dielectric between its plates, the other has air.
     * 
     * @return capacitance, in Farads
     */
    public double getTotalCapacitance() {
        return getAirCapacitance() + getDieletricCapacitance();
    }
    
    /**
     * Gets the capacitance due to the part of the capacitor that is contacting air.
     * 
     * @return capacitance, in Farads
     */
    public double getAirCapacitance() {
        return getCapacitance( CLConstants.EPSILON_AIR, getAirContactArea(), getPlateSeparation() );
    }
    
    /**
     * Gets the capacitance due to the part of the capacitor that is contacting the dielectric.
     * 
     * @return capacitance, in Farads
     */
    public double getDieletricCapacitance() {
        return getCapacitance( dielectricMaterial.getDielectricConstant(), getDielectricContactArea(), getPlateSeparation() );
    }
    
    /*
     * General formula for computing capacitance.
     * 
     * @param epsilon dielectric constant, dimensionless
     * @param area area of the contact between the dielectric and one plate, meters^2
     * @param plateSeparation distance between the plates, meters
     * @return capacitance, in Farads
     */
    private static double getCapacitance( double epsilon, double A, double d ) {
        return epsilon * CLConstants.EPSILON_0 * A / d;
    }
    
    /**
     * Interface implemented by listeners who are interested in capacitor changes.
     * Includes separate notification for each mutable property, plus capacitance.
     */
    public interface CapacitorChangeListener extends EventListener {
        public void plateSizeChanged();
        public void plateSeparationChanged();
        public void dielectricMaterialChanged();
        public void dielectricOffsetChanged();
        public void capacitanceChanged();
    }
    
    public static class CapacitorChangeAdapter implements CapacitorChangeListener {
        public void plateSizeChanged() {}
        public void plateSeparationChanged() {}
        public void dielectricMaterialChanged() {}
        public void dielectricOffsetChanged() {}
        public void capacitanceChanged() {}
    }
    
    public void addCapacitorChangeListener( CapacitorChangeListener listener ) {
        listeners.add( CapacitorChangeListener.class, listener );
    }
    
    public void removeCapacitorChangeListener( CapacitorChangeListener listener ) {
        listeners.remove( CapacitorChangeListener.class, listener );
    }
    
    private void firePlateSizeChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.plateSizeChanged();
        }
    }
    
    private void firePlateSeparationChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.plateSeparationChanged();
        }
    }
    
    private void fireDielectricMaterialChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.dielectricMaterialChanged();
        }
    }
    
    private void fireDielectricOffsetChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.dielectricOffsetChanged();
        }
    }
    
    private void fireCapacitanceChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.capacitanceChanged();
        }
    }
}
