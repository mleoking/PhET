// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Model of a circuit with a battery and N capacitors in series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SeriesCircuit extends AbstractCircuit {

    private final ArrayList<Capacitor> capacitors;

    public SeriesCircuit( String displayName, CLModelViewTransform3D mvt ) {
        super( displayName, mvt );
        capacitors = new ArrayList<Capacitor>(); //TODO populate
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

    //TODO move this to AbstractCircuit, override in SingleCircuit to account for battery disconnection?
    // U = 0.5 * C_total * V_total^2
    public double getStoredEnergy() {
        double C_total = getTotalCapacitance(); // F
        double V_total = getBattery().getVoltage(); // V
        return 0.5 * C_total * V_total * V_total; // Joules (J)
    }

    public double getVoltageBetween( Shape positiveShape, Shape negativeShape ) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //TODO move to AbstractCircuit, replace implementation in SingleCircuit
    public double getEffectiveEFieldAt( Point3D location ) {
        double eField = 0;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.isBetweenPlatesShape( location ) ) {
                eField = capacitor.getEffectiveEField();
                break;
            }
        }
        return eField;
    }

    //TODO move to AbstractCircuit, replace implementation in SingleCircuit
    public double getPlatesDielectricEFieldAt( Point3D location ) {
        double eField = 0;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.isInsideDielectricBetweenPlatesShape( location ) ) {
                eField = capacitor.getPlatesDielectricEField();
                break;
            }
            else if ( capacitor.isInsideAirBetweenPlatesShape( location ) ) {
                eField = capacitor.getPlatesAirEField();
                break;
            }
        }
        return eField;
    }

    //TODO move to AbstractCircuit, replace implementation in SingleCircuit
    public double getDielectricEFieldAt( Point3D location ) {
        double eField = 0;
        for ( Capacitor capacitor : getCapacitors() ) {
            if ( capacitor.isInsideDielectricBetweenPlatesShape( location ) ) {
                eField = capacitor.getDielectricEField();
                break;
            }
            else if ( capacitor.isInsideAirBetweenPlatesShape( location ) ) {
                eField = capacitor.getAirEField();
                break;
            }
        }
        return eField;
    }

    public void reset() {
        super.reset();
        for ( Capacitor capacitor : capacitors ) {
            capacitor.reset();
        }
    }
}
