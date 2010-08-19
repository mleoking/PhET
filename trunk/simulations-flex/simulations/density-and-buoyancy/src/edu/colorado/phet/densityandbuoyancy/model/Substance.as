package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.flexcommon.FlexSimStrings;

public class Substance {
    public static var WOOD:Substance = new Substance(FlexSimStrings.get("substance.wood","Wood"), 0.4 * 1000.0, false);
    public static var LEAD:Substance = new Substance(FlexSimStrings.get("substance.lead","Lead"), 11.34 * 1000.0, false);
    public static var WATER:Substance = new Substance(FlexSimStrings.get("substance.water","Water"), 1.0 * 1000.0, false);
    public static var WATER_BALLOON:Substance = new Substance(FlexSimStrings.get("substance.waterBalloon","Water Balloon"), 1.0 * 1000.0, false);
    public static var CUSTOM:Substance = new Substance(FlexSimStrings.get("substance.custom","Custom"), 1.0 * 1000.0, true);
    public static var OBJECT_SUBSTANCES:Array = [WOOD, WATER_BALLOON, LEAD];

    public static var DIAMOND:Substance = new Substance(FlexSimStrings.get("substance.diamond","Diamond"), 3530, false);
    public static var GOLD:Substance = new Substance(FlexSimStrings.get("substance.gold","Gold"), 19.3 * 1000.0, false);
    public static var GASOLINE_BALLOON:Substance = new Substance(FlexSimStrings.get("substance.gasoline","Gasoline Balloon"), 0.7 * 1000.0, false);
    public static var ICE:Substance = new Substance(FlexSimStrings.get("substance.ice","Ice"), 919, false);
    public static var APPLE:Substance = new Substance(FlexSimStrings.get("substance.apple","Apple"), 641, false);
    public static var MYSTERY_SUBSTANCES:Array = [GOLD,DIAMOND,GASOLINE_BALLOON,ICE,APPLE];

    private var density:Number;
    private var _name:String;
    private var _isCustom:Boolean;
    public static var ALL:Array = [APPLE, DIAMOND, GASOLINE_BALLOON,GOLD,ICE, LEAD,WATER_BALLOON,WOOD];

    public function Substance(name:String, density:Number, isCustom:Boolean) {
        this.density = density;
        this._name = name;
        this._isCustom = isCustom;
    }

    public function synchronizeDensity(densityObject:DensityObject):void {
        if (!isCustom()) {
            densityObject.setDensity(density);
        }
    }

    public function getDensity():Number {
        return density;
    }

    public function get name():String {
        return _name;
    }

    public function equals(substance:Substance):Boolean {
        return substance.density == density && substance._name == _name && substance._isCustom == _isCustom;
    }

    public function isCustom():Boolean {
        return _isCustom;
    }
}
}