package edu.colorado.phet.densityflex.view.units {
public class Meters extends Unit {
    public function Meters() {
        super("Meters");
    }

    override public function toSI(value:Number) {
        return value;
    }

    override public function fromSI(value:Number) {
        return value;
    }
}
}