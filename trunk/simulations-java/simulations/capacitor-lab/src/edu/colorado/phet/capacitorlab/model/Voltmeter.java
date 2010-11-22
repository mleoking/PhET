/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.capacitorlab.shapes.VoltmeterShapeFactory;
import edu.colorado.phet.capacitorlab.util.ShapeUtils;
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
    private final VoltmeterShapeFactory shapeFactory;
    
    // observable properties
    private final Property<Boolean> visibleProperty;
    private final Property<Point3D> positiveProbeLocationProperty, negativeProbeLocationProperty;
    
    // derived observable properties
    private final Property<Double> valueProperty;

    public Voltmeter( BatteryCapacitorCircuit circuit, final World world, CLModelViewTransform3D mvt, boolean visible, Point3D positiveProbeLocation, Point3D negativeProbeLocation ) {
       
        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void efieldChanged() {
                updateValue();
            }
        });
        
        this.world = world;
        this.shapeFactory = new VoltmeterShapeFactory( this, mvt );
        this.visibleProperty = new Property<Boolean>( visible );
        this.positiveProbeLocationProperty = new Property<Point3D>( positiveProbeLocation );
        this.negativeProbeLocationProperty = new Property<Point3D>( negativeProbeLocation );
        this.valueProperty = new Property<Double>( 0d ); // will be properly initialized by updateValue
        
        // observers
        {
            // keep the probes inside the world bounds
            world.addBoundsObserver( new SimpleObserver() {
                public void update() {
                    setPositiveProbeLocation( world.getConstrainedLocation( getPositiveProbeLocationReference() ) );
                    setNegativeProbeLocation( world.getConstrainedLocation( getNegativeProbeLocationReference() ) );
                }
            } );
            
            // update value when probes move
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    updateValue();
                }
            };
            positiveProbeLocationProperty.addObserver( o );
            negativeProbeLocationProperty.addObserver( o );
        }
    }
    
    private void updateValue() {
        if ( probesAreTouching() ) {
            valueProperty.setValue( 0d );
        }
        else {
            valueProperty.setValue( circuit.getVoltageBetween( shapeFactory.getPositiveProbeTipShape(), shapeFactory.getNegativeProbeTipShape() ) );
        }
    }
    
    private boolean probesAreTouching() {
        return ShapeUtils.intersects( shapeFactory.getPositiveProbeTipShape(), shapeFactory.getNegativeProbeTipShape() );
    }
    
    public void reset() {
        visibleProperty.reset();
        positiveProbeLocationProperty.reset();
        negativeProbeLocationProperty.reset();
        // value property updates other properties are reset
    }
    
    public boolean isVisible() {
        return visibleProperty.getValue();
    }
    
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            this.visibleProperty.setValue( visible );
        }
    }
    
    public void addVisibleObserver( SimpleObserver o ) {
        visibleProperty.addObserver( o );
    }
    
    public Point3D getPositiveProbeLocationReference() {
        return positiveProbeLocationProperty.getValue();
    }
    
    public void setPositiveProbeLocation( Point3D location ) {
        positiveProbeLocationProperty.setValue( world.getConstrainedLocation( location ) );
    }
    
    public void addPositiveProbeLocationObserver( SimpleObserver o ) {
        positiveProbeLocationProperty.addObserver( o );
    }
    
    public Point3D getNegativeProbeLocationReference() {
        return negativeProbeLocationProperty.getValue();
    }
    
    public void setNegativeProbeLocation( Point3D location ) {
        negativeProbeLocationProperty.setValue( world.getConstrainedLocation( location ) );
    }
    
    public void addNegativeProbeLocationObserver( SimpleObserver o ) {
        negativeProbeLocationProperty.addObserver( o );
    }

    public double getValue() {
        return valueProperty.getValue();
    }
    
    public void setValue( double value ) {
        valueProperty.setValue( value );
    }
    
    public void addValueObserver( SimpleObserver o ) {
        valueProperty.addObserver( o );
    }
}
