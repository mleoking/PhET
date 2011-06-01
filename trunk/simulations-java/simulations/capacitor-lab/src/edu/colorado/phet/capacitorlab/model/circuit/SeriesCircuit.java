// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.CircuitConfig;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryBottomToCapacitorBottoms;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryTopToCapacitorTops;
import edu.colorado.phet.capacitorlab.model.wire.WireCapacitorBottomToCapacitorTops;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery (B) and N capacitors (Ci) in series.
 * <p/>
 * <code>
 * |-----|
 * |     |
 * |    C1
 * |     |
 * B    C2
 * |     |
 * |    C3
 * |     |
 * |-----|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SeriesCircuit extends AbstractCircuit {

    private final ArrayList<Capacitor> capacitors; // ordered clockwise from battery's top terminal
    private final ArrayList<Wire> wires; // ordered clockwise from battery's top terminal

    public SeriesCircuit( CircuitConfig config, String displayName, int numberOfCapacitors ) {
        super( displayName, config.clock, config.mvt, config.batteryLocation );

        assert ( numberOfCapacitors > 0 );

        capacitors = createCapacitors( numberOfCapacitors, config );

        wires = createWires( getBattery(), capacitors, config );

        // observe battery
        getBattery().addVoltageObserver( new SimpleObserver() {
            public void update() {
                updatePlateVoltages();
            }
        } );

        // observe capacitors
        CapacitorChangeListener capacitorChangeListener = new CapacitorChangeListener() {
            public void capacitorChanged() {
                updatePlateVoltages();
                fireCircuitChanged();
            }
        };
        for ( Capacitor capacitor : capacitors ) {
            capacitor.addCapacitorChangeListener( capacitorChangeListener );
        }
    }

    // Creates a column of capacitors, to the right of the battery, vertically centered on the battery.
    private ArrayList<Capacitor> createCapacitors( int numberOfCapacitors, CircuitConfig config ) {

        final double x = config.batteryLocation.getX() + config.capacitorXSpacing;
        double y = 0;
        final double z = config.batteryLocation.getZ();

        double half = numberOfCapacitors / 2;
        if ( half * 2 != numberOfCapacitors ) {
            // we have an odd number of capacitors, align the middle one with the battery
            y = config.batteryLocation.getY() - ( half * config.capacitorYSpacing );
        }
        else {
            // we have an even number of capacitors
            y = config.batteryLocation.getY() - ( half * config.capacitorYSpacing ) + ( 0.5 * config.capacitorYSpacing );
        }

        ArrayList<Capacitor> capacitors = new ArrayList<Capacitor>();
        for ( int i = 0; i < numberOfCapacitors; i++ ) {
            Point3D location = new Point3D.Double( x, y, z );
            Capacitor capacitor = new Capacitor( location, config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );
            capacitors.add( capacitor );
            y += config.capacitorYSpacing;
        }
        return capacitors;
    }

    // Creates the wires, starting at the battery's top terminal and working clockwise.
    private ArrayList<Wire> createWires( Battery battery, ArrayList<Capacitor> capacitors, CircuitConfig config ) {
        ArrayList<Wire> wires = new ArrayList<Wire>();
        wires.add( new WireBatteryTopToCapacitorTops( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors.get( 0 ) ) );
        for ( int i = 0; i < capacitors.size() - 1; i++ ) {
            wires.add( new WireCapacitorBottomToCapacitorTops( config.mvt, config.wireThickness, capacitors.get( i ), capacitors.get( i + 1 ) ) );
        }
        wires.add( new WireBatteryBottomToCapacitorBottoms( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors.get( capacitors.size() - 1 ) ) );
        assert ( wires.size() == capacitors.size() + 1 );
        return wires;
    }

    private void updatePlateVoltages() {
        double Q_total = getTotalCharge();
        for ( Capacitor capacitor : getCapacitors() ) {
            double Ci = capacitor.getTotalCapacitance();
            double Vi = Q_total / Ci;
            capacitor.setPlatesVoltage( Vi );
        }
    }

    public ArrayList<Capacitor> getCapacitors() {
        return capacitors;
    }

    public ArrayList<Wire> getWires() {
        return wires;
    }

    // C_total = 1 / ( 1/C1 + 1/C2 + ... + 1/Cn)
    public double getTotalCapacitance() {
        double sum = 0;
        for ( Capacitor capacitor : getCapacitors() ) {
            assert ( capacitor.getTotalCapacitance() > 0 );
            sum += 1 / capacitor.getTotalCapacitance();
        }
        assert ( sum > 0 );
        return 1 / sum;
    }

    public double getVoltageAt( Shape shape ) {

        double voltage = Double.NaN;

        // battery
        if ( getBattery().intersectsTopTerminal( shape ) ) {
            voltage = getTotalVoltage();
        }
        else if ( getBattery().intersectsBottomTerminal( shape ) ) {
            voltage = 0;
        }
        else {
            // plates & wires
            ArrayList<Capacitor> capacitors = getCapacitors();
            ArrayList<Wire> wires = getWires();
            for ( int i = 0; i < capacitors.size(); i++ ) {
                Capacitor capacitor = capacitors.get( i );
                Wire topWire = wires.get( i );
                Wire bottomWire = wires.get( i + 1 );
                if ( capacitor.intersectsTopPlateShape( shape ) || topWire.intersects( shape ) ) {
                    // intersects top plate or wire, sum voltage of this capacitor and all capacitors below it.
                    voltage = sumPlateVoltages( i );
                }
                else if ( capacitor.intersectsBottomPlateShape( shape ) || bottomWire.intersects( shape ) ) {
                    // intersects bottom plate or wire, sum voltage of all capacitors below this one.
                    voltage = sumPlateVoltages( i + 1 );
                }
            }
        }

        return voltage;
    }

    // Sums the plate voltages for all capacitors between some top plate and ground.
    private double sumPlateVoltages( int topPlateIndex ) {
        double voltage = 0;
        ArrayList<Capacitor> capacitors = getCapacitors();
        for ( int i = topPlateIndex; i < capacitors.size(); i++ ) {
            voltage += capacitors.get( i ).getPlatesVoltage();
        }
        return voltage;
    }

    public void reset() {
        super.reset();
        for ( Capacitor capacitor : getCapacitors() ) {
            capacitor.reset();
        }
    }
}
