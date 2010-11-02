/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Voltmeter model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Voltmeter {
    
    private final BatteryCapacitorCircuit circuit;
    private final World world;
    
    // observable properties
    private final Property<Boolean> visible;
    private final Property<Point3D> positiveProbeLocation, negativeProbeLocation;
    private final Property<Double> value;

    public Voltmeter( BatteryCapacitorCircuit circuit, World world, boolean visible, Point3D positiveProbeLocation, Point3D negativeProbeLocation ) {
       
        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void efieldChanged() {
                updateValue();
            }
        });
        
        this.world = world;
        this.visible = new Property<Boolean>( visible );
        this.positiveProbeLocation = new Property<Point3D>( positiveProbeLocation );
        this.negativeProbeLocation = new Property<Point3D>( negativeProbeLocation );
        this.value = new Property<Double>( 0d ); // will be properly initialized by updateValue
        
        world.addBoundsObserver( new SimpleObserver() {
            public void update() {
                constrainProbeLocation( Voltmeter.this.positiveProbeLocation );
                constrainProbeLocation( Voltmeter.this.negativeProbeLocation );
            }
        } );
        
        updateValue();
    }
    
    private void updateValue() {
        value.setValue( circuit.getVoltageBetween( positiveProbeLocation.getValue(), negativeProbeLocation.getValue() ) );
    }
    
    public void reset() {
        visible.reset();
        positiveProbeLocation.reset();
        negativeProbeLocation.reset();
        // value property updates other properties are reset
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
    
    public Point3D getPositiveProbeLocationReference() {
        return positiveProbeLocation.getValue();
    }
    
    public void setPositiveProbeLocation( Point3D location ) {
        if ( !location.equals( getPositiveProbeLocationReference() )) {
            this.positiveProbeLocation.setValue( new Point3D.Double( location ) );
            updateValue();
        }
    }
    
    public void addPositiveProbeLocationObserver( SimpleObserver o ) {
        positiveProbeLocation.addObserver( o );
    }
    
    public Point3D getNegativeProbeLocationReference() {
        return negativeProbeLocation.getValue();
    }
    
    public void setNegativeProbeLocation( Point3D location ) {
        if ( !location.equals( getNegativeProbeLocationReference() )) {
            this.negativeProbeLocation.setValue( new Point3D.Double( location ) );
            updateValue();
        }
    }
    
    public void addNegativeProbeLocationObserver( SimpleObserver o ) {
        negativeProbeLocation.addObserver( o );
    }

    public double getValue() {
        return value.getValue();
    }
    
    public void setValue( double value ) {
        if ( value != getValue() ) {
            this.value.setValue( value );
        }
    }
    
    public void addValueObserver( SimpleObserver o ) {
        value.addObserver( o );
    }
    
    private void constrainProbeLocation( Property<Point3D> probeLocation ) {
        if ( !world.contains( probeLocation.getValue() ) ) {
            
            // adjust x coordinate
            double newX = probeLocation.getValue().getX();
            if ( probeLocation.getValue().getX() < world.getBoundsReference().getX() ) {
                newX = world.getBoundsReference().getX();
            }
            else if ( probeLocation.getValue().getX() > world.getBoundsReference().getMaxX() ) {
                newX = world.getBoundsReference().getMaxX();
            }
            
            // adjust y coordinate
            double newY = probeLocation.getValue().getY();
            if ( probeLocation.getValue().getY() < world.getBoundsReference().getY() ) {
                newY = world.getBoundsReference().getY();
            }
            else if ( probeLocation.getValue().getY() > world.getBoundsReference().getMaxY() ) {
                newY = world.getBoundsReference().getMaxY();
            }
            
            // z is fixed
            final double z = probeLocation.getValue().getZ();
            
            probeLocation.setValue( new Point3D.Double( newX, newY, z ) );
        }
    }
}
