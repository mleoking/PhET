// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.*;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.colorado.phet.common.phetcommon.math.Dimension3D;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Model of a capacitor, used in the Introduction and Dielectric tabs, where we have a
 * single capacitor whose geometry can be directly manipulated by the user.
 * <p/>
 * A capacitor consists of 2 parallel, square plates, with a dielectric material between the plates.
 * When the dielectric can be partially inserted, the capacitor must be modeled as 2 parallel capacitors,
 * one of which has the dielectric between its plates, and the other of which has air between its plates.
 * <p/>
 * A capacitor's capacitance (C) is solely dependent on its geometry and the dielectric material.
 * Charge (Q) on the plates is a function of capacitance and voltage (V) across the plates: Q = CV
 * <p/>
 * Variable names used in this implementation where chosen to match the specification
 * in the design document, and therefore violate Java naming conventions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Capacitor {

    private final CLModelViewTransform3D mvt;
    private final CapacitorShapeFactory shapeFactory;
    private final SimpleObserver propertiesObserver;
    private final SimpleObserver dielectricConstantObserver;
    private final EventListenerList listeners = new EventListenerList();

    // immutable properties
    private final Point3D location; // location of the capacitor's geometric center (meters)

    // observable properties
    private final Property<Dimension3D> plateSizeProperty; // size of a plate (meters)
    private final Property<Double> plateSeparationProperty; // distance between the plates (meters)
    private final Property<DielectricMaterial> dielectricMaterialProperty; // insulator between the plates
    private final Property<Double> dielectricOffsetProperty; // x-axis offset of dielectric's center, relative to the capacitor's origin (meters)
    private final Property<Double> platesVoltageProperty; // voltage across the plates (Volts)

    /**
     * Constructor
     *
     * @param location
     * @param plateWidth
     * @param plateSeparation
     * @param dielectricMaterial
     * @param dielectricOffset
     * @param mvt
     */
    public Capacitor( Point3D location, double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset, CLModelViewTransform3D mvt ) {

        this.mvt = mvt;
        this.shapeFactory = new CapacitorShapeFactory( this, mvt );

        this.location = new Point3D.Double( location.getX(), location.getY(), location.getZ() );

        this.plateSizeProperty = new Property<Dimension3D>( new Dimension3D( plateWidth, CLConstants.PLATE_HEIGHT, plateWidth ) ); // plates are square
        this.plateSeparationProperty = new Property<Double>( plateSeparation );
        this.dielectricMaterialProperty = new Property<DielectricMaterial>( dielectricMaterial );
        this.dielectricOffsetProperty = new Property<Double>( dielectricOffset );
        this.platesVoltageProperty = new Property<Double>( 0d ); // zero until it's connected into a circuit

        // if observable properties change, so do derived properties
        propertiesObserver = new SimpleObserver() {
            public void update() {
                fireCapacitorChanged();
            }
        };
        plateSizeProperty.addObserver( propertiesObserver );
        plateSeparationProperty.addObserver( propertiesObserver );
        dielectricOffsetProperty.addObserver( propertiesObserver );
        dielectricMaterialProperty.addObserver( propertiesObserver );
        dielectricMaterialProperty.get().addDielectricConstantObserver( propertiesObserver );
        platesVoltageProperty.addObserver( propertiesObserver );

        // observe dielectric constant
        this.dielectricConstantObserver = new SimpleObserver() {
            public void update() {
                fireCapacitorChanged();
            }
        };

        // observe change of dielectric material
        dielectricMaterialProperty.addObserver( new ChangeObserver<DielectricMaterial>() {
            public void update( DielectricMaterial newMaterial, DielectricMaterial oldMaterial ) {
                oldMaterial.removeDielectricConstantObserver( dielectricConstantObserver );
                newMaterial.addDielectricConstantObserver( dielectricConstantObserver );
            }
        } );
        dielectricMaterialProperty.get().addDielectricConstantObserver( dielectricConstantObserver );
    }

    public void reset() {
        plateSizeProperty.reset();
        plateSeparationProperty.reset();
        dielectricMaterialProperty.reset();
        dielectricOffsetProperty.reset();
        platesVoltageProperty.reset();
    }

    //----------------------------------------------------------------------------------
    // Location
    //----------------------------------------------------------------------------------

    /**
     * Gets the capacitor's location in model coordinates.
     *
     * @return location, in meters relative to (0,0,0)
     */
    public Point3D getLocation() {
        return new Point3D.Double( location );
    }

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    public double getZ() {
        return location.getZ();
    }

    //----------------------------------------------------------------------------------
    // Plate size (L, A)
    //----------------------------------------------------------------------------------

    /**
     * Gets the plate size.
     *
     * @return
     */
    public Dimension3D getPlateSize() {
        return new Dimension3D( plateSizeProperty.get() );
    }

    /**
     * Sets the plate width.
     * (design doc symbol: L)
     * <p/>
     * Only the plate width settable.
     * Plates are square, the plate depth is identical to the width.
     * And the height (thickness) is constant.
     *
     * @param plateWidth meters
     */
    public void setPlateWidth( double plateWidth ) {
        if ( !( plateWidth > 0 ) ) {
            throw new IllegalArgumentException( "plateWidth must be > 0: " + plateWidth );
        }
        plateSizeProperty.set( new Dimension3D( plateWidth, getPlateHeight(), plateWidth ) );
    }

    /**
     * Gets the length of a plate's side. Plates are square, so all sides have equal length.
     * (design doc symbol: L)
     *
     * @return length, in meters
     */
    public double getPlateWidth() {
        return plateSizeProperty.get().getWidth();
    }

    /**
     * Gets the height (thickness) of the plates.
     *
     * @return thickness, in meters
     */
    public double getPlateHeight() {
        return plateSizeProperty.get().getHeight();
    }

    public double getPlateDepth() {
        return plateSizeProperty.get().getDepth();
    }

    /**
     * Convenience method, gets the area of one plate's top (or bottom) surfaces.
     * (design doc symbol: A)
     *
     * @return area in meters^2
     */
    public double getPlateArea() {
        return getPlateWidth() * getPlateDepth();
    }

    public void addPlateSizeObserver( SimpleObserver o ) {
        plateSizeProperty.addObserver( o );
    }

    //----------------------------------------------------------------------------------
    // Plate separation (d)
    //----------------------------------------------------------------------------------

    /**
     * Sets the distance between the 2 parallel plates.
     * NOTE: The model for this sim requires that the plate separation be > 0.
     * (design doc symbol: d)
     *
     * @param plateSeparation distance, in meters.
     */
    public void setPlateSeparation( double plateSeparation ) {
        if ( !( plateSeparation > 0 ) ) {
            throw new IllegalArgumentException( "plateSeparation must be > 0: " + plateSeparation );
        }
        plateSeparationProperty.set( plateSeparation );
    }

    /**
     * Gets the distance between the 2 parallel plates.
     * (design doc symbol: d)
     * <p/>
     * return distance, in meters.
     */
    public double getPlateSeparation() {
        return plateSeparationProperty.get();
    }

    public void addPlateSeparationObserver( SimpleObserver o ) {
        plateSeparationProperty.addObserver( o );
    }

    public void removePlateSeparationObserver( SimpleObserver o ) {
        plateSeparationProperty.removeObserver( o );
    }

    /**
     * Convenience method for determining the outside center of the top plate.
     * This is a wire attachment point.
     *
     * @return
     */
    public Point3D getTopPlateCenter() {
        return new Point3D.Double( getX(), getY() - ( getPlateSeparation() / 2 ) - getPlateHeight(), getZ() );
    }

    /**
     * Convenience method for determining the outside center of the bottom plate.
     * This is a wire attachment point.
     *
     * @return
     */
    public Point3D getBottomPlateCenter() {
        return new Point3D.Double( getX(), getY() + ( getPlateSeparation() / 2 ) + getPlateHeight(), getZ() );
    }

    //----------------------------------------------------------------------------------
    // Dielectric
    //----------------------------------------------------------------------------------

    /**
     * Sets the dielectric material that is between the plates.
     *
     * @param dielectricMaterial
     */
    public void setDielectricMaterial( DielectricMaterial dielectricMaterial ) {
        if ( dielectricMaterial == null ) {
            throw new IllegalArgumentException( "dielectricMaterial must be non-null" );
        }
        dielectricMaterialProperty.get().removeDielectricConstantObserver( propertiesObserver );
        dielectricMaterialProperty.set( dielectricMaterial );
        dielectricMaterialProperty.get().addDielectricConstantObserver( propertiesObserver );
    }

    /**
     * Gets the dielectric material that is between the plates.
     *
     * @return
     */
    public DielectricMaterial getDielectricMaterial() {
        return dielectricMaterialProperty.get();
    }

    public void addDielectricMaterialObserver( SimpleObserver o ) {
        dielectricMaterialProperty.addObserver( o );
    }

    /**
     * Convenience method for getting the dielectric constant of the current dielectric material.
     * (design doc symbol: epsilon_r)
     *
     * @return dielectric constant, dimensionless
     */
    public double getDielectricConstant() {
        return getDielectricMaterial().getDielectricConstant();
    }

    // Convenience methods for getting dielectric size.
    public Dimension3D getDielectricSize() {
        return new Dimension3D( getDielectricWidth(), getDielectricHeight(), getDielectricDepth() );
    }

    public double getDielectricWidth() {
        return getPlateWidth();
    }

    public double getDielectricHeight() {
        return getPlateSeparation();
    }

    public double getDielectricDepth() {
        return getPlateDepth();
    }

    /**
     * Sets the offset of the dielectric.
     * When the dielectric is fully inserted between the plates, its offset is zero.
     * (design doc symbol: offset)
     *
     * @param dielectricOffset offset, in meters.
     */
    public void setDielectricOffset( double dielectricOffset ) {
        if ( !( dielectricOffset >= 0 ) ) {
            throw new IllegalArgumentException( "dielectricOffset must be >= 0: " + dielectricOffset );
        }
        dielectricOffsetProperty.set( dielectricOffset );
    }

    /**
     * Gets the offset of the dielectric.
     * When the dielectric is fully inserted between the plates, its offset is zero.
     *
     * @return offset, in meters.
     */
    public double getDielectricOffset() {
        return dielectricOffsetProperty.get();
    }

    public void addDielectricOffsetObserver( SimpleObserver o ) {
        dielectricOffsetProperty.addObserver( o );
    }

    /**
     * Gets the area of the contact between one of the plates and air.
     * (design doc symbol: A_air)
     *
     * @return area, in meters^2
     */
    public double getAirContactArea() {
        return getPlateArea() - getDielectricContactArea();
    }

    /**
     * Gets the area of the contact between one of the plates and the dielectric material.
     * (design doc symbol: A_dielectric)
     *
     * @return area, in meters^2
     */
    public double getDielectricContactArea() {
        double absoluteOffset = Math.abs( getDielectricOffset() );
        double area = ( getPlateWidth() - absoluteOffset ) * getPlateDepth(); // front * side
        if ( area < 0 ) {
            area = 0;
        }
        return area;
    }

    //----------------------------------------------------------------------------------
    // Capacitance (C)
    //----------------------------------------------------------------------------------

    /**
     * In the "Multiple Capacitors" module, the user has direct control over capacitance,
     * but no control over the capacitor's geometry or dielectric.
     * Assume that plate width (and therefore area) is constant, and use capacitance to calculate the plate separation.
     *
     * @param capacitance
     */
    public void setTotalCapacitance( double capacitance ) {
        setPlateSeparation( getPlateSeparation( getDielectricConstant(), getPlateWidth(), capacitance ) );
    }

    // d =  epsilon * epsilon_0 * A / C
    public static double getPlateSeparation( double dielectricConstant, double plateWidth, double capacitance ) {
        return dielectricConstant * CLConstants.EPSILON_0 * plateWidth * plateWidth / capacitance;
    }

    /**
     * Gets the total capacitance.
     * For the general case of a moveable dielectric, the capacitor is treated as 2 capacitors in parallel.
     * One of the capacitors has the dielectric between its plates, the other has air.
     * (design doc symbol: C_total)
     *
     * @return capacitance, in Farads
     */
    public double getTotalCapacitance() {
        return getAirCapacitance() + getDielectricCapacitance();
    }

    /**
     * Gets the capacitance due to the part of the capacitor that is contacting air.
     * (design doc symbol: C_air)
     *
     * @return capacitance, in Farads
     */
    public double getAirCapacitance() {
        return getCapacitance( CLConstants.EPSILON_AIR, getAirContactArea(), getPlateSeparation() );
    }

    /**
     * Gets the capacitance due to the part of the capacitor that is contacting the dielectric.
     * (design doc symbol: C_dielectric)
     *
     * @return capacitance, in Farads
     */
    public double getDielectricCapacitance() {
        return getCapacitance( getDielectricConstant(), getDielectricContactArea(), getPlateSeparation() );
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

    //----------------------------------------------------------------------------------
    // Shapes
    //----------------------------------------------------------------------------------

    public CapacitorShapeFactory getShapeFactory() {
        return shapeFactory;
    }

    /**
     * Does a Shape intersect the top plate shape?
     *
     * @param shape
     * @return
     */
    public boolean intersectsTopPlateShape( Shape shape ) {
        return ShapeUtils.intersects( shape, shapeFactory.createTopPlateShapeOccluded() );
    }

    /**
     * Does a shape intersect the bottom plate shape?
     *
     * @param shape
     * @return
     */
    public boolean intersectsBottomPlateShape( Shape shape ) {
        return ShapeUtils.intersects( shape, shapeFactory.createBottomPlateShapeOccluded() );
    }

    /**
     * Is a point inside the Shape that is the 2D projection of the space between the capacitor plates?
     *
     * @param p a point in the global 3D model coordinate frame
     * @return true or false
     */
    public boolean isBetweenPlatesShape( Point3D p ) {
        return isInsideDielectricBetweenPlatesShape( p ) || isInsideAirBetweenPlatesShape( p );
    }

    /**
     * Is a point inside the Shape that is the 2D projection the portion of the dielectric that is between the plates?
     *
     * @param p a point in the global 3D model coordinate frame
     * @return true or false
     */
    public boolean isInsideDielectricBetweenPlatesShape( Point3D p ) {
        return shapeFactory.createDielectricBetweenPlatesShapeOccluded().contains( mvt.modelToView( p ) );
    }

    /**
     * Is a point inside the Shape that is the 2D projection of air between the plates?
     *
     * @param p a point in the global 3D model coordinate frame
     * @return true or false
     */
    public boolean isInsideAirBetweenPlatesShape( Point3D p ) {
        return shapeFactory.createAirBetweenPlatesShapeOccluded().contains( mvt.modelToView( p ) );
    }

    //----------------------------------------------------------------------------------
    // Plate Voltage (V)
    //----------------------------------------------------------------------------------

    public void setPlatesVoltage( double voltage ) {
        platesVoltageProperty.set( voltage );
    }

    public double getPlatesVoltage() {
        return platesVoltageProperty.get();
    }

    //----------------------------------------------------------------------------------
    // Plate Charge (Q)
    //----------------------------------------------------------------------------------

    /**
     * Gets the charge for the portion of the top plate contacting the air.
     *
     * @return charge, in Coulombs
     *         (design doc symbol: Q_air)
     */
    public double getAirPlateCharge() {
        return getAirCapacitance() * getPlatesVoltage();
    }

    /**
     * Gets the charge for the portion of the top plate contacting the dielectric.
     * (design doc symbol: Q_dielectric)
     *
     * @return charge, in Coulombs
     */
    public double getDielectricPlateCharge() {
        return getDielectricCapacitance() * getPlatesVoltage();
    }

    /**
     * Gets the total charge on the top plate.
     * (design doc symbol: Q_total)
     *
     * @return charge, in Coulombs
     */
    public double getTotalPlateCharge() {
        return getDielectricPlateCharge() + getAirPlateCharge();
    }

    /**
     * Gets the excess plate charge due to plates contacting air.
     * (design doc symbol: Q_exess_air)
     *
     * @return excess charge, in Coulombs
     */
    public double getExcessAirPlateCharge() {
        return getExcessPlateCharge( CLConstants.EPSILON_AIR, getAirCapacitance(), getPlatesVoltage() );
    }

    /**
     * Gets the excess plate charge due to plates contacting the dielectric.
     * (design doc symbol: Q_excess_dielectric)
     *
     * @return excess charge, in Coulombs
     */
    public double getExcessDielectricPlateCharge() {
        return getExcessPlateCharge( getDielectricConstant(), getDielectricCapacitance(), getPlatesVoltage() );
    }

    /*
     * General solution for excess plate charge.
     *
     * @param epsilon_r dielectric constant, dimensionless
     * @param C capacitance due to the dielectric
     * @param V_plates plate voltage, volts
     * @return charge, in Coulombs (C)
     */
    private static double getExcessPlateCharge( double epsilon_r, double C, double V_plates ) {
        if ( !( epsilon_r > 0 ) ) {
            throw new IllegalArgumentException( "model requires epsilon_r > 0 : " + epsilon_r );
        }
        return ( ( epsilon_r - CLConstants.EPSILON_VACUUM ) / epsilon_r ) * C * V_plates; // Coulombs (1C = 1F * 1V)
    }

    //----------------------------------------------------------------------------------
    // E-Field (E)
    //----------------------------------------------------------------------------------

    /**
     * Gets the effective (net) field between the plates.
     * This is uniform everywhere between the plates.
     * (design doc symbol: E_effective)
     *
     * @return Volts/meter
     */
    public double getEffectiveEField() {
        return getPlatesVoltage() / getPlateSeparation();
    }

    /**
     * Gets the field due to the plates in the capacitor volume that contains air.
     * (design doc symbol: E_plates_air)
     *
     * @return E-field, in Volts/meter
     */
    public double getPlatesAirEField() {
        return getPlatesEField( CLConstants.EPSILON_AIR, getPlatesVoltage(), getPlateSeparation() );
    }

    /**
     * Gets the field due to the plates in the capacitor volume that contains the dielectric.
     * (design doc symbol: E_plates_dielectric)
     *
     * @return E-field, in Volts/meter
     */
    public double getPlatesDielectricEField() {
        return getPlatesEField( getDielectricConstant(), getPlatesVoltage(), getPlateSeparation() );
    }

    /*
     * General solution for the E-field due to some dielectric.
     *
     * @param epsilon_r dielectric constant, dimensionless
     * @param V_plates plate voltage, volts
     * @param d plate separation, meters
     * @return E-field, in Volts/meter
     */
    private static double getPlatesEField( double epsilon_r, double V_plates, double d ) {
        if ( !( d > 0 ) ) {
            throw new IllegalArgumentException( "model requires d (plate separation) > 0 : " + d );
        }
        return epsilon_r * V_plates / d;
    }

    /**
     * Gets the field due to air polarization.
     * (design doc symbol: E_air)
     *
     * @return E-field, in Volts/meter
     */
    public double getAirEField() {
        return getPlatesAirEField() - getEffectiveEField();
    }

    /**
     * Gets the field due to dielectric polarization.
     * (design doc symbol: E_dielectric)
     *
     * @return E-field, in Volts/meter
     */
    public double getDielectricEField() {
        return getPlatesDielectricEField() - getEffectiveEField();
    }

    //----------------------------------------------------------------------------------
    // CapacitorChangeListener
    //----------------------------------------------------------------------------------

    /**
     * Listener for capacitor change notifications.
     * Any change to the capacitor fires the capacitorChanged callback, with
     * no information about what has changed.  This may seem wasteful, but in
     * practice most things need to be changed when anything changes.  So this
     * simplifies the programming at the expense of some unneeded computation.
     * No performance problems have been noted, but this would be a prime place
     * to start optimizing if performance becomes an issue.
     * </p>
     * For view and control elements that are strictly interested in geometry
     * of the capacitor, see the various add*Observer methods.
     */
    public interface CapacitorChangeListener extends EventListener {
        void capacitorChanged();
    }

    public void addCapacitorChangeListener( CapacitorChangeListener listener ) {
        listeners.add( CapacitorChangeListener.class, listener );
    }

    public void removeCapacitorChangeListener( CapacitorChangeListener listener ) {
        listeners.remove( CapacitorChangeListener.class, listener );
    }

    public void fireCapacitorChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.capacitorChanged();
        }
    }
}
