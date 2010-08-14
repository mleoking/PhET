package edu.colorado.phet.densityandbuoyancy.view.units {
public class Unit {
    private var _name:String;

    public function Unit(name:String) {
        this._name = name;
    }

    public function get name():String {
        return _name;
    }

    public function toSI(value:Number):Number {
        return NaN;
    }

    public function fromSI(value:Number):Number {
        return NaN;
    }
}
}