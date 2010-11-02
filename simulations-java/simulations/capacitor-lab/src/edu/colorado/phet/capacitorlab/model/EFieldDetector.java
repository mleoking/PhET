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
    private final Property<Boolean> visible;
    private final Property<Point3D> probeLocation;
    private final Property<Double> plateVector; // field due to the plate (E_plate_dielectric or E_plate_air, depending on probe location)
    private final Property<Double> dielectricVector; // field due to dielectric polarization (E_dielectric or E_air, depending on probe location)
    private final Property<Double> sumVector; // effective (net) field between the plates (E_effective)
    private final Property<Boolean> plateVisible, dielectricVisible, sumVisible, valuesVisible;
    
    public EFieldDetector( BatteryCapacitorCircuit circuit, World world, Point3D probeLocation, boolean visible, boolean plateVisible, boolean dielectricVisible, boolean sumVisible, boolean valuesVisible ) {
        
        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void efieldChanged() {
                updateVectors();
            }
        });
        
        this.world = world;
        
        this.visible = new Property<Boolean>( visible );
        this.probeLocation = new Property<Point3D>( new Point3D.Double( probeLocation ) );
        
        this.plateVector = new Property<Double>( 0d );
        this.dielectricVector = new Property<Double>( 0d );
        this.sumVector = new Property<Double>( 0d );
        
        this.plateVisible = new Property<Boolean>( plateVisible );
        this.dielectricVisible = new Property<Boolean>( dielectricVisible );
        this.sumVisible = new Property<Boolean>( sumVisible );
        this.valuesVisible = new Property<Boolean>( valuesVisible );
        
        world.addBoundsObserver( new SimpleObserver() {
            public void update() {
                constrainProbeLocation();
            }
        } );
        
        updateVectors();
    }
    
    public void reset() {
        visible.reset();
        probeLocation.reset();
        plateVisible.reset();
        dielectricVisible.reset();
        sumVisible.reset();
        valuesVisible.reset();
        // vector properties reset probe location is reset
    }
    
    public boolean isVisible() {
        return visible.getValue();
    }
    
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            this.visible.setValue( visible );
        }
    }
    
    public void addVisibleObserver( SimpleObserver o ) {
        visible.addObserver( o );
    }
    
    private void updateVectors() {
        // update values displayed by the meter based on probe location
        plateVector.setValue( circuit.getPlatesDielectricEFieldAt( probeLocation.getValue() ) );
        dielectricVector.setValue( circuit.getDielectricEFieldAt( probeLocation.getValue() ) );
        sumVector.setValue( circuit.getEffectiveEFieldAt( probeLocation.getValue() ) );
    }
    
    public void addProbeLocationListener( SimpleObserver o ) {
        probeLocation.addObserver( o );
    }
    
    public void setProbeLocation( Point3D probeLocation ) {
        if ( !probeLocation.equals( getProbeLocationReference() ) ) {
            this.probeLocation.setValue( new Point3D.Double( probeLocation ) );
            updateVectors();
        }
    }
    
    public Point3D getProbeLocation() {
        return new Point3D.Double( probeLocation.getValue() );
    }
    
    public Point3D getProbeLocationReference() {
        return probeLocation.getValue();
    }
    
    public void addPlateVectorListener( SimpleObserver o ) {
        plateVector.addObserver( o );
    }
    
    public double getPlateVector() {
        return plateVector.getValue();
    }
    
    public void addDielectricVectorListener( SimpleObserver o ) {
        dielectricVector.addObserver( o );
    }
    
    public double getDielectricVector() {
        return dielectricVector.getValue();
    }
    
    public void addSumVectorListener( SimpleObserver o ) {
        sumVector.addObserver( o );
    }
    
    public double getSumVector() {
        return sumVector.getValue();
    }
    
    public void addPlateVisibleListener( SimpleObserver o ) {
        plateVisible.addObserver( o );
    }
    
    public void setPlateVisible( boolean visible ) {
        plateVisible.setValue( visible );
    }
    
    public boolean isPlateVisible() {
        return plateVisible.getValue();
    }
    
    public void addDielectricVisibleListener( SimpleObserver o ) {
        dielectricVisible.addObserver( o );
    }
    
    public void setDielectricVisible( boolean visible ) {
        dielectricVisible.setValue( visible );
    }
    
    public boolean isDielectricVisible() {
        return dielectricVisible.getValue();
    }
    
    public void addSumVisibleListener( SimpleObserver o ) {
        sumVisible.addObserver( o );
    }
    
    public void setSumVisible( boolean visible ) {
        sumVisible.setValue( visible );
    }
    
    public boolean isSumVectorVisible() {
        return sumVisible.getValue();
    }
    
    public void addValuesVisibleListener( SimpleObserver o ) {
        valuesVisible.addObserver( o );
    }
    
    public void setValuesVisible( boolean visible ) {
        valuesVisible.setValue( visible );
    }
    
    public boolean isValuesVisible() {
        return valuesVisible.getValue();
    }
    
    /*
     * Ensures that the probe remains inside the world bounds.
     */
    private void constrainProbeLocation() {
        Point3D eFieldProbeLocation = getProbeLocationReference();
        if ( !world.contains( eFieldProbeLocation ) ) {
            
            // adjust x coordinate
            double newX = eFieldProbeLocation.getX();
            if ( eFieldProbeLocation.getX() < world.getBoundsReference().getX() ) {
                newX = world.getBoundsReference().getX();
            }
            else if ( eFieldProbeLocation.getX() > world.getBoundsReference().getMaxX() ) {
                newX = world.getBoundsReference().getMaxX();
            }
            
            // adjust y coordinate
            double newY = eFieldProbeLocation.getY();
            if ( eFieldProbeLocation.getY() < world.getBoundsReference().getY() ) {
                newY = world.getBoundsReference().getY();
            }
            else if ( eFieldProbeLocation.getY() > world.getBoundsReference().getMaxY() ) {
                newY = world.getBoundsReference().getMaxY();
            }
            
            // z is fixed
            final double z = eFieldProbeLocation.getZ();
            
            setProbeLocation( new Point3D.Double( newX, newY, z ) );
        }
    }
}
