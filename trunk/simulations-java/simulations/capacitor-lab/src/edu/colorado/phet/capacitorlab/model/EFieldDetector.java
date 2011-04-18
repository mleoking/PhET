// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.ICircuit.CircuitChangeListener;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of the E-field Detector.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetector {

    // public observable properties
    public final Property<Boolean> visibleProperty;
    public final WorldLocationProperty bodyLocationProperty, probeLocationProperty;
    public final Property<Boolean> plateVisibleProperty, dielectricVisibleProperty, sumVisibleProperty, valuesVisibleProperty;

    // derived observable properties
    private final Property<Double> plateVectorProperty; // field due to the plate (E_plate_dielectric or E_plate_air, depending on probe location)
    private final Property<Double> dielectricVectorProperty; // field due to dielectric polarization (E_dielectric or E_air, depending on probe location)
    private final Property<Double> sumVectorProperty; // effective (net) field between the plates (E_effective)

    // mutable fields
    private ICircuit circuit;

    public EFieldDetector( ICircuit circuit, final World world, Point3D bodyLocation, Point3D probeLocation,
                           boolean visible, boolean plateVisible, boolean dielectricVisible, boolean sumVisible, boolean valuesVisible ) {

        this.circuit = circuit;

        this.visibleProperty = new Property<Boolean>( visible );
        this.bodyLocationProperty = new WorldLocationProperty( world, bodyLocation );
        this.probeLocationProperty = new WorldLocationProperty( world, probeLocation );

        this.plateVectorProperty = new Property<Double>( 0d );
        this.dielectricVectorProperty = new Property<Double>( 0d );
        this.sumVectorProperty = new Property<Double>( 0d );

        this.plateVisibleProperty = new Property<Boolean>( plateVisible );
        this.dielectricVisibleProperty = new Property<Boolean>( dielectricVisible );
        this.sumVisibleProperty = new Property<Boolean>( sumVisible );
        this.valuesVisibleProperty = new Property<Boolean>( valuesVisible );

        // observers
        {
            // update vectors when the circuit changes
            circuit.addCircuitChangeListener( new CircuitChangeListener() {
                public void circuitChanged() {
                    updateVectors();
                }
            } );

            // update vectors when the probe moves
            probeLocationProperty.addObserver( new SimpleObserver() {
                public void update() {
                    updateVectors();
                }
            } );
        }
    }

    public void reset() {
        visibleProperty.reset();
        bodyLocationProperty.reset();
        probeLocationProperty.reset();
        plateVisibleProperty.reset();
        dielectricVisibleProperty.reset();
        sumVisibleProperty.reset();
        valuesVisibleProperty.reset();
        // vector properties are derived when the other properties are reset
    }

    public void setCircuit( ICircuit circuit ) {
        if ( circuit != this.circuit ) {
            this.circuit = circuit;
            updateVectors();
        }
    }

    public void addPlateVectorObserver( SimpleObserver o ) {
        plateVectorProperty.addObserver( o );
    }

    public double getPlateVector() {
        return plateVectorProperty.getValue();
    }

    public void addDielectricVectorObserver( SimpleObserver o ) {
        dielectricVectorProperty.addObserver( o );
    }

    public double getDielectricVector() {
        return dielectricVectorProperty.getValue();
    }

    public void addSumVectorObserver( SimpleObserver o ) {
        sumVectorProperty.addObserver( o );
    }

    public double getSumVector() {
        return sumVectorProperty.getValue();
    }

    private void updateVectors() {
        // update values displayed by the meter based on probe location
        plateVectorProperty.setValue( circuit.getPlatesDielectricEFieldAt( probeLocationProperty.getValue() ) );
        dielectricVectorProperty.setValue( circuit.getDielectricEFieldAt( probeLocationProperty.getValue() ) );
        sumVectorProperty.setValue( circuit.getEffectiveEFieldAt( probeLocationProperty.getValue() ) );
    }
}
