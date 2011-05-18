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
 * Model of a circuit with a battery (B), 2 capacitors in parallel (c2, c3), and one additional in series (c1).
 * <p/>
 * <code>
 * |-----|
 * |     |
 * |    c1
 * |     |
 * B     |------|
 * |     |      |
 * |     c2    c3
 * |     |      |
 * |-----|------|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Combination2Circuit extends AbstractCircuit {

    private static final double X_SPACING = MultipleCapacitorsModel.CAPACITOR_X_SPACING;
    private static final double Y_SPACING = MultipleCapacitorsModel.CAPACITOR_Y_SPACING;

    private final ArrayList<Capacitor> capacitors;
    private final Capacitor c1, c2, c3;

    public Combination2Circuit( IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation,
                                double plateWidth, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        super( CLStrings.COMBINATION_2, mvt, batteryLocation );

        // create capacitors
        {
            double x = batteryLocation.getX() + X_SPACING;
            double y = batteryLocation.getY() + ( 0.5 * Y_SPACING );
            final double z = batteryLocation.getZ();

            // Parallel
            c2 = new Capacitor( new Point3D.Double( x, y, z ), plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );
            x += X_SPACING;
            c3 = new Capacitor( new Point3D.Double( x, y, z ), plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );

            // Series
            x -= X_SPACING;
            y -= Y_SPACING;
            c1 = new Capacitor( new Point3D.Double( x, y, z ), plateWidth, plateSeparation, dielectricMaterial, dielectricOffset, mvt );

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
        // parallel
        double V_parallel = Q_total / ( c2.getTotalCapacitance() + c3.getTotalCapacitance() );
        c2.setPlatesVoltage( V_parallel );
        c3.setPlatesVoltage( V_parallel );
    }

    public ArrayList<Capacitor> getCapacitors() {
        return capacitors;
    }

    // C_total = ( 1 / ( 1/C1 + 1/(C2 + C3) )
    public double getTotalCapacitance() {
        double C1 = c1.getTotalCapacitance();
        double C2 = c2.getTotalCapacitance();
        double C3 = c3.getTotalCapacitance();
        return ( 1 / ( 1 / C1 + 1 / ( C2 + C3 ) ) );
    }

    public double getVoltageAt( Shape s ) {
        double voltage = Double.NaN;
        if ( getBattery().intersectsTopTerminal( s ) || c1.intersectsTopPlateShape( s ) ) {
            voltage = getTotalVoltage();
        }
        else if ( getBattery().intersectsBottomTerminal( s ) || c2.intersectsBottomPlateShape( s ) || c3.intersectsBottomPlateShape( s ) ) {
            voltage = 0;
        }
        else if ( c1.intersectsBottomPlateShape( s ) || c2.intersectsTopPlateShape( s ) || c3.intersectsTopPlateShape( s ) ) {
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
