package edu.colorado.phet.densityflex.view {
import away3d.materials.*;

import edu.colorado.phet.densityflex.model.Block;
import edu.colorado.phet.densityflex.model.Listener;

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
    private var block:Block;
    private var view:DensityView;
    private var textField:TextField = new TextField();

    [Embed(source="../../../../../../data/density-flex/images/wall.jpg")]
    private var wallClass:Class;
    private var frontMaterial:MovieMaterial;
    private var redWallMaterial:BitmapMaterial;

    public function BlockNode(block:Block, view:DensityView):void {
        super(block);

        this.block = block;
        this.view = view;

        // TODO: determine reliance on the starting size of the block, so that the block can be scaled in size effectively

        frontSprite = new Sprite();

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

        frontSprite.addChild(new Bitmap(coloredData));

        var cube:PickableCube = getCube();

        textField.text = String(block.getMass()) + " kg";
        textField.height = wallData.height;
        textField.width = wallData.width;
        textField.multiline = true;
        textField.setTextFormat(createTextFormat(45 * (200 / cube.width)));
        frontSprite.addChild(textField);

        frontMaterial = new MovieMaterial(frontSprite);
        frontMaterial.smooth = true; //makes the font smooth instead of jagged, see http://www.mail-archive.com/away3d-dev@googlegroups.com/msg06699.html
        redWallMaterial = new BitmapMaterial(coloredData);

        cube.cubeMaterials.left = cube.cubeMaterials.right = cube.cubeMaterials.top = cube.cubeMaterials.bottom = cube.cubeMaterials.front = redWallMaterial;

        cube.cubeMaterials.back = frontMaterial;
        //make block semi-transparent
        //        redWallMaterial.alpha = 0.5;
        //        frontSprite.alpha = 0.5;
    }

    override protected function createCube():PickableCube {
        const cube:PickableCube = super.createCube();
        cube.cubeMaterials.left = cube.cubeMaterials.right = cube.cubeMaterials.top = cube.cubeMaterials.bottom = cube.cubeMaterials.front = redWallMaterial;

        cube.cubeMaterials.back = frontMaterial;
        return cube;
    }

    private function createTextFormat(newSize:Number):TextFormat {
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
}
}