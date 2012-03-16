//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.units {
import edu.colorado.phet.flashcommon.AbstractMethodError;

/**
 * Provides a way to convert to and from different units (such as meters <-> feet)
 * This is an abstract class (but since AS3 doesn't support 'abstract' we throw AbstractMethodError for methods that should be abstract.
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
        throw new AbstractMethodError();
    }

    public function fromSI( value: Number ): Number {
        throw new AbstractMethodError();
    }
}
}