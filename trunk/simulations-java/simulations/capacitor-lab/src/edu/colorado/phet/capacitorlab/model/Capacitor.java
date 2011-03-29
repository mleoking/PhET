// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.colorado.phet.common.phetcommon.math.Dimension3D;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

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

    private final CLModelViewTransform3D mvt;
    private final CapacitorShapeFactory shapeFactory;
    private final SimpleObserver physicalPropertiesObserver;

    // immutable properties
    private final Point3D location; // location of the capacitor's geometric center (meters)

    // observable properties
    private final Property<Dimension3D> plateSizeProperty; // size of a plate (meters)
    private final Property<Double> plateSeparationProperty; // distance between the plates (meters)
    private final Property<DielectricMaterial> dielectricMaterialProperty; // insulator between the plates
    private final Property<Double> dielectricOffsetProperty; // x-axis offset of dielectric's center, relative to the capacitor's origin (meters)

    // observable properties, derived
    private final Property<Double> airCapacitanceProperty; // C_air, Farads
    private final Property<Double> dielectricCapacitanceProperty; // C_dielectric, Farads
    private final Property<Double> totalCapacitanceProperty; // C_total, Farads

    public Capacitor( Point3D location, double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset, CLModelViewTransform3D mvt ) {

        this.mvt = mvt;
        this.shapeFactory = new CapacitorShapeFactory( this, mvt );

        this.location = new Point3D.Double( location.getX(), location.getY(), location.getZ() );

        this.plateSizeProperty = new Property<Dimension3D>( new Dimension3D( plateWidth, CLConstants.PLATE_HEIGHT, plateWidth ) ); // plates are square
        this.plateSeparationProperty = new Property<Double>( plateSeparation );
        this.dielectricMaterialProperty = new Property<DielectricMaterial>( dielectricMaterial );
        this.dielectricOffsetProperty = new Property<Double>( dielectricOffset );

        // derived
        this.airCapacitanceProperty = new Property<Double>( 0d );
        this.dielectricCapacitanceProperty = new Property<Double>( 0d );
        this.totalCapacitanceProperty = new Property<Double>( 0d );

        // if physical properties change, derive capacitance properties
        {
            physicalPropertiesObserver = new SimpleObserver() {
                public void update() {
                    updateCapacitance();
                }
            };
            plateSizeProperty.addObserver( physicalPropertiesObserver );
            plateSeparationProperty.addObserver( physicalPropertiesObserver );
            dielectricOffsetProperty.addObserver( physicalPropertiesObserver );
            dielectricMaterialProperty.addObserver( physicalPropertiesObserver );
            dielectricMaterialProperty.getValue().addDielectricConstantObserver( physicalPropertiesObserver );
        }
    }

    public void reset() {
        plateSizeProperty.reset();
        plateSeparationProperty.reset();
        dielectricMaterialProperty.reset();
        dielectricOffsetProperty.reset();
    }

    /**
     * Gets the capacitor's location in model coordinates.
     *
     * @return location, in meters relative to (0,0,0)
     */
    public Point3D getLocationReference() {
        return location;
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

    /**
     * Gets the plate size.
     * @return
     */
    public Dimension3D getPlateSize() {
        return new Dimension3D( plateSizeProperty.getValue() );
    }

    /**
     * Sets the plate width.
     * (design doc symbol: L)
     * <p>
     * Only the plate width settable.
     * Plates are square, the plate depth is identical to the width.
     * And the height (thickness) is constant.
     *
     * @param plateWidth meters
     */
    public void setPlateWidth( double plateWidth ) {
        if ( ! ( plateWidth > 0 ) ) {
            throw new IllegalArgumentException( "plateWidth must be > 0: " + plateWidth );
        }
        plateSizeProperty.setValue( new Dimension3D( plateWidth, plateSizeProperty.getValue().getHeight(), plateWidth ) );
    }

    /**
     * Gets the length of a plate's side. Plates are square, so all sides have equal length.
     * (design doc symbol: L)
     *
     * @param return length, in meters
     */
    public double getPlateWidth() {
        return plateSizeProperty.getValue().getWidth();
    }

    /**
     * Gets the height (thickness) of the plates.
     *
     * @param thickness, in meters
     */
    public double getPlateHeight() {
        return plateSizeProperty.getValue().getHeight();
    }

    public double getPlateDepth() {
        return plateSizeProperty.getValue().getDepth();
    }

    public void addPlateSizeObserver( SimpleObserver o ) {
        plateSizeProperty.addObserver( o );
    }

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
        plateSeparationProperty.setValue( plateSeparation );
    }

    /**
     * Gets the distance between the 2 parallel plates.
     * (design doc symbol: d)
     *
     * return distance, in meters.
     */
    public double getPlateSeparation() {
        return plateSeparationProperty.getValue();
    }

    public void addPlateSeparationObserver( SimpleObserver o ) {
        plateSeparationProperty.addObserver( o );
    }

    /**
     * Convenience method for determining the outside center of the top plate.
     * This is a wire attachment point.
     * @return
     */
    public Point3D getTopPlateCenter() {
        return new Point3D.Double( getX(), getY() - ( getPlateSeparation() / 2 ) - getPlateHeight(), getZ() );
    }

    /**
     * Convenience method for determining the outside center of the bottom plate.
     * This is a wire attachment point.
     * @return
     */
    public Point3D getBottomPlateCenter() {
        return new Point3D.Double( getX(), getY() + ( getPlateSeparation() / 2 ) + getPlateHeight(), getZ());
    }

    /**
     * Sets the dielectric material that is between the plates.
     * @param dielectricMaterial
     */
    public void setDielectricMaterial( DielectricMaterial dielectricMaterial ) {
        if ( dielectricMaterial == null ) {
            throw new IllegalArgumentException( "dielectricMaterial must be non-null" );
        }
        dielectricMaterialProperty.getValue().removeDielectricConstantObserver( physicalPropertiesObserver );
        dielectricMaterialProperty.setValue( dielectricMaterial );
        dielectricMaterialProperty.getValue().addDielectricConstantObserver( physicalPropertiesObserver );
    }

    /**
     * Gets the dielectric material that is between the plates.
     * @return
     */
    public DielectricMaterial getDielectricMaterial() {
        return dielectricMaterialProperty.getValue();
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

    /**
     * Convenience method for getting dielectric size.
     * @return
     */
    public Dimension3D getDielectricSize() {
        return new Dimension3D( getPlateWidth(), getPlateSeparation(), getPlateDepth() );
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
        dielectricOffsetProperty.setValue( dielectricOffset );
    }

    /**
     * Gets the offset of the dielectric.
     * When the dielectric is fully inserted between the plates, its offset is zero.
     * @return offset, in meters.
     */
    public double getDielectricOffset() {
        return dielectricOffsetProperty.getValue();
    }

    public void addDielectricOffsetObserver( SimpleObserver o ) {
        dielectricOffsetProperty.addObserver( o );
    }

    /**
     * Gets the 2D area of one plate's top (or bottom) surfaces.
     * (design doc symbol: A)
     *
     * @return area in meters^2
     */
    public double getPlateArea() {
        return getPlateWidth() * getPlateDepth();
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

    /*
     * Updates the derived properties that are related to capacitance.
     * Note that there is order dependency here, total must be set last.
     */
    private void updateCapacitance() {
        airCapacitanceProperty.setValue( getCapacitance( CLConstants.EPSILON_AIR, getAirContactArea(), getPlateSeparation() ) );
        dielectricCapacitanceProperty.setValue( getCapacitance( getDielectricConstant(), getDielectricContactArea(), getPlateSeparation() ) );
        totalCapacitanceProperty.setValue( getAirCapacitance() + getDieletricCapacitance() );
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
        return totalCapacitanceProperty.getValue();
    }

    public void addTotalCapacitanceObserver( SimpleObserver o ) {
        totalCapacitanceProperty.addObserver( o );
    }

    /**
     * Gets the capacitance due to the part of the capacitor that is contacting air.
     * (design doc symbol: C_air)
     *
     * @return capacitance, in Farads
     */
    public double getAirCapacitance() {
        return airCapacitanceProperty.getValue();
    }

    public void addAirCapacitanceObserver( SimpleObserver o ) {
        airCapacitanceProperty.addObserver( o );
    }

    /**
     * Gets the capacitance due to the part of the capacitor that is contacting the dielectric.
     * (design doc symbol: C_dielectric)
     *
     * @return capacitance, in Farads
     */
    public double getDieletricCapacitance() {
        return dielectricCapacitanceProperty.getValue();
    }

    public void addDielectricCapacitanceObserver( SimpleObserver o ) {
        dielectricCapacitanceProperty.addObserver( o );
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

    public CapacitorShapeFactory getShapeFactory() {
        return shapeFactory;
    }

    /**
     * Does a Shape intersect the top plate shape?
     * @param shape
     * @return
     */
    public boolean intersectsTopPlateShape( Shape shape ) {
        return ShapeUtils.intersects( shape, shapeFactory.createTopPlateShapeOccluded() );
    }

    /**
     * Does a shape intersect the bottom plate shape?
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
}
