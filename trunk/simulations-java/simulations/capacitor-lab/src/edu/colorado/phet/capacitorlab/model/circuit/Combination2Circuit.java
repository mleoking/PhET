// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.Shape;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.CircuitConfig;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryToCapacitors.WireBatteryToCapacitorsBottom;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryToCapacitors.WireBatteryToCapacitorsTop;
import edu.colorado.phet.capacitorlab.model.wire.WireCapacitorToCapacitors;
import edu.colorado.phet.common.phetcommon.math.Point3D;

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
 * </p>
 * At first glance, this class appears to duplicate code from Combination1Circuit. But there are subtle
 * differences, and the commonalities are only due to coincidences in the how the components are numbered
 * in the circuit diagram.  It would be a mistake to extract common functionality based on these factors,
 * since one or the other circuit could change in the future.  Unlike the other circuits, these 2 circuit
 * are very specific, one-of-a-kind, and there's really no meaningful base class that safely describes them both.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Combination2Circuit extends AbstractCircuit {

    private final Capacitor c1, c2, c3; // references for correlation with javadoc diagram, to improve code readability

    public Combination2Circuit( final CircuitConfig config ) {
        super( config, CLStrings.COMBINATION_2, 3 /* numberOfCapacitors */,
               new CreateCapacitors() {
                   // Creates capacitors, as shown in javadoc diagram.
                   public ArrayList<Capacitor> apply( CircuitConfig config, Integer numberOfCapacitors ) {
                       // Series
                       double x = config.batteryLocation.getX() + config.capacitorXSpacing;
                       double y = config.batteryLocation.getY() - ( 0.5 * config.capacitorYSpacing );
                       final double z = config.batteryLocation.getZ();
                       final Capacitor c1 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );

                       // Parallel
                       y += config.capacitorYSpacing;
                       final Capacitor c2 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );
                       x += config.capacitorXSpacing;
                       final Capacitor c3 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );

                       return new ArrayList<Capacitor>() {{
                           add( c1 );
                           add( c2 );
                           add( c3 );
                       }};
                   }
               },
               new CreateWires() {
                   // Creates wires, as shown in javadoc diagram
                   public ArrayList<Wire> apply( final CircuitConfig config, final Battery battery, final ArrayList<Capacitor> capacitors ) {
                       final Capacitor c1 = capacitors.get( 0 );
                       final Capacitor c2 = capacitors.get( 1 );
                       final Capacitor c3 = capacitors.get( 2 );
                       return new ArrayList<Wire>() {{
                           add( new WireBatteryToCapacitorsTop( config.mvt, CLConstants.WIRE_THICKNESS, config.wireExtent, battery, c1 ) );
                           add( new WireCapacitorToCapacitors( config.mvt, CLConstants.WIRE_THICKNESS, c1, c2, c3 ) );
                           add( new WireBatteryToCapacitorsBottom( config.mvt, CLConstants.WIRE_THICKNESS, config.wireExtent, battery, c2, c3 ) );
                       }};
                   }
               }
        );

        c1 = getCapacitors().get( 0 );
        c2 = getCapacitors().get( 1 );
        c3 = getCapacitors().get( 2 );
        updatePlateVoltages();
    }

    // @see AbstractCircuit.updatePlateVoltages
    @Override protected void updatePlateVoltages() {
        double Q_total = getTotalCharge();
        // series
        c1.setPlatesVoltage( Q_total / c1.getTotalCapacitance() );
        // parallel
        double V_parallel = Q_total / ( c2.getTotalCapacitance() + c3.getTotalCapacitance() );
        c2.setPlatesVoltage( V_parallel );
        c3.setPlatesVoltage( V_parallel );
    }

    // Gets the wire that connects C1 to C2 and C3.
    private Wire getMiddleWire() {
        return getWires().get( 1 );
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
        return getBattery().intersectsTopTerminal( shape ) || getTopWire().intersects( shape ) || c1.intersectsTopPlate( shape );
    }

    // True if shape is touching part of the circuit that is connected to the battery's bottom terminal.
    private boolean connectedToBatteryBottom( Shape shape ) {
        return getBattery().intersectsBottomTerminal( shape ) || getBottomWire().intersects( shape ) || c2.intersectsBottomPlate( shape ) || c3.intersectsBottomPlate( shape );
    }

    // True if shape is touching part of the circuit that is connected to C2's top plate.
    private boolean connectedToC2TopPlate( Shape shape ) {
        return c1.intersectsBottomPlate( shape ) || getMiddleWire().intersects( shape ) || c2.intersectsTopPlate( shape ) || c3.intersectsTopPlate( shape );
    }
}
