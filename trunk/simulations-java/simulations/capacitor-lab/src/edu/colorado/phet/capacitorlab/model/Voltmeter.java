// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.capacitorlab.model.ICircuit.CircuitChangeListener;
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

    private final ICircuit circuit;
    private final VoltmeterShapeFactory shapeFactory;

    // observable properties
    private final Property<Boolean> visibleProperty;
    private final WorldLocationProperty bodyLocationProperty, positiveProbeLocationProperty, negativeProbeLocationProperty;

    // derived observable properties
    private final Property<Double> valueProperty;

    public Voltmeter( ICircuit circuit, final World world, CLModelViewTransform3D mvt,
            Point3D bodyLocation, Point3D positiveProbeLocation, Point3D negativeProbeLocation, boolean visible ) {

        this.circuit = circuit;
        circuit.addCircuitChangeListener( new CircuitChangeListener() {
            public void circuitChanged() {
                updateValue();
            }
        });

        this.shapeFactory = new VoltmeterShapeFactory( this, mvt );
        this.visibleProperty = new Property<Boolean>( visible );
        this.bodyLocationProperty = new WorldLocationProperty( world, bodyLocation );
        this.positiveProbeLocationProperty = new WorldLocationProperty( world, positiveProbeLocation );
        this.negativeProbeLocationProperty = new WorldLocationProperty( world, negativeProbeLocation );
        this.valueProperty = new Property<Double>( 0d ); // will be properly initialized by updateValue

        // update value when probes move
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                updateValue();
            }
        };
        positiveProbeLocationProperty.addObserver( o );
        negativeProbeLocationProperty.addObserver( o );
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
        bodyLocationProperty.reset();
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

    public Property<Boolean> getVisibleProperty() {
        return visibleProperty;
    }

    public Point3D getBodyLocationReference() {
        return bodyLocationProperty.getValue();
    }

    public void setBodyLocation( Point3D location ) {
        bodyLocationProperty.setValue( location );
    }

    public void addBodyLocationObserver( SimpleObserver o ) {
        bodyLocationProperty.addObserver( o );
    }

    public Point3D getPositiveProbeLocationReference() {
        return positiveProbeLocationProperty.getValue();
    }

    public void setPositiveProbeLocation( Point3D location ) {
        positiveProbeLocationProperty.setValue( location );
    }

    public void addPositiveProbeLocationObserver( SimpleObserver o ) {
        positiveProbeLocationProperty.addObserver( o );
    }

    public Point3D getNegativeProbeLocationReference() {
        return negativeProbeLocationProperty.getValue();
    }

    public void setNegativeProbeLocation( Point3D location ) {
        negativeProbeLocationProperty.setValue( location );
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

    public VoltmeterShapeFactory getShapeFactory() {
        return shapeFactory;
    }

    public Dimension2D getProbeTipSizeReference() {
        return PROBE_TIP_SIZE;
    }
}
