package edu.colorado.phet.semiconductor.macro.circuit.battery;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.circuit.LinearBranch;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:13:01 PM
 * Copyright (c) Jan 15, 2004 by Sam Reid
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
        for( int i = 0; i < obs.size(); i++ ) {
            BatteryListener batteryListener = (BatteryListener)obs.get( i );
            batteryListener.voltageChanged( this );
        }
    }

    public double getVoltage() {
        return volts;
    }
}
