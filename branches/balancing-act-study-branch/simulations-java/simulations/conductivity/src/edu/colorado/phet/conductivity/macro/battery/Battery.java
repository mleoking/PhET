// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity.macro.battery;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.conductivity.macro.circuit.LinearBranch;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.battery:
//            BatteryListener

public class Battery extends LinearBranch {

    public Battery( MutableVector2D phetvector, MutableVector2D phetvector1 ) {
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
