package edu.colorado.phet.densityflex.model {
public class Substance {
    public static var STYROFOAM:Substance = new Substance( "Styrofoam", 0.035 * 1000.0 );
    public static var LEAD:Substance = new Substance( "Lead", 11.34 * 1000.0 );
    public static var WATER:Substance = new Substance( "Water", 1.0 * 1000.0 );
    public static var WATER_BALLOON:Substance = new Substance( "Water Balloon", 1.0 * 1000.0 );
    public static var OBJECT_SUBSTANCES:Array = [STYROFOAM, LEAD, WATER_BALLOON];
    private var density:Number;
    private var _name:String;

    public function Substance( name:String, density:Number ) {
        this.density = density;
        this._name = name;
    }

    public function getDensity():Number {
        return density;
    }

    public function getName():String {
        return name;
    }

    public function get name():String {
        return _name;
    }
    public function equals(substance:Substance):Boolean{
//        return substance.density==density && substance._name==_name;
        return substance.density==density;
    }
}
}