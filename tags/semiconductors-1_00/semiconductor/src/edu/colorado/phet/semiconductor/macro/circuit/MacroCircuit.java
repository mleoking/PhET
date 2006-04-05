package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.semiconductor.macro.circuit.battery.Battery;

/**
 * User: Sam Reid
 * Date: Jan 16, 2004
 * Time: 5:38:39 PM
 * Copyright (c) Jan 16, 2004 by Sam Reid
 */
public class MacroCircuit extends Circuit {
    private Resistor resistor;
    private Battery battery;

    public MacroCircuit( double x, double y, double width, double height, double resistorThickness ) {
        super( x, y, resistorThickness );
        double resistorWidth = width * .4;
        double battWidth = width * .25;
        double resInset = ( width - resistorWidth ) / 2.0;
        double battInset = ( width - battWidth ) / 2.0;
        wireTo( x + resInset, y );
        resistor = resistorTo( x + resInset + resistorWidth, y );
        wireTo( x + width, y );
        wireTo( x + width, y + height );
        wireTo( x + width - battInset, y + height );
        battery = batteryTo( x + battInset, y + height );
        wireTo( x, y + height );
        wireTo( x, y );
    }

    public Resistor getResistor() {
        return resistor;
    }

    public Battery getBattery() {
        return battery;
    }
}
