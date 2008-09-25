package edu.colorado.phet.semiconductor.macro.circuit.battery;

import java.util.ArrayList;

import edu.colorado.phet.semiconductor.macro.circuit.LinearBranch;
import edu.colorado.phet.semiconductor.util.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:13:01 PM
 */
public class Battery extends LinearBranch {
    private double volts = 0;
    ArrayList obs = new ArrayList();

    public Battery( PhetVector start, PhetVector end ) {
        super( start, end );
    }

    public void addBatteryListener( BatteryListener bl ) {
        obs.add( bl );
    }

    public void setVoltage( double volts ) {
        this.volts = volts;
        for ( int i = 0; i < obs.size(); i++ ) {
            BatteryListener batteryListener = (BatteryListener) obs.get( i );
            batteryListener.voltageChanged( this );
        }
    }

    public double getVoltage() {
        return volts;
    }
}
