// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryBottomToCapacitorBottoms;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryTopToCapacitorTops;
import edu.colorado.phet.capacitorlab.model.wire.WireCapacitorBottomToCapacitorTops;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery (B), 2 capacitors in series (c1, c2), and one additional in parallel (c3).
 * <p/>
 * <code>
 * |-----|------|
 * |     |      |
 * |     c1     |
 * |     |      |
 * B     |      c3
 * |     |      |
 * |     c2     |
 * |     |      |
 * |-----|------|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Combination1Circuit extends AbstractCircuit {

    private static final double X_SPACING = MultipleCapacitorsModel.CAPACITOR_X_SPACING;
    private static final double Y_SPACING = MultipleCapacitorsModel.CAPACITOR_Y_SPACING;

    private final ArrayList<Capacitor> capacitors;
    private final Capacitor c1, c2, c3;
    private final ArrayList<Wire> wires;

    public Combination1Circuit( IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation,
                                double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        super( CLStrings.COMBINATION_1, clock, mvt, batteryLocation );

        // capacitors
        {
            // Series
            double x = batteryLocation.getX() + X_SPACING;
            double y = batteryLocation.getY() - ( 0.5 * Y_SPACING );
            final double z = batteryLocation.getZ();
            c1 = new Capacitor( new Point3D.Double( x, y, z ), plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );
            y += Y_SPACING;
            c2 = new Capacitor( new Point3D.Double( x, y, z ), plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );

            // Parallel
            x += X_SPACING;
            c3 = new Capacitor( new Point3D.Double( x, y, z ), plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );

            capacitors = new ArrayList<Capacitor>();
            capacitors.add( c1 );
            capacitors.add( c2 );
            capacitors.add( c3 );
        }

        // wires
        {
            wires = new ArrayList<Wire>();
            wires.add( new WireBatteryTopToCapacitorTops( mvt, CLConstants.WIRE_THICKNESS, getBattery(), c1, c3 ) );
            wires.add( new WireCapacitorBottomToCapacitorTops( mvt, CLConstants.WIRE_THICKNESS, c1, c2 ) );
            wires.add( new WireBatteryBottomToCapacitorBottoms( mvt, CLConstants.WIRE_THICKNESS, getBattery(), c2, c3 ) );
        }

        // observe battery
        getBattery().addVoltageObserver( new SimpleObserver() {
            public void update() {
                updatePlateVoltages();
            }
        } );

        // observe capacitor
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
        c1.setPlatesVoltage( getTotalVoltage() * c1.getTotalCapacitance() / ( c1.getTotalCapacitance() + c2.getTotalCapacitance() ) );
        c2.setPlatesVoltage( getTotalVoltage() * c2.getTotalCapacitance() / ( c1.getTotalCapacitance() + c2.getTotalCapacitance() ) );
        // parallel
        c3.setPlatesVoltage( getTotalVoltage() );
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

    // C_total = ( 1 / ( 1/C1 + 1/C2 ) ) + C3
    public double getTotalCapacitance() {
        double C1 = c1.getTotalCapacitance();
        double C2 = c2.getTotalCapacitance();
        double C3 = c3.getTotalCapacitance();
        return ( 1 / ( 1 / C1 + 1 / C2 ) ) + C3;
    }

    public double getVoltageAt( Shape shape ) {
        double voltage = Double.NaN;
        if ( getBattery().intersectsTopTerminal( shape ) || c1.intersectsTopPlateShape( shape ) || c3.intersectsTopPlateShape( shape ) || getTopWire().intersects( shape ) ) {
            voltage = getTotalVoltage();
        }
        else if ( getBattery().intersectsBottomTerminal( shape ) || c2.intersectsBottomPlateShape( shape ) || c3.intersectsBottomPlateShape( shape ) || getBottomWire().intersects( shape ) ) {
            voltage = 0;
        }
        else if ( c1.intersectsBottomPlateShape( shape ) || c2.intersectsTopPlateShape( shape ) || getMiddleWire().intersects( shape ) ) {
            voltage = c2.getPlatesVoltage();
        }
        return voltage;
    }

    public void reset() {
        super.reset();
        for ( Capacitor capacitor : capacitors ) {
            capacitor.reset();
        }
    }
}
