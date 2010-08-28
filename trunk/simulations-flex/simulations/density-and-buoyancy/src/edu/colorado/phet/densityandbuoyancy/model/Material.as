package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.flexcommon.FlexSimStrings;

public class Material {
    public static var STYROFOAM:Material = new Material(FlexSimStrings.get("material.styrofoam", "Styrofoam"), 150, false);//between 25 and 200 according to http://wiki.answers.com/Q/What_is_the_density_of_styrofoam; chose 150 so it isn't too low to show on slider, but not 200 so it's not half of wood
    public static var WOOD:Material = new Material(FlexSimStrings.get("material.wood", "Wood"), 0.4 * 1000.0, false);
    public static var LEAD:Material = new Material(FlexSimStrings.get("material.lead", "Lead"), 11.34 * 1000.0, false);
    public static var ALUMINUM:Material = new Material(FlexSimStrings.get("material.aluminum", "Aluminum"), 2.7 * 1000.0, false);
    public static var BRICK:Material = new Material(FlexSimStrings.get("material.brick", "Brick"), 1922, false);//see http://www.simetric.co.uk/si_materials.htm
    public static var WATER:Material = new Material(FlexSimStrings.get("material.water", "Water"), 1.0 * 1000.0, false);
    public static var WATER_BALLOON:Material = new Material(FlexSimStrings.get("material.waterBalloon", "Water Balloon"), 1.0 * 1000.0, false);
    public static var CUSTOM:Material = new Material(FlexSimStrings.get("material.custom", "Custom"), 1.0 * 1000.0, true);
    public static var SELECTABLE_MATERIALS:Array = [STYROFOAM, WOOD, WATER_BALLOON, BRICK, ALUMINUM];//Note that Custom is omitted from here, though it is added in some places where this list is used

    public static var DIAMOND:Material = new Material(FlexSimStrings.get("material.diamond", "Diamond"), 3530, false);
    public static var GOLD:Material = new Material(FlexSimStrings.get("material.gold", "Gold"), 19.3 * 1000.0, false);
    public static var GASOLINE_BALLOON:Material = new Material(FlexSimStrings.get("material.gasoline", "Gasoline Balloon"), 0.7 * 1000.0, false);
    public static var ICE:Material = new Material(FlexSimStrings.get("material.ice", "Ice"), 919, false);
    public static var APPLE:Material = new Material(FlexSimStrings.get("material.apple", "Apple"), 641, false);
    public static var MYSTERY_MATERIALS:Array = [GOLD,DIAMOND,GASOLINE_BALLOON,ICE,APPLE];

    private var density:Number;
    private var _name:String;
    private var _isCustom:Boolean;
    public static var ALL:Array = [ALUMINUM, APPLE, DIAMOND, GASOLINE_BALLOON,GOLD,ICE, LEAD,WATER_BALLOON,WOOD];

    public function Material(name:String, density:Number, isCustom:Boolean) {
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

    public function equals(material:Material):Boolean {
        return material.density == density && material._name == _name && material._isCustom == _isCustom;
    }

    public function isCustom():Boolean {
        return _isCustom;
    }
}
}