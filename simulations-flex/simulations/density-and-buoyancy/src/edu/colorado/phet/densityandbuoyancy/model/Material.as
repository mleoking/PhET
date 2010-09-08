package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.flexcommon.FlexSimStrings;

public class Material {
    public static var STYROFOAM:Material = new Material(FlexSimStrings.get("material.styrofoam", "Styrofoam"), 150, false, 0xcccccc);//between 25 and 200 according to http://wiki.answers.com/Q/What_is_the_density_of_styrofoam; chose 150 so it isn't too low to show on slider, but not 200 so it's not half of wood
    public static var WOOD:Material = new Material(FlexSimStrings.get("material.wood", "Wood"), 400, false, 0xd9ab5d);
    public static var ICE:Material = new Material(FlexSimStrings.get("material.ice", "Ice"), 919, false, 0xbfdbe6);
    public static var WATER_BALLOON:Material = new Material(FlexSimStrings.get("material.waterBalloon", "Water Balloon"), 1000.0, false);
    public static var BRICK:Material = new Material(FlexSimStrings.get("material.brick", "Brick"), 1922, false, 0xab695b);//see http://www.simetric.co.uk/si_materials.htm
    public static var ALUMINUM:Material = new Material(FlexSimStrings.get("material.aluminum", "Aluminum"), 2700, false, 0x75928d);
    public static var CUSTOM:Material = new Material(FlexSimStrings.get("material.custom", "Custom"), 1000.0, true);
    //TODO: Add back water balloon after creating geometry for it
    //NOTE: If other materials less dense than Wood are added, then a volume-bounding solution will need to be applied, like
    //the workaround in PropertyEditor.createSlider
    public static var SELECTABLE_MATERIALS:Array = [STYROFOAM, WOOD, ICE, BRICK, ALUMINUM];//Note that Custom is omitted from here, though it is added in some places where this list is used

    public static var WATER:Material = new Material(FlexSimStrings.get("material.water", "Water"), 1000.0, false);
    public static var LEAD:Material = new Material(FlexSimStrings.get("material.lead", "Lead"), 11340, false);
    public static var DIAMOND:Material = new Material(FlexSimStrings.get("material.diamond", "Diamond"), 3530, false);
    public static var GOLD:Material = new Material(FlexSimStrings.get("material.gold", "Gold"), 19300, false);
    public static var GASOLINE_BALLOON:Material = new Material(FlexSimStrings.get("material.gasoline", "Gasoline Balloon"), 700, false);
    public static var APPLE:Material = new Material(FlexSimStrings.get("material.apple", "Apple"), 641, false);
    public static var MYSTERY_MATERIALS:Array = [GOLD,DIAMOND,GASOLINE_BALLOON,ICE,APPLE];

    private var density:Number;
    private var _name:String;
    private var _isCustom:Boolean;
    public static var ALL:Array = [ALUMINUM, APPLE, DIAMOND, GASOLINE_BALLOON,GOLD,ICE, LEAD,WATER_BALLOON,WOOD];//sorted below
    private var _tickColor:uint;

    private static function sortOnDensity(a:Material, b:Material):Number {
        var aPrice:Number = a.getDensity();
        var bPrice:Number = b.getDensity();

        if (aPrice > bPrice) {
            return 1;
        } else if (aPrice < bPrice) {
            return -1;
        } else {
            //aPrice == bPrice
            return 0;
        }
    }

    ALL.sort(sortOnDensity);

    public function Material(name:String, density:Number, isCustom:Boolean, tickColor:uint = 0x000000) {
        this.density = density;
        this._name = name;
        this._isCustom = isCustom;
        this._tickColor = tickColor;
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

    public function get tickColor():uint {
        return _tickColor;
    }
}
}