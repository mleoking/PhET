package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.*;

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

public class BlockNode extends CuboidNode implements Pickable, Listener{

    private var frontSprite : Sprite;
    private var block : Block;
    private var view : DensityView3D;

    [Embed(source="../../../../../../data/density-flex/images/wall.jpg")]
    private var wallClass : Class;

    public function BlockNode( block:Block, view : DensityView3D ) : void {
        super(block);

        this.block = block;
        this.view = view;

        // TODO: determine reliance on the starting size of the block, so that the block can be scaled in size effectively

        frontSprite = new Sprite();

        var wallData : BitmapData = (new wallClass() as BitmapAsset).bitmapData;
        var imageRect : Rectangle = new Rectangle(0, 0, wallData.width, wallData.height);
        wallData.colorTransform(imageRect, new ColorTransform(1.0, 0.5, 0.5));
        var coloredData : BitmapData = (new wallClass() as BitmapAsset).bitmapData;
        if ( block.getColor().redMultiplier < 0.5 ) {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.GREEN, BitmapDataChannel.RED);
        }
        else {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.RED, BitmapDataChannel.RED);
        }
        if ( block.getColor().greenMultiplier < 0.5 ) {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.GREEN, BitmapDataChannel.GREEN);
        }
        else {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.RED, BitmapDataChannel.GREEN);
        }
        if ( block.getColor().blueMultiplier < 0.5 ) {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.GREEN, BitmapDataChannel.BLUE);
        }
        else {
            coloredData.copyChannel(wallData, imageRect, new Point(0, 0), BitmapDataChannel.RED, BitmapDataChannel.BLUE);
        }

        frontSprite.addChild(new Bitmap(coloredData));

        var cube : PickableCube = getCube();

        var tf : TextField = new TextField();
        tf.text = String(block.getMass()) + " kg";
        tf.height = wallData.height;
        tf.width = wallData.width;
        var format : TextFormat = new TextFormat();
        format.size = int(45 * (200 / cube.width));
        format.bold = true;
        format.font = "Arial";
        tf.multiline = true;
        tf.setTextFormat(format);
        frontSprite.addChild(tf);


        var frontMaterial : MovieMaterial = new MovieMaterial(frontSprite);
        frontMaterial.smooth = true; //makes the font smooth instead of jagged, see http://www.mail-archive.com/away3d-dev@googlegroups.com/msg06699.html
        var redWallMaterial : BitmapMaterial = new BitmapMaterial(coloredData);

        cube.cubeMaterials.left = cube.cubeMaterials.right = cube.cubeMaterials.top = cube.cubeMaterials.bottom = cube.cubeMaterials.front = redWallMaterial;

        cube.cubeMaterials.back = frontMaterial;
    }

    public function getBlock() : Block {
        return block;
    }

    override public function remove():void {
        view.removeObject( this );
    }
}
}