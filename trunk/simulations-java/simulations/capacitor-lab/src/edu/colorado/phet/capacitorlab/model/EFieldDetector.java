/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of the E-field Detector.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetector {
    
    private final BatteryCapacitorCircuit circuit;
    private final World world;
    
    // observable properties
    private final Property<Boolean> visibleProperty;
    private final Property<Point3D> probeLocationProperty;
    private final Property<Boolean> plateVisibleProperty, dielectricVisibleProperty, sumVisibleProperty, valuesVisibleProperty;
    
    // derived observable properties
    private final Property<Double> plateVectorProperty; // field due to the plate (E_plate_dielectric or E_plate_air, depending on probe location)
    private final Property<Double> dielectricVectorProperty; // field due to dielectric polarization (E_dielectric or E_air, depending on probe location)
    private final Property<Double> sumVectorProperty; // effective (net) field between the plates (E_effective)
    
    public EFieldDetector( BatteryCapacitorCircuit circuit, final World world, Point3D probeLocation, boolean visible, boolean plateVisible, boolean dielectricVisible, boolean sumVisible, boolean valuesVisible ) {
        
        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void efieldChanged() {
                updateVectors();
            }
        });
        
        this.world = world;
        
        this.visibleProperty = new Property<Boolean>( visible );
        this.probeLocationProperty = new Property<Point3D>( new Point3D.Double( probeLocation ) );
        
        this.plateVectorProperty = new Property<Double>( 0d );
        this.dielectricVectorProperty = new Property<Double>( 0d );
        this.sumVectorProperty = new Property<Double>( 0d );
        
        this.plateVisibleProperty = new Property<Boolean>( plateVisible );
        this.dielectricVisibleProperty = new Property<Boolean>( dielectricVisible );
        this.sumVisibleProperty = new Property<Boolean>( sumVisible );
        this.valuesVisibleProperty = new Property<Boolean>( valuesVisible );

        // observers
        {
            // keep the probe inside the world bounds
            world.addBoundsObserver( new SimpleObserver() {
                public void update() {
                    setProbeLocation( world.getConstrainedLocation( getProbeLocation() ) );
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
        probeLocationProperty.reset();
        plateVisibleProperty.reset();
        dielectricVisibleProperty.reset();
        sumVisibleProperty.reset();
        valuesVisibleProperty.reset();
        // vector properties are derived when the other properties are reset
    }
    
    public boolean isVisible() {
        return visibleProperty.getValue();
    }
    
    public void setVisible( boolean visible ) {
        visibleProperty.setValue( visible );
    }
    
    public void addVisibleObserver( SimpleObserver o ) {
        visibleProperty.addObserver( o );
    }
    
    public void addProbeLocationListener( SimpleObserver o ) {
        probeLocationProperty.addObserver( o );
    }
    
    public void setProbeLocation( Point3D location ) {
        probeLocationProperty.setValue( world.getConstrainedLocation( location ) );
    }
    
    public Point3D getProbeLocation() {
        return new Point3D.Double( probeLocationProperty.getValue() );
    }
    
    public Point3D getProbeLocationReference() {
        return probeLocationProperty.getValue();
    }
    
    public void addPlateVectorListener( SimpleObserver o ) {
        plateVectorProperty.addObserver( o );
    }
    
    public double getPlateVector() {
        return plateVectorProperty.getValue();
    }
    
    public void addDielectricVectorListener( SimpleObserver o ) {
        dielectricVectorProperty.addObserver( o );
    }
    
    public double getDielectricVector() {
        return dielectricVectorProperty.getValue();
    }
    
    public void addSumVectorListener( SimpleObserver o ) {
        sumVectorProperty.addObserver( o );
    }
    
    public double getSumVector() {
        return sumVectorProperty.getValue();
    }
    
    public void addPlateVisibleListener( SimpleObserver o ) {
        plateVisibleProperty.addObserver( o );
    }
    
    public void setPlateVisible( boolean visible ) {
        plateVisibleProperty.setValue( visible );
    }
    
    public boolean isPlateVisible() {
        return plateVisibleProperty.getValue();
    }
    
    public void addDielectricVisibleListener( SimpleObserver o ) {
        dielectricVisibleProperty.addObserver( o );
    }
    
    public void setDielectricVisible( boolean visible ) {
        dielectricVisibleProperty.setValue( visible );
    }
    
    public boolean isDielectricVisible() {
        return dielectricVisibleProperty.getValue();
    }
    
    public void addSumVisibleListener( SimpleObserver o ) {
        sumVisibleProperty.addObserver( o );
    }
    
    public void setSumVisible( boolean visible ) {
        sumVisibleProperty.setValue( visible );
    }
    
    public boolean isSumVectorVisible() {
        return sumVisibleProperty.getValue();
    }
    
    public void addValuesVisibleListener( SimpleObserver o ) {
        valuesVisibleProperty.addObserver( o );
    }
    
    public void setValuesVisible( boolean visible ) {
        valuesVisibleProperty.setValue( visible );
    }
    
    public boolean isValuesVisible() {
        return valuesVisibleProperty.getValue();
    }
    
    private void updateVectors() {
        // update values displayed by the meter based on probe location
        plateVectorProperty.setValue( circuit.getPlatesDielectricEFieldAt( probeLocationProperty.getValue() ) );
        dielectricVectorProperty.setValue( circuit.getDielectricEFieldAt( probeLocationProperty.getValue() ) );
        sumVectorProperty.setValue( circuit.getEffectiveEFieldAt( probeLocationProperty.getValue() ) );
    }
}
