// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeListener;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of the E-field Detector.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetector {

    private final BatteryCapacitorCircuit circuit;

    // directly observable properties
    public final Property<Boolean> visible;
    public final WorldLocationProperty bodyLocation, probeLocation;
    public final Property<Boolean> plateVisible, dielectricVisible, sumVisible, valuesVisible;

    // derived observable properties
    private final Property<Double> plateVector; // field due to the plate (E_plate_dielectric or E_plate_air, depending on probe location)
    private final Property<Double> dielectricVector; // field due to dielectric polarization (E_dielectric or E_air, depending on probe location)
    private final Property<Double> sumVector; // effective (net) field between the plates (E_effective)

    public EFieldDetector( BatteryCapacitorCircuit circuit, final World world, Point3D bodyLocation, Point3D probeLocation,
            boolean visible, boolean plateVisible, boolean dielectricVisible, boolean sumVisible, boolean valuesVisible ) {

        this.circuit = circuit;

        this.visible = new Property<Boolean>( visible );
        this.bodyLocation = new WorldLocationProperty( world, bodyLocation );
        this.probeLocation = new WorldLocationProperty( world, probeLocation );

        this.plateVector = new Property<Double>( 0d );
        this.dielectricVector = new Property<Double>( 0d );
        this.sumVector = new Property<Double>( 0d );

        this.plateVisible = new Property<Boolean>( plateVisible );
        this.dielectricVisible = new Property<Boolean>( dielectricVisible );
        this.sumVisible = new Property<Boolean>( sumVisible );
        this.valuesVisible = new Property<Boolean>( valuesVisible );

        // observers
        {
            // update vectors when the circuit changes
            circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
                public void circuitChanged() {
                    updateVectors();
                }
            });

            // update vectors when the probe moves
            this.probeLocation.addObserver( new SimpleObserver() {
                public void update() {
                    updateVectors();
                }
            } );
        }
    }

    public void reset() {
        visible.reset();
        bodyLocation.reset();
        probeLocation.reset();
        plateVisible.reset();
        dielectricVisible.reset();
        sumVisible.reset();
        valuesVisible.reset();
        // vector properties are derived when the other properties are reset
    }

    public void addPlateVectorObserver( SimpleObserver o ) {
        plateVector.addObserver( o );
    }

    public double getPlateVector() {
        return plateVector.getValue();
    }

    public void addDielectricVectorObserver( SimpleObserver o ) {
        dielectricVector.addObserver( o );
    }

    public double getDielectricVector() {
        return dielectricVector.getValue();
    }

    public void addSumVectorObserver( SimpleObserver o ) {
        sumVector.addObserver( o );
    }

    public double getSumVector() {
        return sumVector.getValue();
    }

    public void addPlateVisibleObserver( SimpleObserver o ) {
        plateVisible.addObserver( o );
    }

    public void setPlateVisible( boolean visible ) {
        plateVisible.setValue( visible );
    }

    public boolean isPlateVisible() {
        return plateVisible.getValue();
    }

    public void addDielectricVisibleObserver( SimpleObserver o ) {
        dielectricVisible.addObserver( o );
    }

    public void setDielectricVisible( boolean visible ) {
        dielectricVisible.setValue( visible );
    }

    public boolean isDielectricVisible() {
        return dielectricVisible.getValue();
    }

    public void addSumVisibleObserver( SimpleObserver o ) {
        sumVisible.addObserver( o );
    }

    public void setSumVisible( boolean visible ) {
        sumVisible.setValue( visible );
    }

    public boolean isSumVectorVisible() {
        return sumVisible.getValue();
    }

    public void addValuesVisibleObserver( SimpleObserver o ) {
        valuesVisible.addObserver( o );
    }

    public void setValuesVisible( boolean visible ) {
        valuesVisible.setValue( visible );
    }

    public boolean isValuesVisible() {
        return valuesVisible.getValue();
    }

    private void updateVectors() {
        // update values displayed by the meter based on probe location
        plateVector.setValue( circuit.getPlatesDielectricEFieldAt( probeLocation.getValue() ) );
        dielectricVector.setValue( circuit.getDielectricEFieldAt( probeLocation.getValue() ) );
        sumVector.setValue( circuit.getEffectiveEFieldAt( probeLocation.getValue() ) );
    }
}
