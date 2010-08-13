package edu.colorado.phet.densityflex.view {
import away3d.materials.*;

import edu.colorado.phet.densityflex.model.Block;
import edu.colorado.phet.densityflex.model.Listener;
import edu.colorado.phet.densityflex.model.StringProperty;
import edu.colorado.phet.densityflex.model.Substance;

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.display.BitmapDataChannel;
import flash.display.Sprite;
import flash.geom.ColorTransform;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.text.TextField;
import flash.text.TextFormat;

import mx.core.BitmapAsset;

public class BlockNode extends CuboidNode implements Pickable, Listener {

    private var frontSprite:Sprite;
    private var textureHolder:Sprite; // holds wood or etc. texture in frontSprite so it stays under the text
    private var block:Block;
    private var view:AbstractDensityModule;
    private var textField:TextField = new TextField();

    [Embed(source="../../../../../../data/density-flex/images/wall.jpg")]
    private var wallClass:Class;

    // initial testing wood texture
    // public domain, see http://www.publicdomainpictures.net/view-image.php?picture=wood-texture&image=1282&large=1
    // license: " 	This image is public domain. You may use this picture for any purpose, including commercial. If you do use it, please consider linking back to us. If you are going to redistribute this image online, a hyperlink to this particular page is mandatory."
    [Embed(source="../../../../../../data/density-flex/images/wood4.png")]
    private var woodClass:Class;
    
    [Embed(source="../../../../../../data/density-flex/images/lead.jpg")]
    private var leadClass:Class;
    
    private var frontMaterial:MovieMaterial;
    private var sideMaterial:BitmapMaterial;

    private var textureBitmap:Bitmap; // the texture being used (wood bitmap, wall (custom) bitmap, etc.)
    private var label:StringProperty;

    public function BlockNode(block:Block, view:AbstractDensityModule, label:StringProperty):void {
        super(block);

        this.label = label;
        this.block = block;
        this.view = view;

        // TODO: determine reliance on the starting size of the block, so that the block can be scaled in size effectively

        frontSprite = new Sprite();
        textureHolder = new Sprite();
        frontSprite.addChild(textureHolder);
        frontSprite.addChild(textField);

        label.addListener(updateText);
        block.addSubstanceListener(updateSubstance);

        updateSubstance();
        updateShape();
    }

    private function getCustomBitmap():Bitmap {
        var wallData:BitmapData = (new wallClass() as BitmapAsset).bitmapData;
        var imageRect:Rectangle = new Rectangle(0, 0, wallData.width, wallData.height);
        wallData.colorTransform(imageRect, new ColorTransform(1.0, 0.5, 0.5));
        var coloredData:BitmapData = (new wallClass() as BitmapAsset).bitmapData;
        if (block.getColor().redMultiplier < 0.5) {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.GREEN, BitmapDataChannel.RED);
        }
        else {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.RED, BitmapDataChannel.RED);
        }
        if (block.getColor().greenMultiplier < 0.5) {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.GREEN, BitmapDataChannel.GREEN);
        }
        else {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.RED, BitmapDataChannel.GREEN);
        }
        if (block.getColor().blueMultiplier < 0.5) {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.GREEN, BitmapDataChannel.BLUE);
        }
        else {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.RED, BitmapDataChannel.BLUE);
        }

        return new Bitmap(coloredData);
    }

    override protected function createCube():PickableCube {
        const cube:PickableCube = super.createCube();
        cube.cubeMaterials.left = cube.cubeMaterials.right = cube.cubeMaterials.top = cube.cubeMaterials.bottom = cube.cubeMaterials.front = sideMaterial;

        cube.cubeMaterials.back = frontMaterial;
        return cube;
    }

    private function createTextFormat(newSize:Number):TextFormat {
        trace("newsivze = "+newSize);
        var format:TextFormat = new TextFormat();
        format.size = newSize;
        format.bold = true;
        format.font = "Arial";
        return format;
    }

    public function getBlock():Block {
        return block;
    }

    override public function remove():void {
        view.removeObject(this);
    }

    private function updateText():void {
        textField.text = label.value;
        textField.height = textureBitmap.bitmapData.height;
        textField.width = textureBitmap.bitmapData.width;
        textField.multiline = true;
        updateShape();
    }

    private function updateSubstance():void {

        // remove the old texture if we have one
        if (textureBitmap != null) {
            textureHolder.removeChild(textureBitmap);
        }

        // update the bitmap we use as a background
        if (block.getSubstance() == Substance.WOOD) {
            textureBitmap = new woodClass();
        } else if (block.getSubstance() == Substance.LEAD){
            textureBitmap = new leadClass();
        }   else{
            textureBitmap = getCustomBitmap();
        }

        // add the new bitmap
        textureHolder.addChild(textureBitmap);

        // update the text field
        updateText();

        frontMaterial = new MovieMaterial(frontSprite);
        frontMaterial.smooth = true; //makes the font smooth instead of jagged, see http://www.mail-archive.com/away3d-dev@googlegroups.com/msg06699.html

        sideMaterial = new BitmapMaterial(textureBitmap.bitmapData);

        // TODO: possibly change tiling for textures that are not symmetric
        var cube:PickableCube = getCube();
        cube.cubeMaterials.left = cube.cubeMaterials.right = cube.cubeMaterials.top = cube.cubeMaterials.bottom = cube.cubeMaterials.front = sideMaterial;
        cube.cubeMaterials.back = frontMaterial;

        //make block semi-transparent
        //        redWallMaterial.alpha = 0.5;
        //        frontSprite.alpha = 0.5;
    }

    override protected function updateShape():void {
        super.updateShape();
        textField.setTextFormat(createTextFormat(45 * (200 / getCube().width)));
    }
}
}