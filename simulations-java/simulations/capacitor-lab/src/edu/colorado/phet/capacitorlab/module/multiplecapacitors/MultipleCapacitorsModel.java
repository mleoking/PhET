// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.awt.*;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;


public class MultipleCapacitorsModel {

    // directly observable properties
    public final Property<Boolean> plateChargesVisible = new Property<Boolean>( CLConstants.PLATE_CHARGES_VISIBLE );
    public final Property<Boolean> eFieldVisible = new Property<Boolean>( CLConstants.EFIELD_VISIBLE );
    public final Property<PreconfiguredCircuitChoices> circuitChoice = new Property<PreconfiguredCircuitChoices>( PreconfiguredCircuitChoices.SINGLE );

    private final World world;
    private final CapacitanceMeter capacitanceMeter;
    private final PlateChargeMeter plateChargeMeter;
    private final StoredEnergyMeter storedEnergyMeter;
    private final EFieldDetector eFieldDetector;
    private final Voltmeter voltmeter;

    private ICircuit circuit;

    public MultipleCapacitorsModel( CLClock clock, CLModelViewTransform3D mvt ) {

        world = new World();

        circuit = new DummyCircuit();

        capacitanceMeter = new CapacitanceMeter( circuit, world, CLConstants.CAPACITANCE_METER_LOCATION, CLConstants.CAPACITANCE_METER_VISIBLE );
        plateChargeMeter = new PlateChargeMeter( circuit, world, CLConstants.PLATE_CHARGE_METER_LOCATION, CLConstants.PLATE_CHARGE_METER_VISIBLE );
        storedEnergyMeter = new StoredEnergyMeter( circuit, world, CLConstants.STORED_ENERGY_METER_LOCATION, CLConstants.STORED_ENERGY_METER_VISIBLE );

        eFieldDetector = new EFieldDetector( circuit, world, CLConstants.EFIELD_DETECTOR_BODY_LOCATION, CLConstants.EFIELD_DETECTOR_PROBE_LOCATION,
                                             CLConstants.EFIELD_DETECTOR_VISIBLE, CLConstants.EFIELD_PLATE_VECTOR_VISIBLE, CLConstants.EFIELD_DIELECTRIC_VECTOR_VISIBLE,
                                             CLConstants.EFIELD_SUM_VECTOR_VISIBLE, CLConstants.EFIELD_VALUES_VISIBLE );

        voltmeter = new Voltmeter( circuit, world, mvt,
                                   CLConstants.VOLTMETER_BODY_LOCATION, CLConstants.VOLTMETER_POSITIVE_PROBE_LOCATION, CLConstants.VOLTMETER_NEGATIVE_PROBE_LOCATION,
                                   CLConstants.VOLTMETER_VISIBLE );

        circuitChoice.addObserver( new SimpleObserver() {
            public void update() {
                System.out.println( "circuitChoice=" + circuitChoice.getValue() );//XXX
                //TODO change circuit based on selection
            }
        } );
    }

    public void reset() {
        plateChargesVisible.reset();
        eFieldVisible.reset();
        circuitChoice.reset();
        capacitanceMeter.reset();
        plateChargeMeter.reset();
        storedEnergyMeter.reset();
        eFieldDetector.reset();
        voltmeter.reset();
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

    /*
     * This circuit does nothing.
     */
    private static class DummyCircuit implements ICircuit {

        public double getTotalCapacitance() {
            return 0;
        }

        public double getTotalCharge() {
            return 0;
        }

        public double getStoredEnergy() {
            return 0;
        }

        public double getVoltageBetween( Shape positiveShape, Shape negativeShape ) {
            return 0;
        }

        public double getEffectiveEFieldAt( Point3D location ) {
            return 0;
        }

        public double getPlatesDielectricEFieldAt( Point3D location ) {
            return 0;
        }

        public double getDielectricEFieldAt( Point3D location ) {
            return 0;
        }

        public void addCircuitChangeListener( CircuitChangeListener listener ) {
            // do nothing
        }

        public void removeCircuitChangeListener( CircuitChangeListener listener ) {
            // do nothing
        }
    }
}
