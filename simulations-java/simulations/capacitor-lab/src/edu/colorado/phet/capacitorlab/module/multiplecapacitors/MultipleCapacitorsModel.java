// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.BarMeter.CapacitanceMeter;
import edu.colorado.phet.capacitorlab.model.BarMeter.PlateChargeMeter;
import edu.colorado.phet.capacitorlab.model.BarMeter.StoredEnergyMeter;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Air;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;


/**
 * Model for the "Multiple Capacitors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsModel {

    //TODO decide whether these should live here
    public static final Point3D BATTERY_LOCATION = new Point3D.Double( 0.005, 0.034, 0 ); // meters
    public static final Point3D SINGLE_CAPACITOR_LOCATION = new Point3D.Double( BATTERY_LOCATION.getX() + 0.025, BATTERY_LOCATION.getY(), 0 ); // meters
    public static final DoubleRange CAPACITANCE_RANGE = new DoubleRange( 1E-13, 4E-13 ); // Farads
    public static final int CAPACITANCE_DISPLAY_EXPONENT = -13;
    public static final DielectricMaterial DIELECTRIC_MATERIAL = new Air();
    public static final double DIELECTRIC_OFFSET = 0;
    public static final double PLATE_WIDTH = 0.01; // meters
    public static final double PLATE_SEPARATION = Capacitor.getPlateSeparation( DIELECTRIC_MATERIAL.getDielectricConstant(), PLATE_WIDTH, CAPACITANCE_RANGE.getMin() );

    private final ArrayList<ICircuit> circuits; // the set of circuits to choose from

    // directly observable properties
    public final Property<ICircuit> currentCircuitProperty;

    private final WorldBounds worldBounds;
    private final CapacitanceMeter capacitanceMeter;
    private final PlateChargeMeter plateChargeMeter;
    private final StoredEnergyMeter storedEnergyMeter;
    private final EFieldDetector eFieldDetector;
    private final Voltmeter voltmeter;

    public MultipleCapacitorsModel( final IClock clock, final CLModelViewTransform3D mvt ) {

        // create circuits
        circuits = new ArrayList<ICircuit>() {{
            add( new SingleCircuit( clock, mvt, BATTERY_LOCATION, SINGLE_CAPACITOR_LOCATION, PLATE_WIDTH, PLATE_SEPARATION, DIELECTRIC_MATERIAL, DIELECTRIC_OFFSET ) );
            add( new SeriesCircuit( CLStrings.TWO_IN_SERIES, clock, mvt, BATTERY_LOCATION, 2, PLATE_WIDTH, PLATE_SEPARATION, DIELECTRIC_MATERIAL, DIELECTRIC_OFFSET ) );
            add( new SeriesCircuit( CLStrings.THREE_IN_SERIES, clock, mvt, BATTERY_LOCATION, 3, PLATE_WIDTH, PLATE_SEPARATION, DIELECTRIC_MATERIAL, DIELECTRIC_OFFSET ) );
            add( new NullCircuit( CLStrings.TWO_IN_PARALLEL, mvt, BATTERY_LOCATION ) );
            add( new NullCircuit( CLStrings.THREE_IN_PARALLEL, mvt, BATTERY_LOCATION ) );
            add( new NullCircuit( CLStrings.COMBINATION_1, mvt, BATTERY_LOCATION ) );
            add( new NullCircuit( CLStrings.COMBINATION_2, mvt, BATTERY_LOCATION ) );
        }};

        currentCircuitProperty = new Property<ICircuit>( circuits.get( 0 ) );

        worldBounds = new WorldBounds();

        capacitanceMeter = new CapacitanceMeter( currentCircuitProperty.get(), worldBounds, CLConstants.CAPACITANCE_METER_LOCATION, CLConstants.CAPACITANCE_METER_VISIBLE );
        plateChargeMeter = new PlateChargeMeter( currentCircuitProperty.get(), worldBounds, CLConstants.PLATE_CHARGE_METER_LOCATION, CLConstants.PLATE_CHARGE_METER_VISIBLE );
        storedEnergyMeter = new StoredEnergyMeter( currentCircuitProperty.get(), worldBounds, CLConstants.STORED_ENERGY_METER_LOCATION, CLConstants.STORED_ENERGY_METER_VISIBLE );

        eFieldDetector = new EFieldDetector( currentCircuitProperty.get(), worldBounds, CLConstants.EFIELD_DETECTOR_BODY_LOCATION, CLConstants.EFIELD_DETECTOR_PROBE_LOCATION,
                                             CLConstants.EFIELD_DETECTOR_VISIBLE, CLConstants.EFIELD_PLATE_VECTOR_VISIBLE, CLConstants.EFIELD_DIELECTRIC_VECTOR_VISIBLE,
                                             CLConstants.EFIELD_SUM_VECTOR_VISIBLE, CLConstants.EFIELD_VALUES_VISIBLE );

        voltmeter = new Voltmeter( currentCircuitProperty.get(), worldBounds, mvt,
                                   CLConstants.VOLTMETER_BODY_LOCATION, CLConstants.VOLTMETER_POSITIVE_PROBE_LOCATION, CLConstants.VOLTMETER_NEGATIVE_PROBE_LOCATION,
                                   CLConstants.VOLTMETER_VISIBLE );

        // when the circuit changes...
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

    public WorldBounds getWorldBounds() {
        return worldBounds;
    }

    public ArrayList<ICircuit> getCircuits() {
        return circuits;
    }

    public CapacitanceMeter getCapacitanceMeter() {
        return capacitanceMeter;
    }

    public PlateChargeMeter getPlateChargeMeter() {
        return plateChargeMeter;
    }

    public StoredEnergyMeter getStoredEnergyMeter() {
        return storedEnergyMeter;
    }

    public EFieldDetector getEFieldDetector() {
        return eFieldDetector;
    }

    public Voltmeter getVoltmeter() {
        return voltmeter;
    }
}
