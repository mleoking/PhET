package edu.colorado.phet.densityandbuoyancy.view.units {
public class LinearUnit extends Unit {
    private var scale:Number;

    public function LinearUnit(name:String, scale:Number) {
        super(name);
        this.scale = scale;
    }

    override public function toSI(value:Number):Number {
        return value / scale;
    }

    override public function fromSI(value:Number):Number {
        return value * scale;
    }
}
}