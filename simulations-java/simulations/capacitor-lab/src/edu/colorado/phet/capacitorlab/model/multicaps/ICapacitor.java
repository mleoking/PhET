// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.multicaps;

import java.awt.*;
import java.util.EventListener;

import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.colorado.phet.common.phetcommon.math.Dimension3D;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Interface implemented by all capacitor model elements.
 * Symbols shown are described in the design document.
 * Units are meters, Volts, Coulombs and Farads.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ICapacitor {

    // location, in model coordinate frame
    Point3D getLocation();

    double getX();

    double getY();

    double getZ();

    // d
    void setPlateSeparation( double plateSeparation );

    // d
    double getPlateSeparation();

    // L
    void setPlateWidth( double plateWidth );

    // L
    double getPlateWidth();

    Dimension3D getPlateSize();

    double getPlateHeight();

    double getPlateDepth();

    // plate surface area
    double getPlateArea();

    // center of the top surface of the top plate, in model coordinate frame
    Point3D getTopPlateCenter();

    // center of the top surface of the bottom plate, in model coordinate frame
    Point3D getBottomPlateCenter();

    // V
    void setPlatesVoltage( double voltage );

    // V
    double getPlatesVoltage();

    void setDielectricMaterial( DielectricMaterial dielectricMaterial );

    DielectricMaterial getDielectricMaterial();

    Dimension3D getDielectricSize();

    double getDielectricWidth();

    double getDielectricHeight();

    double getDielectricDepth();

    // offset
    void setDielectricOffset( double dielectricOffset );

    // offset
    double getDielectricOffset();

    // A_dielectric
    double getDielectricContactArea();

    // A_air
    double getAirContactArea();

    // C_total
    double getTotalCapacitance();

    // C_air
    double getAirCapacitance();

    // C_dielectric
    double getDieletricCapacitance();

    // Q_total
    double getTotalPlateCharge();

    // Q_dielectric
    double getDielectricPlateCharge();

    // Q_air
    double getAirPlateCharge();

    // Q_excess_dielectric
    double getExcessDielectricPlateCharge();

    // Q_exess_air
    double getExcessAirPlateCharge();

    // sigma_air
    double getAirSurfaceChargeDensity();

    // sigma_dielectric
    double getDielectricSurfaceChargeDensity();

    // E_effective
    double getEffectiveEfield();

    // E_dielectric
    double getDielectricEField();

    // E_air
    double getAirEField();

    // E_plates_dielectric
    double getPlatesDielectricEField();

    // E_plates_air
    double getPlatesAirEField();

    void addPlateSizeObserver( SimpleObserver o );

    void addPlateSeparationObserver( SimpleObserver o );

    void addDielectricOffsetObserver( SimpleObserver o );

    void addDielectricMaterialObserver( SimpleObserver o );

    // Gets a factory that creates 2D projections of shapes that are related to the 3D capacitor model.
    CapacitorShapeFactory getShapeFactory();

    // Does a Shape intersect the top plate shape?
    boolean intersectsTopPlateShape( Shape shape );

    // Does a shape intersect the bottom plate shape?
    boolean intersectsBottomPlateShape( Shape shape );

    // Is a point inside the Shape that is the 2D projection of the space between the capacitor plates?
    boolean isBetweenPlatesShape( Point3D p );

    // Is a point inside the Shape that is the 2D projection the portion of the dielectric that is between the plates?
    boolean isInsideDielectricBetweenPlatesShape( Point3D p );

    // Is a point inside the Shape that is the 2D projection of air between the plates?
    boolean isInsideAirBetweenPlatesShape( Point3D p );

    // Resets the capacitor to it's initial state.
    void reset();

    public interface CapacitorChangeListener extends EventListener {
        void capacitorChanged();
    }

    public void addCapacitorChangeListener( CapacitorChangeListener listener );

    public void removeCapacitorChangeListener( CapacitorChangeListener listener );
}
