// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.CircuitConfig;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryToCapacitors.WireBatteryToCapacitorsBottom;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryToCapacitors.WireBatteryToCapacitorsTop;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Model of a circuit with a battery (B) and N capacitors (C1...Cn) in parallel.
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

    public ParallelCircuit( CircuitConfig config, String displayName, int numberOfCapacitors ) {
        super( config, displayName, numberOfCapacitors,
               new CreateCapacitors() {
                   // Creates a row of capacitors, as shown in the javadoc diagram.
                   public ArrayList<Capacitor> apply( CircuitConfig config, Integer numberOfCapacitors ) {
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
               },
               new CreateWires() {
                   // Creates wires, as shown in the javadoc diagram.
                   public ArrayList<Wire> apply( final CircuitConfig config, final Battery battery, final ArrayList<Capacitor> capacitors ) {
                       return new ArrayList<Wire>() {{
                           add( new WireBatteryToCapacitorsTop( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors ) );
                           add( new WireBatteryToCapacitorsBottom( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors ) );
                       }};
                   }
               } );
        updatePlateVoltages();
    }

    // @see AbstractCircuit.updatePlateVoltages
    @Override protected void updatePlateVoltages() {
        for ( Capacitor capacitor : getCapacitors() ) {
            capacitor.setPlatesVoltage( getTotalVoltage() ); // voltage across all capacitors is the same
        }
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
    protected boolean connectedToBatteryTop( Shape shape ) {
        return getBattery().intersectsTopTerminal( shape ) || getTopWire().intersects( shape ) || intersectsSomeTopPlate( shape );
    }

    // True if shape is touching part of the circuit that is connected to the battery's bottom terminal.
    protected boolean connectedToBatteryBottom( Shape shape ) {
        return getBattery().intersectsBottomTerminal( shape ) || getBottomWire().intersects( shape ) || intersectsSomeBottomPlate( shape );
    }

    // True if the shape intersects any capacitor's top plate.
    protected boolean intersectsSomeTopPlate( Shape s ) {
        boolean intersects = false;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.intersectsTopPlate( s ) ) {
                intersects = true;
                break;
            }
        }
        return intersects;
    }

    // True if the shape intersects any capacitor's bottom plate.
    protected boolean intersectsSomeBottomPlate( Shape s ) {
        boolean intersects = false;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.intersectsBottomPlate( s ) ) {
                intersects = true;
                break;
            }
        }
        return intersects;
    }
}
