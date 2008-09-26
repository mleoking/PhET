package edu.colorado.phet.semiconductor.macro.circuit.battery;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.semiconductor.macro.circuit.LinearBranch;


/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:13:01 PM
 */
public class Battery extends LinearBranch {
    private double volts = 0;
    ArrayList obs = new ArrayList();

    public Battery( Vector2D.Double start, Vector2D.Double end ) {
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
