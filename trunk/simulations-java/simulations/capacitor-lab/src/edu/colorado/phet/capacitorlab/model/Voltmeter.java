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
        
        world.addBoundsObserver( new SimpleObserver() {
            public void update() {
                if ( !world.isBoundsEmpty() ) {
                    constrainProbeLocation( Voltmeter.this.positiveProbeLocationProperty );
                    constrainProbeLocation( Voltmeter.this.negativeProbeLocationProperty );
                }
            }
        } );
        
        updateValue();
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
        if ( !location.equals( getPositiveProbeLocationReference() )) {
            this.positiveProbeLocationProperty.setValue( new Point3D.Double( location ) );
            updateValue();
        }
    }
    
    public void addPositiveProbeLocationObserver( SimpleObserver o ) {
        positiveProbeLocationProperty.addObserver( o );
    }
    
    public Point3D getNegativeProbeLocationReference() {
        return negativeProbeLocationProperty.getValue();
    }
    
    public void setNegativeProbeLocation( Point3D location ) {
        if ( !location.equals( getNegativeProbeLocationReference() )) {
            this.negativeProbeLocationProperty.setValue( new Point3D.Double( location ) );
            updateValue();
        }
    }
    
    public void addNegativeProbeLocationObserver( SimpleObserver o ) {
        negativeProbeLocationProperty.addObserver( o );
    }

    public double getValue() {
        return valueProperty.getValue();
    }
    
    public void setValue( double value ) {
        if ( value != getValue() ) {
            this.valueProperty.setValue( value );
        }
    }
    
    public void addValueObserver( SimpleObserver o ) {
        valueProperty.addObserver( o );
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
