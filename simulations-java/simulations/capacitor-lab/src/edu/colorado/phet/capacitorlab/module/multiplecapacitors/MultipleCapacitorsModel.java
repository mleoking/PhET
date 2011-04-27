// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;


public class MultipleCapacitorsModel {

    private final ArrayList<ICircuit> circuits; // the set of circuits to choose from

    // directly observable properties
    public final Property<Boolean> plateChargesVisible = new Property<Boolean>( CLConstants.PLATE_CHARGES_VISIBLE );
    public final Property<Boolean> eFieldVisible = new Property<Boolean>( CLConstants.EFIELD_VISIBLE );
    public final Property<ICircuit> currentCircuit;

    private final WorldBounds worldBounds;
    private final CapacitanceMeter capacitanceMeter;
    private final PlateChargeMeter plateChargeMeter;
    private final StoredEnergyMeter storedEnergyMeter;
    private final EFieldDetector eFieldDetector;
    private final Voltmeter voltmeter;

    public MultipleCapacitorsModel( IClock clock, CLModelViewTransform3D mvt ) {

        // create circuits
        circuits = new ArrayList<ICircuit>() {{
            add( new DummyCircuit( "Dummy 1" ) );
            add( new DummyCircuit( "Dummy 2" ) );
            add( new DummyCircuit( "Dummy 3" ) );
        }};

        currentCircuit = new Property<ICircuit>( circuits.get( 0 ) );

        worldBounds = new WorldBounds();

        capacitanceMeter = new CapacitanceMeter( currentCircuit.getValue(), worldBounds, CLConstants.CAPACITANCE_METER_LOCATION, CLConstants.CAPACITANCE_METER_VISIBLE );
        plateChargeMeter = new PlateChargeMeter( currentCircuit.getValue(), worldBounds, CLConstants.PLATE_CHARGE_METER_LOCATION, CLConstants.PLATE_CHARGE_METER_VISIBLE );
        storedEnergyMeter = new StoredEnergyMeter( currentCircuit.getValue(), worldBounds, CLConstants.STORED_ENERGY_METER_LOCATION, CLConstants.STORED_ENERGY_METER_VISIBLE );

        eFieldDetector = new EFieldDetector( currentCircuit.getValue(), worldBounds, CLConstants.EFIELD_DETECTOR_BODY_LOCATION, CLConstants.EFIELD_DETECTOR_PROBE_LOCATION,
                                             CLConstants.EFIELD_DETECTOR_VISIBLE, CLConstants.EFIELD_PLATE_VECTOR_VISIBLE, CLConstants.EFIELD_DIELECTRIC_VECTOR_VISIBLE,
                                             CLConstants.EFIELD_SUM_VECTOR_VISIBLE, CLConstants.EFIELD_VALUES_VISIBLE );

        voltmeter = new Voltmeter( currentCircuit.getValue(), worldBounds, mvt,
                                   CLConstants.VOLTMETER_BODY_LOCATION, CLConstants.VOLTMETER_POSITIVE_PROBE_LOCATION, CLConstants.VOLTMETER_NEGATIVE_PROBE_LOCATION,
                                   CLConstants.VOLTMETER_VISIBLE );


        // when the circuit changes...
        currentCircuit.addObserver( new SimpleObserver() {
            public void update() {
                ICircuit circuit = currentCircuit.getValue();
                System.out.println( "MultipleCapacitorsModel$SimpleObserver.update circuit=" + circuit.getDisplayName() );//XXX
                //TODO change circuit based on selection
                capacitanceMeter.setCircuit( circuit );
                plateChargeMeter.setCircuit( circuit );
                storedEnergyMeter.setCircuit( circuit );
                eFieldDetector.setCircuit( circuit );
                voltmeter.setCircuit( circuit );
            }
        } );
    }

    public void reset() {
        plateChargesVisible.reset();
        eFieldVisible.reset();
        currentCircuit.reset();
        capacitanceMeter.reset();
        plateChargeMeter.reset();
        storedEnergyMeter.reset();
        eFieldDetector.reset();
        voltmeter.reset();
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

    /*
     * This circuit does nothing.
     */
    private static class DummyCircuit extends AbstractCircuit {

        public DummyCircuit( String displayName ) {
            super( displayName );
        }

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
