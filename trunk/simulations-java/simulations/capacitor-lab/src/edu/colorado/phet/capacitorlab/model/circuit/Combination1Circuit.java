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
 * Model of a circuit with a battery (B), 2 capacitors in series (C1, C2), and one additional in parallel (C3).
 * <p/>
 * <code>
 * |-----|------|
 * |     |      |
 * |     C1     |
 * |     |      |
 * B     |      C3
 * |     |      |
 * |     C2     |
 * |     |      |
 * |-----|------|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Combination1Circuit extends AbstractCircuit {

    private final Capacitor c1, c2, c3; // references to improve code readability
    private final ArrayList<Capacitor> capacitors; // order is significant
    private final ArrayList<Wire> wires; // order is significant

    public Combination1Circuit( final CircuitConfig config ) {
        super( CLStrings.COMBINATION_1, config.clock, config.mvt, config.batteryLocation );

        // capacitors
        {
            // Series
            double x = getBattery().getX() + config.capacitorXSpacing;
            double y = getBattery().getY() - ( 0.5 * config.capacitorYSpacing );
            final double z = getBattery().getZ();
            c1 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );
            y += config.capacitorYSpacing;
            c2 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );

            // Parallel
            x += config.capacitorXSpacing;
            c3 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );

            capacitors = new ArrayList<Capacitor>() {{
                add( c1 );
                add( c2 );
                add( c3 );
            }};
        }

        // wires
        wires = new ArrayList<Wire>() {{
            add( new WireBatteryTopToCapacitorTops( config.mvt, CLConstants.WIRE_THICKNESS, config.wireExtent, getBattery(), c1, c3 ) );
            add( new WireCapacitorBottomToCapacitorTops( config.mvt, CLConstants.WIRE_THICKNESS, c1, c2 ) );
            add( new WireBatteryBottomToCapacitorBottoms( config.mvt, CLConstants.WIRE_THICKNESS, config.wireExtent, getBattery(), c2, c3 ) );
        }};

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
        // series
        final double seriesCapacitance = c1.getTotalCapacitance() + c2.getTotalCapacitance();
        c1.setPlatesVoltage( getTotalVoltage() * c1.getTotalCapacitance() / seriesCapacitance );
        c2.setPlatesVoltage( getTotalVoltage() * c2.getTotalCapacitance() / seriesCapacitance );
        // parallel
        c3.setPlatesVoltage( getTotalVoltage() );
    }

    public ArrayList<Capacitor> getCapacitors() {
        return new ArrayList<Capacitor>( capacitors );
    }

    public ArrayList<Wire> getWires() {
        return new ArrayList<Wire>( wires );
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

    // C_total = ( 1 / ( 1/C1 + 1/C2 ) ) + C3
    public double getTotalCapacitance() {
        double C1 = c1.getTotalCapacitance();
        double C2 = c2.getTotalCapacitance();
        double C3 = c3.getTotalCapacitance();
        return ( 1 / ( 1 / C1 + 1 / C2 ) ) + C3;
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
        return getBattery().intersectsTopTerminal( shape ) || getTopWire().intersects( shape ) || c1.intersectsTopPlateShape( shape ) || c3.intersectsTopPlateShape( shape );
    }

    // True if shape is touching part of the circuit that is connected to the battery's bottom terminal.
    private boolean connectedToBatteryBottom( Shape shape ) {
        return getBattery().intersectsBottomTerminal( shape ) || getBottomWire().intersects( shape ) || c2.intersectsBottomPlateShape( shape ) || c3.intersectsBottomPlateShape( shape );
    }

    // True if shape is touching part of the circuit that is connected to C2's top plate.
    private boolean connectedToC2TopPlate( Shape shape ) {
        return c1.intersectsBottomPlateShape( shape ) || c2.intersectsTopPlateShape( shape ) || getMiddleWire().intersects( shape );
    }

    public void reset() {
        super.reset();
        for ( Capacitor capacitor : capacitors ) {
            capacitor.reset();
        }
    }
}
