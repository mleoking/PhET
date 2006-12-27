// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.semiconductor.macro.battery.Battery;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.circuit:
//            Circuit, Resistor

public class MacroCircuit extends Circuit {

    public MacroCircuit() {
        super( x, y );
        double d = xmax - x;
        double d1 = d * 0.80000000000000004D;
        double d2 = ( d - d1 ) / 2D;
        wireTo( x + d2, y );
        resistor = resistorTo( x + d2 + d1, y );
        wireTo( xmax, y );
        wireTo( xmax, 0.90000000000000002D );
        wireTo( 0.65000000000000002D, 0.90000000000000002D );
        battery = batteryTo( 0.5D, 0.90000000000000002D );
        wireTo( x, 0.90000000000000002D );
        wireTo( x, y );
    }

    public Resistor getResistor() {
        return resistor;
    }

    public Battery getBattery() {
        return battery;
    }

    static double x = 0.31D;
    static double xmax = 0.90000000000000002D;
    static double y = 0.40000000000000002D;
    private Resistor resistor;
    private Battery battery;

}
