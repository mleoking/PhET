//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
/**
 * Function that doesn't bound the value.
 */
public class Unbounded implements Bounds {
    public function Unbounded() {
    }

    public function clamp( value: Number ): Number {
        return value;
    }
}
}