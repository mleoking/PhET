// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
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
 * Model of a circuit with a battery (B), 2 capacitors in parallel (C2, C3), and one additional in series (C1).
 * <p/>
 * <code>
 * |-----|
 * |     |
 * |    C1
 * |     |
 * B     |------|
 * |     |      |
 * |     C2    C3
 * |     |      |
 * |-----|------|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Combination2Circuit extends AbstractCircuit {

    private final Capacitor c1, c2, c3; // references to improve code readability
    private final ArrayList<Capacitor> capacitors; // order is significant
    private final ArrayList<Wire> wires; // order is significant

    public Combination2Circuit( CircuitConfig config ) {
        super( CLStrings.COMBINATION_2, config.clock, config.mvt, config.batteryLocation );

        // capacitors
        {
            // Series
            double x = getBattery().getX() + config.capacitorXSpacing;
            double y = getBattery().getY() - ( 0.5 * config.capacitorYSpacing );
            final double z = getBattery().getZ();
            c1 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );

            // Parallel
            y += config.capacitorYSpacing;
            c2 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );
            x += config.capacitorXSpacing;
            c3 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );

            capacitors = new ArrayList<Capacitor>();
            capacitors.add( c1 );
            capacitors.add( c2 );
            capacitors.add( c3 );
        }

        // wires
        {
            wires = new ArrayList<Wire>();
            wires.add( new WireBatteryTopToCapacitorTops( config.mvt, CLConstants.WIRE_THICKNESS, config.wireExtent, getBattery(), c1 ) );
            wires.add( new WireCapacitorBottomToCapacitorTops( config.mvt, CLConstants.WIRE_THICKNESS, c1, c2, c3 ) );
            wires.add( new WireBatteryBottomToCapacitorBottoms( config.mvt, CLConstants.WIRE_THICKNESS, config.wireExtent, getBattery(), c2, c3 ) );
        }

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

    private void updatePlateVoltages() {
        double Q_total = getTotalCharge();
        // series
        c1.setPlatesVoltage( Q_total / c1.getTotalCapacitance() );
        // parallel
        double V_parallel = Q_total / ( c2.getTotalCapacitance() + c3.getTotalCapacitance() );
        c2.setPlatesVoltage( V_parallel );
        c3.setPlatesVoltage( V_parallel );
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

    // Gets the wire between the 2 capacitors.
    private Wire getMiddleWire() {
        return wires.get( 1 );
    }

    // C_total = ( 1 / ( 1/C1 + 1/(C2 + C3) )
    public double getTotalCapacitance() {
        double C1 = c1.getTotalCapacitance();
        double C2 = c2.getTotalCapacitance();
        double C3 = c3.getTotalCapacitance();
        return ( 1 / ( 1 / C1 + 1 / ( C2 + C3 ) ) );
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
        else if ( connectedToC2TopPlate( shape ) ) {
            voltage = c2.getPlatesVoltage();
        }
        return voltage;
    }

    // True if shape is touching part of the circuit that is connected to the battery's top terminal.
    private boolean connectedToBatteryTop( Shape shape ) {
        return getBattery().intersectsTopTerminal( shape ) || getTopWire().intersects( shape ) || c1.intersectsTopPlateShape( shape );
    }

    // True if shape is touching part of the circuit that is connected to the battery's bottom terminal.
    private boolean connectedToBatteryBottom( Shape shape ) {
        return getBattery().intersectsBottomTerminal( shape ) || getBottomWire().intersects( shape ) || c2.intersectsBottomPlateShape( shape ) || c3.intersectsBottomPlateShape( shape );
    }

    // True if shape is touching part of the circuit that is connected to C2's top plate.
    private boolean connectedToC2TopPlate( Shape shape ) {
        return c1.intersectsBottomPlateShape( shape ) || getMiddleWire().intersects( shape ) || c2.intersectsTopPlateShape( shape ) || c3.intersectsTopPlateShape( shape );
    }

    public void reset() {
        super.reset();
        for ( Capacitor capacitor : capacitors ) {
            capacitor.reset();
        }
    }
}
