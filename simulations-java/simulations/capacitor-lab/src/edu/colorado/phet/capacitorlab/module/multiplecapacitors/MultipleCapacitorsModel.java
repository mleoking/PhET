// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Air;
import edu.colorado.phet.capacitorlab.model.circuit.*;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.CapacitanceMeter;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.PlateChargeMeter;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.StoredEnergyMeter;
import edu.colorado.phet.capacitorlab.model.meter.EFieldDetector;
import edu.colorado.phet.capacitorlab.model.meter.Voltmeter;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;


/**
 * Model for the "Multiple Capacitors" module.
 * </p>
 * This model is somewhat similar to DielectricModel, but was developed much later,
 * and is parametrized differently (with a large number of parameters).
 * I attempted to force some of the common bits into the base class, but it became messy and less readable.
 * So I decided that a bit of duplication is preferable here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsModel extends CLModel {

    //================================================================================
    // Model parameter values
    //================================================================================

    // Circuits
    private static final Point3D BATTERY_LOCATION = new Point3D.Double( 0.005, 0.030, 0 ); // meters
    private static final double CAPACITOR_X_SPACING = 0.018; // meters
    private static final double CAPACITOR_Y_SPACING = 0.016; // meters
    private static final DielectricMaterial DIELECTRIC_MATERIAL = new Air();
    private static final double DIELECTRIC_OFFSET = 0;
    private static final double PLATE_WIDTH = 0.0075; // meters
    private static final double PLATE_SEPARATION = Capacitor.getPlateSeparation( DIELECTRIC_MATERIAL.getDielectricConstant(), PLATE_WIDTH, CLConstants.CAPACITANCE_RANGE.getMin() );
    private static final double WIRE_THICKNESS = CLConstants.WIRE_THICKNESS;
    private static final double WIRE_EXTENT = 0.01; // how far a wire extends above or below topmost capacitor's origin, in meters

    // Capacitance meter
    public static final Point3D CAPACITANCE_METER_LOCATION = new Point3D.Double( 0.033, 0.0017, 0 );
    public static final boolean CAPACITANCE_METER_VISIBLE = false;

    // Plate Charge meter
    public static final Point3D PLATE_CHARGE_METER_LOCATION = new Point3D.Double( 0.046, 0.0017, 0 );
    public static final boolean PLATE_CHARGE_METER_VISIBLE = false;

    // Stored Energy meter
    public static final Point3D STORED_ENERGY_METER_LOCATION = new Point3D.Double( 0.059, 0.0017, 0 );
    public static final boolean STORED_ENERGY_METER_VISIBLE = false;

    // E-Field Detector
    public static final Point3D EFIELD_DETECTOR_BODY_LOCATION = new Point3D.Double( 0.05, 0.041, 0 );
    public static final Point3D EFIELD_DETECTOR_PROBE_LOCATION = BATTERY_LOCATION;
    public static final boolean EFIELD_DETECTOR_VISIBLE = false;
    public static final boolean EFIELD_PLATE_VECTOR_VISIBLE = true;
    public static final boolean EFIELD_DIELECTRIC_VECTOR_VISIBLE = true;
    public static final boolean EFIELD_SUM_VECTOR_VISIBLE = true;
    public static final boolean EFIELD_VALUES_VISIBLE = true;

    // Voltmeter
    public static final Point3D VOLTMETER_BODY_LOCATION = new Point3D.Double( 0.04, 0.041, 0 );
    public static final Point3D VOLTMETER_POSITIVE_PROBE_LOCATION = new Point3D.Double( 0.035, 0.042, 0 );
    public static final Point3D VOLTMETER_NEGATIVE_PROBE_LOCATION = new Point3D.Double( 0.038, 0.042, 0 );
    public static final boolean VOLTMETER_VISIBLE = false;

    //================================================================================

    private final ArrayList<ICircuit> circuits; // the set of circuits to choose from

    public final Property<ICircuit> currentCircuitProperty; // the circuit that is currently chosen by the user
    public final CapacitanceMeter capacitanceMeter;
    public final PlateChargeMeter plateChargeMeter;
    public final StoredEnergyMeter storedEnergyMeter;
    public final EFieldDetector eFieldDetector;
    public final Voltmeter voltmeter;

    public MultipleCapacitorsModel( final IClock clock, final CLModelViewTransform3D mvt ) {

        // configuration info common to all circuits
        final CircuitConfig circuitConfig = new CircuitConfig( clock,
                                                               mvt,
                                                               BATTERY_LOCATION,
                                                               CAPACITOR_X_SPACING,
                                                               CAPACITOR_Y_SPACING,
                                                               PLATE_WIDTH,
                                                               PLATE_SEPARATION,
                                                               DIELECTRIC_MATERIAL,
                                                               DIELECTRIC_OFFSET,
                                                               WIRE_THICKNESS,
                                                               WIRE_EXTENT );

        // create circuits
        circuits = new ArrayList<ICircuit>() {{
            add( new SingleCircuit( circuitConfig ) );
            add( new SeriesCircuit( circuitConfig, CLStrings.TWO_IN_SERIES, 2 ) );
            add( new SeriesCircuit( circuitConfig, CLStrings.THREE_IN_SERIES, 3 ) );
            add( new ParallelCircuit( circuitConfig, CLStrings.TWO_IN_PARALLEL, 2 ) );
            add( new ParallelCircuit( circuitConfig, CLStrings.THREE_IN_PARALLEL, 3 ) );
            add( new Combination1Circuit( circuitConfig ) );
            add( new Combination2Circuit( circuitConfig ) );
        }};
        currentCircuitProperty = new Property<ICircuit>( circuits.get( 0 ) );

        capacitanceMeter = new CapacitanceMeter( currentCircuitProperty.get(), getWorldBounds(), CAPACITANCE_METER_LOCATION, CAPACITANCE_METER_VISIBLE );
        plateChargeMeter = new PlateChargeMeter( currentCircuitProperty.get(), getWorldBounds(), PLATE_CHARGE_METER_LOCATION, PLATE_CHARGE_METER_VISIBLE );
        storedEnergyMeter = new StoredEnergyMeter( currentCircuitProperty.get(), getWorldBounds(), STORED_ENERGY_METER_LOCATION, STORED_ENERGY_METER_VISIBLE );

        eFieldDetector = new EFieldDetector( currentCircuitProperty.get(), getWorldBounds(), mvt, EFIELD_DETECTOR_BODY_LOCATION, EFIELD_DETECTOR_PROBE_LOCATION,
                                             EFIELD_DETECTOR_VISIBLE, EFIELD_PLATE_VECTOR_VISIBLE, EFIELD_DIELECTRIC_VECTOR_VISIBLE,
                                             EFIELD_SUM_VECTOR_VISIBLE, EFIELD_VALUES_VISIBLE );

        voltmeter = new Voltmeter( currentCircuitProperty.get(), getWorldBounds(), mvt,
                                   VOLTMETER_BODY_LOCATION, VOLTMETER_POSITIVE_PROBE_LOCATION, VOLTMETER_NEGATIVE_PROBE_LOCATION,
                                   VOLTMETER_VISIBLE );

        // when the user's circuit selection changes...
        currentCircuitProperty.addObserver( new SimpleObserver() {
            public void update() {
                ICircuit circuit = currentCircuitProperty.get();
                capacitanceMeter.setCircuit( circuit );
                plateChargeMeter.setCircuit( circuit );
                storedEnergyMeter.setCircuit( circuit );
                eFieldDetector.setCircuit( circuit );
                voltmeter.setCircuit( circuit );
            }
        } );

        // synchronize battery voltages in all circuits, so that it looks like circuits share one battery
        {
            final Property<Double> batteryVoltageProperty = new Property<Double>( currentCircuitProperty.get().getBattery().getVoltage() );
            batteryVoltageProperty.addObserver( new SimpleObserver() {
                public void update() {
                    for ( ICircuit circuit : circuits ) {
                        circuit.getBattery().setVoltage( batteryVoltageProperty.get() );
                    }
                }
            } );
            for ( final ICircuit circuit : circuits ) {
                circuit.getBattery().addVoltageObserver( new SimpleObserver() {
                    public void update() {
                        batteryVoltageProperty.set( circuit.getBattery().getVoltage() );
                    }
                } );
            }
        }
    }

    public void reset() {
        capacitanceMeter.reset();
        plateChargeMeter.reset();
        storedEnergyMeter.reset();
        eFieldDetector.reset();
        voltmeter.reset();
        for ( ICircuit circuit : circuits ) {
            circuit.reset();
        }
        currentCircuitProperty.reset();
    }

    // Gets the list of circuits, but returns a new list so that no one messes with the model's list.
    public ArrayList<ICircuit> getCircuits() {
        return new ArrayList<ICircuit>( circuits );
    }

    /*
     * Gets the E-field reference magnitude, used to determine the initial scale of the E-Field Detector.
     * This is based on the default capacitor configuration, with maximum battery voltage.
     */
    public static double getEFieldReferenceMagnitude() {
        Capacitor capacitor = new Capacitor( new Point3D.Double(),
                                             PLATE_WIDTH,
                                             PLATE_SEPARATION,
                                             new Air(),
                                             0,
                                             new CLModelViewTransform3D() );
        capacitor.setPlatesVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        return capacitor.getEffectiveEField();
    }
}
