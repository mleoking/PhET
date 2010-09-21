package edu.colorado.phet.densityandbuoyancy.view.units {
public class Units {
    private var name:String;
    private var _massUnit:Unit;
    private var _volumeUnit:Unit;
    private var _densityUnit:Unit;

    public function Units(name:String, massUnit:Unit, volumeUnit:Unit, densityUnit:Unit) {
        this.name = name;
        this._massUnit = massUnit;
        this._volumeUnit = volumeUnit;
        this._densityUnit = densityUnit;
    }

    public function get massUnit():Unit {
        return _massUnit;
    }

    public function get volumeUnit():Unit {
        return _volumeUnit;
    }

    public function get densityUnit():Unit {
        return _densityUnit;
    }
}
}