package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.Bitmap;

public class Material {
    // initial testing wood texture
    // public domain, see http://www.publicdomainpictures.net/view-image.php?picture=wood-texture&image=1282&large=1
    // license: " 	This image is public domain. You may use this picture for any purpose, including commercial. If you do use it, please consider linking back to us. If you are going to redistribute this image online, a hyperlink to this particular page is mandatory."
    [Embed(source="../../../../../../data/density-and-buoyancy/images/wood.png")]
    private static var woodTextureClass: Class;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/custom.jpg")]
    public static var customObjectTexture: Class;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/styrofoam.jpg")]
    //SRR made styrofoam.jpg
    private static var styrofoamTextureClass: Class;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/aluminum.jpg")]
    //SRR modified aluminum.jpg based on microsoft clip art
    private static var aluminumTextureClass: Class;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/wall.jpg")]
    //came with away3d
    private static var brickTextureClass: Class;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/ice.jpg")]
    //SRR modified aluminum.jpg based on microsoft clip art
    private static var iceTextureClass: Class;

    public static var STYROFOAM: Material = new Material( FlexSimStrings.get( "material.styrofoam", "Styrofoam" ), 150, false, 0xcccccc, new styrofoamTextureClass() );//between 25 and 200 according to http://wiki.answers.com/Q/What_is_the_density_of_styrofoam; chose 150 so it isn't too low to show on slider, but not 200 so it's not half of wood
    public static var WOOD: Material = new Material( FlexSimStrings.get( "material.wood", "Wood" ), 400, false, 0xd9ab5d, new woodTextureClass() );
    public static var ICE: Material = new Material( FlexSimStrings.get( "material.ice", "Ice" ), 919, false, 0xbfdbe6, new iceTextureClass(), 0.75 );
    public static var BRICK: Material = new Material( FlexSimStrings.get( "material.brick", "Brick" ), 1922, false, 0xab695b, new brickTextureClass() );//see http://www.simetric.co.uk/si_materials.htm
    public static var ALUMINUM: Material = new Material( FlexSimStrings.get( "material.aluminum", "Aluminum" ), 2700, false, 0x75928d, new aluminumTextureClass() );
    public static var CUSTOM: Material = new Material( FlexSimStrings.get( "material.custom", "My Object" ), 1000.0, true, new customObjectTexture() );
    //TODO: Add back water balloon after creating geometry for it
    public static var WATER_BALLOON: Material = new Material( FlexSimStrings.get( "material.waterBalloon", "Water Balloon" ), 1000.0, false );

    //NOTE: If other materials less dense than Wood are added, then a volume-bounding solution will need to be applied, like
    //the workaround in PropertyEditor.createSlider
    public static var SELECTABLE_MATERIALS: Array = [STYROFOAM, WOOD, ICE, BRICK, ALUMINUM];//Note that Custom is omitted from here, though it is added in some places where this list is used

    public static var WATER: Material = new Material( FlexSimStrings.get( "material.water", "Water" ), 1000.0, false, 0x0088FF );
    public static var AIR: Material = new Material( FlexSimStrings.get( "material.air", "Air" ), 1.2, false, 0x666666 );
    public static var LEAD: Material = new Material( FlexSimStrings.get( "material.lead", "Lead" ), 11340, false );
    public static var DIAMOND: Material = new Material( FlexSimStrings.get( "material.diamond", "Diamond" ), 3530, false );
    public static var GOLD: Material = new Material( FlexSimStrings.get( "material.gold", "Gold" ), 19300, false );
    public static var GASOLINE: Material = new Material( FlexSimStrings.get( "material.gasoline", "Gasoline" ), 700, false, 0x444400 );
    public static var APPLE: Material = new Material( FlexSimStrings.get( "material.apple", "Apple" ), 641, false );
    public static var MYSTERY_MATERIALS: Array = [GOLD,DIAMOND,GASOLINE,ICE,APPLE];

    public static var LABELED_LIQUID_MATERIALS: Array = [AIR,GASOLINE,WATER];

    private var density: Number;
    private var _name: String;
    private var _isCustom: Boolean;
    public static var ALL: Array = [ALUMINUM, APPLE, DIAMOND, GASOLINE,GOLD,ICE, LEAD,WATER,WOOD];//sorted below
    private var _tickColor: uint;
    private var _textureBitmap: Bitmap;
    private var _alpha: Number = 1;

    private static function sortOnDensity( a: Material, b: Material ): Number {
        var aPrice: Number = a.getDensity();
        var bPrice: Number = b.getDensity();

        if ( aPrice > bPrice ) {
            return 1;
        }
        else {
            if ( aPrice < bPrice ) {
                return -1;
            }
            else {
                //aPrice == bPrice
                return 0;
            }
        }
    }

    ALL.sort( sortOnDensity );

    public function Material( name: String, density: Number, isCustom: Boolean, tickColor: uint = 0x000000, textureBitmap: Bitmap = null, alpha: Number = 1 ) {
        this.density = density;
        this._name = name;
        this._isCustom = isCustom;
        this._tickColor = tickColor;
        this._textureBitmap = textureBitmap;
        this._alpha = alpha;
    }

    public function get alpha(): Number {
        return _alpha;
    }

    public function synchronizeDensity( densityObject: DensityObject ): void {
        if ( !isCustom() ) {
            densityObject.setDensity( density );
        }
    }

    public function getDensity(): Number {
        return density;
    }

    public function get name(): String {
        return _name;
    }

    public function equals( material: Material ): Boolean {
        return material.density == density && material._name == _name && material._isCustom == _isCustom;
    }

    public function isCustom(): Boolean {
        return _isCustom;
    }

    public function get tickColor(): uint {
        return _tickColor;
    }

    public function get textureBitmap(): Bitmap {
        return _textureBitmap;
    }
}
}