// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.ICapacitor.CapacitorChangeListener;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery and N capacitors in series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SeriesCircuit extends AbstractCircuit {

    private final ArrayList<Capacitor> capacitors;

    public SeriesCircuit( String displayName, IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation, int numberOfCapacitors,
                          double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        super( displayName, mvt, batteryLocation );
        capacitors = new ArrayList<Capacitor>(); //TODO populate

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
            sum += 1 / capacitor.getTotalCapacitance();
        }
        return 1 / sum;
    }

    // Q_total = V_total * C_total
    public double getTotalCharge() {
        return getBattery().getVoltage() * getTotalCapacitance();
    }

    public double getVoltageAt( Shape s ) {
        return 0; //TODO
    }

    public void reset() {
        super.reset();
        for ( Capacitor capacitor : capacitors ) {
            capacitor.reset();
        }
    }
}
