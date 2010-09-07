package edu.colorado.phet.densityandbuoyancy.view {
import away3d.materials.*;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.StringProperty;

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.geom.Rectangle;

import mx.core.BitmapAsset;

public class BlockNode extends CubeNode implements Pickable {

    private var block:Block;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/custom.jpg")]
    private var customObjectTexture:Class;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/styrofoam.jpg")]
    //SRR made styrofoam.jpg
    private var styrofoamTextureClass:Class;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/aluminum.jpg")]
    //SRR modified aluminum.jpg based on microsoft clip art
    private var aluminumTextureClass:Class;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/wall.jpg")]
    //came with away3d
    private var brickTextureClass:Class;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/ice.jpg")]
    //SRR modified aluminum.jpg based on microsoft clip art
    private var iceTextureClass:Class;

    // initial testing wood texture
    // public domain, see http://www.publicdomainpictures.net/view-image.php?picture=wood-texture&image=1282&large=1
    // license: " 	This image is public domain. You may use this picture for any purpose, including commercial. If you do use it, please consider linking back to us. If you are going to redistribute this image online, a hyperlink to this particular page is mandatory."
    [Embed(source="../../../../../../data/density-and-buoyancy/images/wood4.png")]
    private var woodClass:Class;

    private var sideMaterial:BitmapMaterial;

    private var textureBitmap:Bitmap; // the texture being used (wood bitmap, wall (custom) bitmap, etc.)
    private var label:StringProperty;
    private var readoutFontScale:Number;

    public function BlockNode(block:Block, view:AbstractDensityModule, label:StringProperty, readoutFontScale:Number = 1):void {
        this.label = label;
        this.block = block;
        this.readoutFontScale = readoutFontScale;

        super(block, view);

        var cube:PickableCube = getCube();
        cube.cubeMaterials.back = cube.cubeMaterials.left = cube.cubeMaterials.right = cube.cubeMaterials.top = cube.cubeMaterials.bottom = cube.cubeMaterials.front = sideMaterial;
        addChild(cube);

        label.addListener(updateText);
        block.addMaterialListener(updateMaterial);
        block.addColorTransformListener(updateMaterial);

        updateMaterial();
        updateGeometry();
    }

    public function getBlock():Block {
        return block;
    }

    private function updateText():void {
        setReadoutText( label.value );
        updateGeometry();
    }

    private function updateMaterial():void {
        // update the bitmap we use as a background
        //TODO: Object orient this
        if (block.getMaterial() == Material.WOOD) {
            textureBitmap = new woodClass();
        }
        else if (block.getMaterial() == Material.BRICK) {
            textureBitmap = new brickTextureClass();
        }
        else if (block.getMaterial() == Material.STYROFOAM) {
            textureBitmap = new styrofoamTextureClass();
        }
        else if (block.getMaterial() == Material.ALUMINUM) {
            textureBitmap = new aluminumTextureClass();
        }
        else if (block.getMaterial() == Material.ICE) {
            textureBitmap = new iceTextureClass();
        }
        else {
            textureBitmap = getCustomBitmap();
        }

        // update the text field
        updateText();

        sideMaterial = new BitmapMaterial(textureBitmap.bitmapData);
        if (block.getMaterial() == Material.ICE) {//TODO: Object orient this
            sideMaterial.alpha = 0.75;
        }

        // TODO: possibly change tiling for textures that are not symmetric
        var cube:PickableCube = getCube();
        cube.cubeMaterials.back = cube.cubeMaterials.left = cube.cubeMaterials.right = cube.cubeMaterials.top = cube.cubeMaterials.bottom = cube.cubeMaterials.front = sideMaterial;
    }

    private function getCustomBitmap():Bitmap {
        var wallData:BitmapData = (new customObjectTexture() as BitmapAsset).bitmapData;
        wallData.colorTransform(new Rectangle(0, 0, wallData.width, wallData.height), block.colorTransform);
        return new Bitmap(wallData);
    }

    public override function updateGeometry():void {
        super.updateGeometry();
        textReadout.x = getCube().x - getCube().width / 2;
        textReadout.y = getCube().y - getCube().height / 2;
        textReadout.z = getCube().z - getCube().depth / 2 - DensityConstants.FUDGE_FACTOR_DZ;
    }

    override protected function getFontReadoutSize():Number {
        return super.getFontReadoutSize() * readoutFontScale;
    }
}
}