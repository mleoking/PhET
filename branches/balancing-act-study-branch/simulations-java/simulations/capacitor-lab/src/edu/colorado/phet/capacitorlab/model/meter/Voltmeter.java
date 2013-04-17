// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.meter;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.WorldBounds;
import edu.colorado.phet.capacitorlab.model.WorldLocationProperty;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit.CircuitChangeListener;
import edu.colorado.phet.capacitorlab.shapes.VoltmeterShapeCreator;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
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

    private final VoltmeterShapeCreator shapeCreator; // determines shape of probe tips in view coordinates

    // public observable properties
    public final Property<Boolean> visibleProperty;
    public final WorldLocationProperty bodyLocationProperty, positiveProbeLocationProperty, negativeProbeLocationProperty;

    // derived observable properties
    private final Property<Double> valueProperty;

    private ICircuit circuit;
    private final CircuitChangeListener circuitChangeListener;


    public Voltmeter( ICircuit circuit, final WorldBounds worldBounds, CLModelViewTransform3D mvt,
                      Point3D bodyLocation, Point3D positiveProbeLocation, Point3D negativeProbeLocation, boolean visible ) {

        this.circuit = circuit;
        circuitChangeListener = new CircuitChangeListener() {
            public void circuitChanged() {
                updateValue();
            }
        };
        circuit.addCircuitChangeListener( circuitChangeListener );

        this.shapeCreator = new VoltmeterShapeCreator( this, mvt );
        this.visibleProperty = new Property<Boolean>( visible );
        this.bodyLocationProperty = new WorldLocationProperty( worldBounds, bodyLocation );
        this.positiveProbeLocationProperty = new WorldLocationProperty( worldBounds, positiveProbeLocation );
        this.negativeProbeLocationProperty = new WorldLocationProperty( worldBounds, negativeProbeLocation );
        this.valueProperty = new Property<Double>( 0d ); // will be properly initialized by updateValue

        // update value when probes move
        RichSimpleObserver probeObserver = new RichSimpleObserver() {
            public void update() {
                updateValue();
            }
        };
        probeObserver.observe( positiveProbeLocationProperty, negativeProbeLocationProperty );
    }

    private void updateValue() {
        if ( probesAreTouching() ) {
            valueProperty.set( 0d );
        }
        else {
            valueProperty.set( circuit.getVoltageBetween( shapeCreator.getPositiveProbeTipShape(), shapeCreator.getNegativeProbeTipShape() ) );
        }
    }

    // Probes are touching if their tips intersect.
    private boolean probesAreTouching() {
        return ShapeUtils.intersects( shapeCreator.getPositiveProbeTipShape(), shapeCreator.getNegativeProbeTipShape() );
    }

    public void reset() {
        visibleProperty.reset();
        bodyLocationProperty.reset();
        positiveProbeLocationProperty.reset();
        negativeProbeLocationProperty.reset();
        // valueProperty updates when other properties are reset
    }

    public void setCircuit( ICircuit circuit ) {
        if ( circuit != this.circuit ) {
            this.circuit.removeCircuitChangeListener( circuitChangeListener );
            this.circuit = circuit;
            this.circuit.addCircuitChangeListener( circuitChangeListener );
            updateValue();
        }
    }

    public boolean isVisible() {
        return visibleProperty.get();
    }

    public double getValue() {
        return valueProperty.get();
    }

    public void addValueObserver( SimpleObserver o ) {
        valueProperty.addObserver( o );
    }

    public VoltmeterShapeCreator getShapeCreator() {
        return shapeCreator;
    }

    public Dimension2D getProbeTipSizeReference() {
        return PROBE_TIP_SIZE;
    }
}
