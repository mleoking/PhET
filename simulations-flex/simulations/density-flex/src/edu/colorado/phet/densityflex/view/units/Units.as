package edu.colorado.phet.densityflex.view.units {
public class Units {
    private var name:String;
    private var massUnit:Unit;
    private var volumeUnit:Unit;
    private var densityUnit:Unit;

    public function Units(name:String, massUnit:Unit, volumeUnit:Unit, densityUnit:Unit) {
        this.name = name;
        this.massUnit = massUnit;
        this.volumeUnit = volumeUnit;
        this.densityUnit = densityUnit;
    }
}
}