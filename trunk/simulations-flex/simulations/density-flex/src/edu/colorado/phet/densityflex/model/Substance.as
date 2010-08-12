package edu.colorado.phet.densityflex.model {
public class Substance {
//    public static var STYROFOAM:Substance = new Substance( "Styrofoam", 0.035 * 1000.0 ,false);
    public static var WOOD:Substance = new Substance( "Wood", 0.4 * 1000.0 ,false);
    public static var LEAD:Substance = new Substance( "Lead", 11.34 * 1000.0 ,false);
    public static var WATER:Substance = new Substance( "Water", 1.0 * 1000.0 ,false);
    public static var WATER_BALLOON:Substance = new Substance( "Water Balloon", 1.0 * 1000.0 ,false);
    public static var CUSTOM:Substance = new Substance( "Custom", 1.0 * 1000.0);
    public static var OBJECT_SUBSTANCES:Array = [WOOD, WATER_BALLOON, LEAD];
    
    private var density:Number;
    private var _name:String;
    private var _isCustom;
    
    public function Substance( name:String, density:Number,isCustom:Boolean=true ) {
        this.density = density;
        this._name = name;
        this._isCustom=isCustom;
    }
    public function synchronizeDensity(densityObject:DensityObject):void{
        if (!isCustom){
            densityObject.setDensity(density);
        }
    }
    public function getDensity():Number {
        return density;
    }

    public function get name():String {
        return _name;
    }
    
    public function equals(substance:Substance):Boolean{
//        return substance.density==density && substance._name==_name;
        return substance.density==density;
    }

    public function isCustom():Boolean {
        return _isCustom;
    }
}
}