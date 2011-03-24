//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
public class Unbounded implements Bounds {
    public function Unbounded() {
    }

    public function clamp( newValue: Number ): Number {
        return newValue;
    }
}
}