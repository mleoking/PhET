package edu.colorado.phet.densityandbuoyancy.components {
public class Unbounded implements Bounds {
    public function Unbounded() {
    }

    public function clamp( newValue: Number ): Number {
        return newValue;
    }
}
}