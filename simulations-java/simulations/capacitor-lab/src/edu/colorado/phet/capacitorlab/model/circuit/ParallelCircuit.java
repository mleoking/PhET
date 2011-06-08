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
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery (B) and N capacitors (Ci) in parallel.
 * <p/>
 * <code>
 * |-----|------|------|
 * |     |      |      |
 * B     C1     C2    C3
 * |     |      |      |
 * |-----|------|------|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ParallelCircuit extends AbstractCircuit {

    private final ArrayList<Capacitor> capacitors; // ordered left-to-right from battery
    private final ArrayList<Wire> wires; // ordered clockwise from battery's top terminal

    public ParallelCircuit( CircuitConfig config, String displayName, int numberOfCapacitors ) {
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

    // Creates a row of capacitors, to the right of the battery, vertically centered on the battery.
    private ArrayList<Capacitor> createCapacitors( int numberOfCapacitors, CircuitConfig config ) {

        double x = config.batteryLocation.getX() + config.capacitorXSpacing;
        final double y = config.batteryLocation.getY();
        final double z = config.batteryLocation.getZ();

        ArrayList<Capacitor> capacitors = new ArrayList<Capacitor>();
        for ( int i = 0; i < numberOfCapacitors; i++ ) {
            Point3D location = new Point3D.Double( x, y, z );
            Capacitor capacitor = new Capacitor( location, config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );
            capacitors.add( capacitor );
            x += config.capacitorXSpacing;
        }
        return capacitors;
    }

    // Creates the wires, starting at the battery's top terminal and working clockwise.
    private ArrayList<Wire> createWires( Battery battery, ArrayList<Capacitor> capacitors, CircuitConfig config ) {
        ArrayList<Wire> wires = new ArrayList<Wire>();
        wires.add( new WireBatteryTopToCapacitorTops( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors ) );
        wires.add( new WireBatteryBottomToCapacitorBottoms( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors ) );
        return wires;
    }

    private void updatePlateVoltages() {
        for ( Capacitor capacitor : getCapacitors() ) {
            capacitor.setPlatesVoltage( getTotalVoltage() ); // voltage across all capacitors is the same
        }
    }

    public ArrayList<Capacitor> getCapacitors() {
        return capacitors;
    }

    public ArrayList<Wire> getWires() {
        return wires;
    }

    // Gets the wire connected to the battery's top terminal.
    private Wire getTopWire() {
        return wires.get( 0 );
    }

    // Gets the wire connected to the battery's bottom terminal.
    private Wire getBottomWire() {
        return wires.get( wires.size() - 1 );
    }

    // C_total = C1 + C2 + ... + Cn
    public double getTotalCapacitance() {
        double sum = 0;
        for ( Capacitor capacitor : getCapacitors() ) {
            sum += capacitor.getTotalCapacitance();
        }
        return sum;
    }

    // @see ICircuit.getVoltageAt
    public double getVoltageAt( Shape shape ) {
        double voltage = Double.NaN;
        if ( connectedToBatteryTop( shape ) ) {
            voltage = getTotalVoltage();
        }
        else if ( connectedToBatteryBottom( shape ) ) {
            voltage = 0;
        }
        return voltage;
    }

    // True if shape is touching part of the circuit that is connected to the battery's top terminal.
    private boolean connectedToBatteryTop( Shape shape ) {
        return getBattery().intersectsTopTerminal( shape ) || getTopWire().intersects( shape ) || intersectsSomeTopPlate( shape );
    }

    // True if shape is touching part of the circuit that is connected to the battery's bottom terminal.
    private boolean connectedToBatteryBottom( Shape shape ) {
        return getBattery().intersectsBottomTerminal( shape ) || getBottomWire().intersects( shape ) || intersectsSomeBottomPlate( shape );
    }

    // True if the shape intersects any capacitor's top plate.
    private boolean intersectsSomeTopPlate( Shape s ) {
        boolean intersects = false;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.intersectsTopPlateShape( s ) ) {
                intersects = true;
                break;
            }
        }
        return intersects;
    }

    // True if the shape intersects any capacitor's bottom plate.
    private boolean intersectsSomeBottomPlate( Shape s ) {
        boolean intersects = false;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.intersectsBottomPlateShape( s ) ) {
                intersects = true;
                break;
            }
        }
        return intersects;
    }

    public void reset() {
        super.reset();
        for ( Capacitor capacitor : capacitors ) {
            capacitor.reset();
        }
    }
}
