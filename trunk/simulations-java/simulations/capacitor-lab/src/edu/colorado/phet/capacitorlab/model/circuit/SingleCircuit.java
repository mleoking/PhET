// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.CircuitConfig;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryBottomToCapacitorBottoms;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryTopToCapacitorTops;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model of a circuit with a battery (B) connected to a single capacitor (C1).
 * </p>
 * <code>
 * |-----|
 * |     |
 * B    C1
 * |     |
 * |-----|
 * </code>
 * </p>
 * Unlike other circuits in this simulation, the battery can be disconnected.
 * When the battery is disconnected, plate charge can be controlled directly.
 * </p>
 * This circuit is used in all 3 modules.  In version 1.00, it was the sole circuit
 * in the simulation. It was heavily refactored to support the Multiple Capacitors
 * module introduced in version 2.00.
 * </p>
 * Variable names used in this implementation where chosen to match the specification
 * in the design document, and therefore violate Java naming conventions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SingleCircuit extends AbstractCircuit {

    private final Capacitor capacitor; // convenience field, since 1.00 only had one capacitor
    private final Property<Boolean> batteryConnectedProperty; // is the battery connected to the circuit?
    private double disconnectedPlateCharge; // charge set manually by the user, used when battery is disconnected

    public SingleCircuit( CircuitConfig config ) {
        this( config, true /* batteryConnected */ );
    }

    public SingleCircuit( final CircuitConfig config, boolean batteryConnected ) {
        super( CLStrings.SINGLE, config, 1 /* numberOfCapacitors */,
               new CreateCapacitors() {
                   // Creates a single capacitor to the right of the battery.
                   public ArrayList<Capacitor> apply( final CircuitConfig config, Integer numberOfCapacitors ) {
                       final double x = config.batteryLocation.getX() + config.capacitorXSpacing;
                       final double y = config.batteryLocation.getY();
                       final double z = config.batteryLocation.getZ();
                       return new ArrayList<Capacitor>() {{
                           add( new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt ) );
                       }};
                   }
               },
               new CreateWires() {
                   // Creates wires, as shown in the javadoc diagram.
                   public ArrayList<Wire> apply( final CircuitConfig config, final Battery battery, final ArrayList<Capacitor> capacitors ) {
                       return new ArrayList<Wire>() {{
                           add( new WireBatteryTopToCapacitorTops( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors ) );
                           add( new WireBatteryBottomToCapacitorBottoms( config.mvt, config.wireThickness, config.wireExtent, battery, capacitors ) );
                       }};
                   }
               } );

        this.capacitor = getCapacitors().get( 0 );
        this.batteryConnectedProperty = new Property<Boolean>( batteryConnected );
        this.disconnectedPlateCharge = getTotalCharge();
        updatePlateVoltages();
    }

    @Override public void reset() {
        super.reset();
        batteryConnectedProperty.reset();
    }

    //----------------------------------------------------------------------------------
    // Circuit components
    //----------------------------------------------------------------------------------

    public Capacitor getCapacitor() {
        return capacitor;
    }

    //----------------------------------------------------------------------------------
    // Battery connectivity
    //----------------------------------------------------------------------------------

    /**
     * Determines whether the battery is connected to the capacitor.
     * When the battery is not connected, the plate charge control becomes active.
     *
     * @param batteryConnected
     */
    public void setBatteryConnected( boolean batteryConnected ) {
        if ( batteryConnected != isBatteryConnected() ) {
            /*
             * When disconnecting the battery, set the disconnected plate charge to
             * whatever the total plate charge was with the battery connected.
             * Need to do this before changing the property value.
             */
            if ( !batteryConnected ) {
                disconnectedPlateCharge = getTotalCharge();
            }
            batteryConnectedProperty.set( batteryConnected );
            updatePlateVoltages();
            fireCircuitChanged();
        }
    }

    public boolean isBatteryConnected() {
        return batteryConnectedProperty.get();
    }

    // Updates the plate voltage, depending on whether the battery is connected.
    @Override protected void updatePlateVoltages() {
        double V = getBattery().getVoltage();
        if ( !isBatteryConnected() ) {
            V = disconnectedPlateCharge / capacitor.getTotalCapacitance(); // V = Q/C
        }
        capacitor.setPlatesVoltage( V );
    }

    //----------------------------------------------------------------------------------
    // Plate voltage (V)
    //----------------------------------------------------------------------------------

    /*
     * Normally the total voltage is equivalent to the battery voltage.
     * But disconnecting the battery changes how we compute total voltage, so override this method.
     */
    @Override public double getTotalVoltage() {
        if ( isBatteryConnected() ) {
            return super.getTotalVoltage();
        }
        else {
            return capacitor.getPlatesVoltage();
        }
    }

    // @see ICircuit.getVoltageAt
    public double getVoltageAt( Shape shape ) {
        double voltage = Double.NaN;
        if ( ( isBatteryConnected() && ( getTopWire().intersects( shape ) || getBattery().intersectsTopTerminal( shape ) ) ) || capacitor.intersectsTopPlateShape( shape ) ) {
            voltage = getTotalVoltage();
        }
        else if ( ( isBatteryConnected() && ( getBottomWire().intersects( shape ) || getBattery().intersectsBottomTerminal( shape ) ) ) || capacitor.intersectsBottomPlateShape( shape ) ) {
            voltage = 0;
        }
        return voltage;
    }

    //----------------------------------------------------------------------------------
    // Capacitance (C)
    //----------------------------------------------------------------------------------

    // @see ICircuit.getTotalCapacitance
    public double getTotalCapacitance() {
        return capacitor.getTotalCapacitance();
    }

    //----------------------------------------------------------------------------------
    // Plate charge (Q)
    //----------------------------------------------------------------------------------

    /**
     * Sets the value used for plate charge when the battery is disconnected.
     * (design doc symbol: Q_total)
     *
     * @param disconnectedPlateCharge Coulombs
     */
    public void setDisconnectedPlateCharge( double disconnectedPlateCharge ) {
        if ( disconnectedPlateCharge != this.disconnectedPlateCharge ) {
            this.disconnectedPlateCharge = disconnectedPlateCharge;
            if ( !isBatteryConnected() ) {
                updatePlateVoltages();
                fireCircuitChanged();
            }
        }
    }

    /**
     * Gets the value used for plate charge when the battery is disconnected.
     * (design doc symbol: Q_total)
     *
     * @return charge, in Coulombs
     */
    public double getDisconnectedPlateCharge() {
        return disconnectedPlateCharge;
    }

    // @see ICircuit.getTotalCharge
    @Override public double getTotalCharge() {
        return capacitor.getTotalPlateCharge();
    }
}
