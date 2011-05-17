// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.BarMeter.CapacitanceMeter;
import edu.colorado.phet.capacitorlab.model.BarMeter.PlateChargeMeter;
import edu.colorado.phet.capacitorlab.model.BarMeter.StoredEnergyMeter;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Air;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;


public class MultipleCapacitorsModel {

    //TODO decide whether these should live here
    public static final DoubleRange CAPACITANCE_RANGE = new DoubleRange( 1E-13, 4E-13 ); // Farads
    public static final int CAPACITANCE_DISPLAY_EXPONENT = -13;
    public static final double CAPACITOR_PLATE_WIDTH = 0.01; // meters
    public static final DielectricMaterial DIELECTRIC_MATERIAL = new Air();
    public static final double DIELECTRIC_OFFSET = 0;
    public static final double CAPACITOR_PLATE_SEPARATION = Capacitor.getPlateSeparation( DIELECTRIC_MATERIAL.getDielectricConstant(), CAPACITOR_PLATE_WIDTH, CAPACITANCE_RANGE.getMin() );

    private final ArrayList<ICircuit> circuits; // the set of circuits to choose from

    // directly observable properties
    public final Property<ICircuit> currentCircuit;

    private final WorldBounds worldBounds;
    private final CapacitanceMeter capacitanceMeter;
    private final PlateChargeMeter plateChargeMeter;
    private final StoredEnergyMeter storedEnergyMeter;
    private final EFieldDetector eFieldDetector;
    private final Voltmeter voltmeter;

    public MultipleCapacitorsModel( final IClock clock, final CLModelViewTransform3D mvt ) {

        // create circuits
        circuits = new ArrayList<ICircuit>() {{
            add( new BatteryCapacitorCircuit( clock, mvt, CAPACITOR_PLATE_WIDTH, CAPACITOR_PLATE_SEPARATION, DIELECTRIC_MATERIAL, DIELECTRIC_OFFSET ) );
            add( new DummyCircuit( CLStrings.TWO_IN_SERIES ) );
            add( new DummyCircuit( CLStrings.THREE_IN_SERIES ) );
            add( new DummyCircuit( CLStrings.TWO_IN_PARALLEL ) );
            add( new DummyCircuit( CLStrings.THREE_IN_PARALLEL ) );
            add( new DummyCircuit( CLStrings.COMBINATION_1 ) );
            add( new DummyCircuit( CLStrings.COMBINATION_2 ) );
        }};

        currentCircuit = new Property<ICircuit>( circuits.get( 0 ) );

        worldBounds = new WorldBounds();

        capacitanceMeter = new CapacitanceMeter( currentCircuit.get(), worldBounds, CLConstants.CAPACITANCE_METER_LOCATION, CLConstants.CAPACITANCE_METER_VISIBLE );
        plateChargeMeter = new PlateChargeMeter( currentCircuit.get(), worldBounds, CLConstants.PLATE_CHARGE_METER_LOCATION, CLConstants.PLATE_CHARGE_METER_VISIBLE );
        storedEnergyMeter = new StoredEnergyMeter( currentCircuit.get(), worldBounds, CLConstants.STORED_ENERGY_METER_LOCATION, CLConstants.STORED_ENERGY_METER_VISIBLE );

        eFieldDetector = new EFieldDetector( currentCircuit.get(), worldBounds, CLConstants.EFIELD_DETECTOR_BODY_LOCATION, CLConstants.EFIELD_DETECTOR_PROBE_LOCATION,
                                             CLConstants.EFIELD_DETECTOR_VISIBLE, CLConstants.EFIELD_PLATE_VECTOR_VISIBLE, CLConstants.EFIELD_DIELECTRIC_VECTOR_VISIBLE,
                                             CLConstants.EFIELD_SUM_VECTOR_VISIBLE, CLConstants.EFIELD_VALUES_VISIBLE );

        voltmeter = new Voltmeter( currentCircuit.get(), worldBounds, mvt,
                                   CLConstants.VOLTMETER_BODY_LOCATION, CLConstants.VOLTMETER_POSITIVE_PROBE_LOCATION, CLConstants.VOLTMETER_NEGATIVE_PROBE_LOCATION,
                                   CLConstants.VOLTMETER_VISIBLE );


        // when the circuit changes...
        currentCircuit.addObserver( new SimpleObserver() {
            public void update() {
                ICircuit circuit = currentCircuit.get();
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
        capacitanceMeter.reset();
        plateChargeMeter.reset();
        storedEnergyMeter.reset();
        eFieldDetector.reset();
        voltmeter.reset();
        for ( ICircuit circuit : circuits ) {
            circuit.reset();
        }
        currentCircuit.reset();
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

        public Battery getBattery() {
            return null;
        }

        public Capacitor getCapacitor() {
            return null;
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

        public void reset() {
            // do nothing
        }
    }
}
