// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.semiconductor.macro.circuit.battery;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.semiconductor.macro.circuit.LinearBranch;


/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:13:01 PM
 */
public class Battery extends LinearBranch {
    private double volts = 0;
    ArrayList obs = new ArrayList();

    public Battery( MutableVector2D start, MutableVector2D end ) {
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
