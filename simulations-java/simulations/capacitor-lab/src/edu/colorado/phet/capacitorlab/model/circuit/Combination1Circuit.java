// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.ICapacitor.CapacitorChangeListener;
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

    public Combination1Circuit( IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation,
                                double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        super( CLStrings.COMBINATION_1, mvt, batteryLocation );

        // create capacitors
        {
            double x = batteryLocation.getX() + X_SPACING;
            double y = batteryLocation.getY() - ( 0.5 * Y_SPACING );
            final double z = batteryLocation.getZ();

            // Series
            c1 = new Capacitor( new Point3D.Double( x, y, z ), plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );
            y += Y_SPACING;
            c2 = new Capacitor( new Point3D.Double( x, y, z ), plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );

            // Parallel
            x += X_SPACING;
            y = batteryLocation.getY();
            c3 = new Capacitor( new Point3D.Double( x, y, z ), plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );

            capacitors = new ArrayList<Capacitor>();
            capacitors.add( c1 );
            capacitors.add( c2 );
            capacitors.add( c3 );
        }

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

    private void updateVoltages() {
        double Q_total = getTotalCharge();
        // series
        c1.setPlatesVoltage( Q_total / c1.getTotalCapacitance() );
        c2.setPlatesVoltage( Q_total / c2.getTotalCapacitance() );
        // parallel
        c3.setPlatesVoltage( getTotalVoltage() );
    }

    public ArrayList<Capacitor> getCapacitors() {
        return capacitors;
    }

    // C_total = ( 1 / ( 1/C1 + 1/C2 ) ) + C3
    public double getTotalCapacitance() {
        double C1 = c1.getTotalCapacitance();
        double C2 = c2.getTotalCapacitance();
        double C3 = c3.getTotalCapacitance();
        return ( 1 / ( 1 / C1 + 1 / C2 ) ) + C3;
    }

    public double getVoltageAt( Shape s ) {
        double voltage = Double.NaN;
        if ( getBattery().intersectsTopTerminal( s ) || c1.intersectsTopPlateShape( s ) || c3.intersectsTopPlateShape( s ) ) {
            voltage = getTotalVoltage();
        }
        else if ( getBattery().intersectsBottomTerminal( s ) || c2.intersectsBottomPlateShape( s ) || c3.intersectsBottomPlateShape( s ) ) {
            voltage = 0;
        }
        else if ( c1.intersectsBottomPlateShape( s ) || c2.intersectsTopPlateShape( s ) ) {
            voltage = c2.getPlatesVoltage();
        }
        else {
            //TODO check wires
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
