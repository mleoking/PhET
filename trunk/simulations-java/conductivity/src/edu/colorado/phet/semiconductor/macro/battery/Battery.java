// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.battery;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.circuit.LinearBranch;

import java.util.ArrayList;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.battery:
//            BatteryListener

public class Battery extends LinearBranch {

    public Battery( PhetVector phetvector, PhetVector phetvector1 ) {
        super( phetvector, phetvector1 );
        volts = 0.0D;
        obs = new ArrayList();
    }

    public void addBatteryListener( BatteryListener batterylistener ) {
        obs.add( batterylistener );
    }

    public void setVoltage( double d ) {
        volts = d;
        for( int i = 0; i < obs.size(); i++ ) {
            BatteryListener batterylistener = (BatteryListener)obs.get( i );
            batterylistener.voltageChanged( this );
        }

    }

    public double getVoltage() {
        return volts;
    }

    private double volts;
    ArrayList obs;
}
