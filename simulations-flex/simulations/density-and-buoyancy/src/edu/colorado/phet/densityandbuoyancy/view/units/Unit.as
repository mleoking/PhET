//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.units {
/**
 * Provides a way to convert to and from different units (such as meters <-> feet)
 */
public class Unit {
    private var _name: String;

    public function Unit( name: String ) {
        this._name = name;
    }

    public function get name(): String {
        return _name;
    }

    public function toSI( value: Number ): Number {
        return NaN;
    }

    public function fromSI( value: Number ): Number {
        return NaN;
    }
}
}