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
    private final World world;
    
    // observable properties
    private final Property<Boolean> visibleProperty;
    private final Property<Point3D> bodyLocationProperty, probeLocationProperty;
    private final Property<Boolean> plateVisibleProperty, dielectricVisibleProperty, sumVisibleProperty, valuesVisibleProperty;
    
    // derived observable properties
    private final Property<Double> plateVectorProperty; // field due to the plate (E_plate_dielectric or E_plate_air, depending on probe location)
    private final Property<Double> dielectricVectorProperty; // field due to dielectric polarization (E_dielectric or E_air, depending on probe location)
    private final Property<Double> sumVectorProperty; // effective (net) field between the plates (E_effective)
    
    public EFieldDetector( BatteryCapacitorCircuit circuit, final World world, Point3D bodyLocation, Point3D probeLocation, 
            boolean visible, boolean plateVisible, boolean dielectricVisible, boolean sumVisible, boolean valuesVisible ) {
        
        this.circuit = circuit;
        
        this.world = world;
        
        this.visibleProperty = new Property<Boolean>( visible );
        this.bodyLocationProperty = new Property<Point3D>( new Point3D.Double( bodyLocation ) );
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
            // update vectors when the circuit changes
            circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
                public void circuitChanged() {
                    updateVectors();
                }
            });
            
            // update vectors when the probe moves
            probeLocationProperty.addObserver( new SimpleObserver() {
                public void update() {
                    updateVectors();
                }
            } );
            
            // keep the body and probe inside the world bounds
            world.addBoundsObserver( new SimpleObserver() {
                public void update() {
                    setBodyLocation( getBodyLocationReference() );
                    setProbeLocation( getProbeLocation() );
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
    
    public boolean isVisible() {
        return visibleProperty.getValue();
    }
    
    public void setVisible( boolean visible ) {
        visibleProperty.setValue( visible );
    }
    
    public void addVisibleObserver( SimpleObserver o ) {
        visibleProperty.addObserver( o );
    }
    
    public Property<Boolean> getVisibleProperty() {
        return visibleProperty;
    }
    
    public Point3D getBodyLocationReference() {
        return bodyLocationProperty.getValue();
    }
    
    public void setBodyLocation( Point3D location ) {
        bodyLocationProperty.setValue( world.getConstrainedLocation( location ) );
    }
    
    public void addBodyLocationObserver( SimpleObserver o ) {
        bodyLocationProperty.addObserver( o );
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
    
    public void addProbeLocationObserver( SimpleObserver o ) {
        probeLocationProperty.addObserver( o );
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
    
    public void addPlateVisibleObserver( SimpleObserver o ) {
        plateVisibleProperty.addObserver( o );
    }
    
    public void setPlateVisible( boolean visible ) {
        plateVisibleProperty.setValue( visible );
    }
    
    public boolean isPlateVisible() {
        return plateVisibleProperty.getValue();
    }
    
    public void addDielectricVisibleObserver( SimpleObserver o ) {
        dielectricVisibleProperty.addObserver( o );
    }
    
    public void setDielectricVisible( boolean visible ) {
        dielectricVisibleProperty.setValue( visible );
    }
    
    public boolean isDielectricVisible() {
        return dielectricVisibleProperty.getValue();
    }
    
    public void addSumVisibleObserver( SimpleObserver o ) {
        sumVisibleProperty.addObserver( o );
    }
    
    public void setSumVisible( boolean visible ) {
        sumVisibleProperty.setValue( visible );
    }
    
    public boolean isSumVectorVisible() {
        return sumVisibleProperty.getValue();
    }
    
    public void addValuesVisibleObserver( SimpleObserver o ) {
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
