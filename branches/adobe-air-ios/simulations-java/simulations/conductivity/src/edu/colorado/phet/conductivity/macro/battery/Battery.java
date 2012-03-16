// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.macro.battery;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.conductivity.macro.circuit.LinearBranch;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.battery:
//            BatteryListener

public class Battery extends LinearBranch {

    public Battery( Vector2D phetvector, Vector2D phetvector1 ) {
        super( phetvector, phetvector1 );
        volts = 0.0D;
        obs = new ArrayList();
    }

    public void addBatteryListener( BatteryListener batterylistener ) {
        obs.add( batterylistener );
    }

    public void setVoltage( double d ) {
        volts = d;
        for ( int i = 0; i < obs.size(); i++ ) {
            BatteryListener batterylistener = (BatteryListener) obs.get( i );
            batterylistener.voltageChanged( this );
        }

    }

    public double getVoltage() {
        return volts;
    }

    private double volts;
    ArrayList obs;
}
