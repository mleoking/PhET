// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.meter;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.WorldBounds;
import edu.colorado.phet.capacitorlab.model.WorldLocationProperty;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit.CircuitChangeListener;
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

    private ICircuit circuit;
    private final CLModelViewTransform3D mvt;
    private final CircuitChangeListener circuitChangeListener;
    private boolean hasBeenVisible; // true if the detector has ever been visible

    public EFieldDetector( ICircuit circuit, final WorldBounds worldBounds, CLModelViewTransform3D mvt, Point3D bodyLocation, Point3D probeLocation,
                           boolean visible, boolean plateVisible, boolean dielectricVisible, boolean sumVisible, boolean valuesVisible ) {

        this.circuit = circuit;
        this.mvt = mvt;

        this.visibleProperty = new Property<Boolean>( visible );
        this.bodyLocationProperty = new WorldLocationProperty( worldBounds, bodyLocation );
        this.probeLocationProperty = new WorldLocationProperty( worldBounds, probeLocation );

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
            circuitChangeListener = new CircuitChangeListener() {
                public void circuitChanged() {
                    updateVectors();
                }
            };
            circuit.addCircuitChangeListener( circuitChangeListener );

            // update vectors when the probe moves
            probeLocationProperty.addObserver( new SimpleObserver() {
                public void update() {
                    updateVectors();
                }
            } );

            // When the E-field detector becomes visible for the first time, put the probe between the plates of the first capacitor.
            hasBeenVisible = visible;
            visibleProperty.addObserver( new SimpleObserver() {
                public void update() {
                    if ( !hasBeenVisible ) {
                        hasBeenVisible = true;
                        moveProbe( EFieldDetector.this.circuit.getCapacitors().get( 0 ) );
                    }
                }
            }, false /* notifyOnAdd */ );
        }
    }

    // Moves the probes so that it's between the plates of the specified capacitor.
    private void moveProbe( Capacitor capacitor ) {
        probeLocationProperty.set( capacitor.getLocation() );
        /*
        * If the probe intersects the top plate, then it will appear to be outside the
        * capacitor and won't read the E-field. In this case, move the probe down a
        * bit so that it looks like it's still between the plates, and will measure
        * the E-Field.
        */
        if ( probeIntersectsTopPlate( capacitor ) ) {
            double x = capacitor.getX();
            double y = capacitor.getY() + ( capacitor.getPlateSeparation() / 2 );
            double z = capacitor.getZ();
            probeLocationProperty.set( new Point3D.Double( x, y, z ) );
        }
    }

    // Does the probe intersect the top plate of the specified capacitor?
    private boolean probeIntersectsTopPlate( Capacitor capacitor ) {
        Shape topPlateShape = capacitor.getShapeCreator().createTopPlateShapeOccluded();
        Point2D viewPoint = mvt.modelToView( probeLocationProperty.get() );
        return topPlateShape.contains( viewPoint );
    }

    public void reset() {
        visibleProperty.reset();
        hasBeenVisible = visibleProperty.get();
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
            this.circuit.removeCircuitChangeListener( circuitChangeListener );
            this.circuit = circuit;
            this.circuit.addCircuitChangeListener( circuitChangeListener );
            updateVectors();
        }
    }

    public void addPlateVectorObserver( SimpleObserver o ) {
        plateVectorProperty.addObserver( o );
    }

    public double getPlateVector() {
        return plateVectorProperty.get();
    }

    public void addDielectricVectorObserver( SimpleObserver o ) {
        dielectricVectorProperty.addObserver( o );
    }

    public double getDielectricVector() {
        return dielectricVectorProperty.get();
    }

    public void addSumVectorObserver( SimpleObserver o ) {
        sumVectorProperty.addObserver( o );
    }

    public double getSumVector() {
        return sumVectorProperty.get();
    }

    private void updateVectors() {
        // update values displayed by the meter based on probe location
        plateVectorProperty.set( circuit.getPlatesDielectricEFieldAt( probeLocationProperty.get() ) );
        dielectricVectorProperty.set( circuit.getDielectricEFieldAt( probeLocationProperty.get() ) );
        sumVectorProperty.set( circuit.getEffectiveEFieldAt( probeLocationProperty.get() ) );
    }
}
