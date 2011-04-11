// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.awt.*;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.view.DielectricNode;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;


public class MultipleCapacitorsModel {

    // preconfigured circuit choices
    public static enum CircuitChoices {
        SINGLE,
        TWO_IN_SERIES,
        THREE_IN_SERIES,
        TWO_IN_PARALLEL,
        THREE_IN_PARALLEL,
        SERIES_PARALLEL,
        PARALLEL_SERIES
    }

    // directly observable properties
    public final Property<Boolean> plateChargesVisible = new Property<Boolean>( CLConstants.PLATE_CHARGES_VISIBLE );
    public final Property<Boolean> eFieldVisible = new Property<Boolean>( CLConstants.EFIELD_VISIBLE );
    public final Property<DielectricNode.DielectricChargeView> property = new Property<DielectricNode.DielectricChargeView>( CLConstants.DIELECTRIC_CHARGE_VIEW );

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
    }

    public void reset() {
        plateChargesVisible.reset();
        eFieldVisible.reset();
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
