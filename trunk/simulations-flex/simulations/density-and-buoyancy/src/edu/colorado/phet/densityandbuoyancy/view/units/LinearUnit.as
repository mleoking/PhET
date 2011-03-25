//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.units {
/**
 * A linear unit transforms between different units by multiplying--common for distance measurements (but couldn't be used e.g., for Celsius <-> Fahrenheit
 */
public class LinearUnit extends Unit {
    private var scale: Number;

    public function LinearUnit( name: String, scale: Number ) {
        super( name );
        this.scale = scale;
    }

    override public function toSI( value: Number ): Number {
        return value / scale;
    }

    override public function fromSI( value: Number ): Number {
        return value * scale;
    }
}
}