// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryBottomToCapacitorBottoms;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryTopToCapacitorTops;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery (B) and N capacitors (ci) in parallel.
 * <p/>
 * <code>
 * |-----|------|------|
 * |     |      |      |
 * B     c1     c2    c3
 * |     |      |      |
 * |-----|------|------|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ParallelCircuit extends AbstractCircuit {

    private static final double X_SPACING = MultipleCapacitorsModel.CAPACITOR_X_SPACING;

    private final ArrayList<Capacitor> capacitors;
    private final ArrayList<Wire> wires;

    public ParallelCircuit( String displayName, IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation, int numberOfCapacitors,
                            double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        super( displayName, clock, mvt, batteryLocation );

        assert ( numberOfCapacitors > 0 );

        capacitors = createCapacitors( mvt, batteryLocation, numberOfCapacitors,
                                       plateWidth, plateSeparation, dielectricMaterial, dielectricOffset );

        wires = createWires( mvt, CLConstants.WIRE_THICKNESS, getBattery(), capacitors );

        // observe battery
        getBattery().addVoltageObserver( new SimpleObserver() {
            public void update() {
                updateVoltages();
            }
        } );

        // observe capacitor
        CapacitorChangeListener capacitorChangeListener = new CapacitorChangeListener() {
            public void capacitorChanged() {
                updateVoltages();
                fireCircuitChanged();
            }
        };
        for ( Capacitor capacitor : capacitors ) {
            capacitor.addCapacitorChangeListener( capacitorChangeListener );
        }
    }

    // Creates a row of capacitors, to the right of the battery, vertically centered on the battery.
    private ArrayList<Capacitor> createCapacitors( CLModelViewTransform3D mvt, Point3D batteryLocation, int numberOfCapacitors,
                                                   double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {

        double x = batteryLocation.getX() + X_SPACING;
        final double y = batteryLocation.getY();
        final double z = batteryLocation.getZ();

        ArrayList<Capacitor> capacitors = new ArrayList<Capacitor>();
        for ( int i = 0; i < numberOfCapacitors; i++ ) {
            Point3D location = new Point3D.Double( x, y, z );
            Capacitor capacitor = new Capacitor( location, plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );
            capacitors.add( capacitor );
            x += X_SPACING;
        }
        return capacitors;
    }

    // Creates the wires, starting at the battery's top terminal and working clockwise.
    private ArrayList<Wire> createWires( CLModelViewTransform3D mvt, double thickness, Battery battery, ArrayList<Capacitor> capacitors ) {
        ArrayList<Wire> wires = new ArrayList<Wire>();
        wires.add( new WireBatteryTopToCapacitorTops( mvt, thickness, battery, capacitors ) );
        wires.add( new WireBatteryBottomToCapacitorBottoms( mvt, thickness, battery, capacitors ) );
        return wires;
    }

    private void updateVoltages() {
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

    public double getVoltageAt( Shape shape ) {
        double voltage = Double.NaN;
        if ( getBattery().intersectsTopTerminal( shape ) || intersectsSomeTopPlate( shape ) || getTopWire().intersects( shape ) ) {
            voltage = getTotalVoltage();
        }
        else if ( getBattery().intersectsBottomTerminal( shape ) || intersectsSomeBottomPlate( shape ) || getBottomWire().intersects( shape ) ) {
            voltage = 0;
        }
        return voltage;
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
