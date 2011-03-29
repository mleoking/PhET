// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeListener;
import edu.colorado.phet.capacitorlab.shapes.VoltmeterShapeFactory;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Voltmeter model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Voltmeter {

    // size of the probe tips, determined by visual inspection of the associated image files
    private final PDimension PROBE_TIP_SIZE = new PDimension( 0.0005, 0.0015 ); // meters

    private final BatteryCapacitorCircuit circuit;
    private final VoltmeterShapeFactory shapeFactory;

    // directly observable properties
    public final Property<Boolean> visible;
    public final WorldLocationProperty bodyLocation, positiveProbeLocation, negativeProbeLocation;

    // derived observable properties
    private final Property<Double> valueProperty;

    public Voltmeter( BatteryCapacitorCircuit circuit, final World world, CLModelViewTransform3D mvt,
            Point3D bodyLocation, Point3D positiveProbeLocation, Point3D negativeProbeLocation, boolean visible ) {

        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
            public void circuitChanged() {
                updateValue();
            }
        });

        this.shapeFactory = new VoltmeterShapeFactory( this, mvt );
        this.visible = new Property<Boolean>( visible );
        this.bodyLocation = new WorldLocationProperty( world, bodyLocation );
        this.positiveProbeLocation = new WorldLocationProperty( world, positiveProbeLocation );
        this.negativeProbeLocation = new WorldLocationProperty( world, negativeProbeLocation );
        this.valueProperty = new Property<Double>( 0d ); // will be properly initialized by updateValue

        // update value when probes move
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                updateValue();
            }
        };
        this.positiveProbeLocation.addObserver( o );
        this.negativeProbeLocation.addObserver( o );
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
        visible.reset();
        bodyLocation.reset();
        positiveProbeLocation.reset();
        negativeProbeLocation.reset();
        // value property updates other properties are reset
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

    public VoltmeterShapeFactory getShapeFactory() {
        return shapeFactory;
    }

    public Dimension2D getProbeTipSizeReference() {
        return PROBE_TIP_SIZE;
    }
}
