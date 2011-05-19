// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.ICapacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery (B) and N capacitors (ci) in series.
 * <p/>
 * <code>
 * |-----|
 * |     |
 * |    c1
 * |     |
 * B    c2
 * |     |
 * |    c3
 * |     |
 * |-----|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SeriesCircuit extends AbstractCircuit {

    private static final double X_SPACING = MultipleCapacitorsModel.CAPACITOR_X_SPACING;
    private static final double Y_SPACING = MultipleCapacitorsModel.CAPACITOR_Y_SPACING;

    private final ArrayList<Capacitor> capacitors;

    public SeriesCircuit( String displayName, IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation, int numberOfCapacitors,
                          double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        super( displayName, clock, mvt, batteryLocation );

        assert ( numberOfCapacitors > 0 );

        capacitors = createCapacitors( mvt, batteryLocation, numberOfCapacitors,
                                       plateWidth, plateSeparation, dielectricMaterial, dielectricOffset );

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

        //TODO add wires
    }

    // Creates a column of capacitors, to the right of the battery, vertically centered on the battery.
    private ArrayList<Capacitor> createCapacitors( CLModelViewTransform3D mvt, Point3D batteryLocation, int numberOfCapacitors,
                                                   double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {

        final double x = batteryLocation.getX() + X_SPACING;
        double y = 0;
        final double z = batteryLocation.getZ();

        double half = numberOfCapacitors / 2;
        if ( half * 2 != numberOfCapacitors ) {
            // we have an odd number of capacitors, align the middle one with the battery
            y = batteryLocation.getY() - ( half * Y_SPACING );
        }
        else {
            // we have an even number of capacitors
            y = batteryLocation.getY() - ( half * Y_SPACING ) + ( 0.5 * Y_SPACING );
        }

        ArrayList<Capacitor> capacitors = new ArrayList<Capacitor>();
        for ( int i = 0; i < numberOfCapacitors; i++ ) {
            Point3D location = new Point3D.Double( x, y, z );
            Capacitor capacitor = new Capacitor( location, plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );
            capacitors.add( capacitor );
            y += Y_SPACING;
        }
        return capacitors;
    }

    private void updateVoltages() {
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

    public double getVoltageAt( Shape s ) {
        double voltage = Double.NaN;

        // battery
        if ( getBattery().intersectsTopTerminal( s ) ) {
            voltage = getTotalVoltage();
        }
        else if ( getBattery().intersectsBottomTerminal( s ) ) {
            voltage = 0;
        }
        else {
            // plates
            voltage = intersectsSomePlate( s );

            // wires
            if ( voltage == Double.NaN ) {
                //TODO check wires
            }
        }

        return voltage;
    }

    /*
     * If shape intersects a plate, returns the voltage between that plate and ground.
     * If no intersection, returns Double.NaN.
     */
    private double intersectsSomePlate( Shape s ) {
        double voltage = Double.NaN;
        ArrayList<Capacitor> capacitors = getCapacitors();
        for ( int i = 0; i < capacitors.size(); i++ ) {
            if ( capacitors.get( i ).intersectsTopPlateShape( s ) ) {
                // sum voltage of this capacitor and all capacitors below it.
                voltage = 0;
                for ( int j = i; j < capacitors.size(); j++ ) {
                    voltage += capacitors.get( j ).getPlatesVoltage();
                }
            }
            else if ( capacitors.get( i ).intersectsBottomPlateShape( s ) ) {
                // sum voltage of all capacitors below this one.
                voltage = 0;
                for ( int j = i + 1; j < capacitors.size(); j++ ) {
                    voltage += capacitors.get( j ).getPlatesVoltage();
                }
            }
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
