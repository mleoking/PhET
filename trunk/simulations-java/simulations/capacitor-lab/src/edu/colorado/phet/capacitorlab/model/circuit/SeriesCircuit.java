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
import edu.colorado.phet.capacitorlab.model.wire.WireCapacitorToCapacitors;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Model of a circuit with a battery (B) and N capacitors (C1...Cn) in series.
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

    public SeriesCircuit( CircuitConfig config, String displayName, int numberOfCapacitors ) {
        super( config, displayName, numberOfCapacitors,
               new CreateCapacitors() {
                   // Creates a column of capacitors, as shown in the javadoc diagram.
                   public ArrayList<Capacitor> apply( CircuitConfig config, Integer numberOfCapacitors ) {
                       // location of first capacitor
                       final double x = config.batteryLocation.getX() + config.capacitorXSpacing;
                       double y = config.batteryLocation.getY() - ( ( numberOfCapacitors / 2 ) * config.capacitorYSpacing );
                       if ( numberOfCapacitors % 2 == 0 ) {
                           // we have an even number of capacitors, shift up
                           y += ( 0.5 * config.capacitorYSpacing );
                       }
                       final double z = config.batteryLocation.getZ();

                       ArrayList<Capacitor> capacitors = new ArrayList<Capacitor>();
                       for ( int i = 0; i < numberOfCapacitors; i++ ) {
                           Point3D location = new Point3D.Double( x, y, z );
                           Capacitor capacitor = new Capacitor( location, config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );
                           capacitors.add( capacitor );
                           y += config.capacitorYSpacing;
                       }
                       return capacitors;
                   }
               },
               new CreateWires() {
                   // Creates wires, as shown in the javadoc diagram.
                   public ArrayList<Wire> apply( CircuitConfig config, Battery battery, ArrayList<Capacitor> capacitors ) {
                       ArrayList<Wire> wires = new ArrayList<Wire>();
                       wires.add( new WireBatteryToCapacitorsTop( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors.get( 0 ) ) );
                       for ( int i = 0; i < capacitors.size() - 1; i++ ) {
                           wires.add( new WireCapacitorToCapacitors( config.mvt, config.wireThickness, capacitors.get( i ), capacitors.get( i + 1 ) ) );
                       }
                       wires.add( new WireBatteryToCapacitorsBottom( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors.get( capacitors.size() - 1 ) ) );
                       assert ( wires.size() == capacitors.size() + 1 );
                       return wires;
                   }
               } );
        updatePlateVoltages();
    }

    // @see AbstractCircuit.updatePlateVoltages
    @Override protected void updatePlateVoltages() {
        double Q_total = getTotalCharge();
        for ( Capacitor capacitor : getCapacitors() ) {
            double Ci = capacitor.getTotalCapacitance();
            double Vi = Q_total / Ci;
            capacitor.setPlatesVoltage( Vi );
        }
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

    // @see ICircuit.getVoltageAt
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
                if ( capacitor.intersectsTopPlate( shape ) || topWire.intersects( shape ) ) {
                    // intersects top plate or wire, sum voltage of this capacitor and all capacitors below it.
                    voltage = sumPlateVoltages( i );
                }
                else if ( capacitor.intersectsBottomPlate( shape ) || bottomWire.intersects( shape ) ) {
                    // intersects bottom plate or wire, sum voltage of all capacitors below this one.
                    voltage = sumPlateVoltages( i + 1 );
                }
            }
        }

        return voltage;
    }

    /*
     * Sums the plate voltages for all capacitors between some top plate and ground.
     * Assumes that capacitors are ordered as shown in the javadoc diagram.
     */
    private double sumPlateVoltages( int topPlateIndex ) {
        double voltage = 0;
        ArrayList<Capacitor> capacitors = getCapacitors();
        for ( int i = topPlateIndex; i < capacitors.size(); i++ ) {
            voltage += capacitors.get( i ).getPlatesVoltage();
        }
        return voltage;
    }
}
