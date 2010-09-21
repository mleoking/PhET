package edu.colorado.phet.densityandbuoyancy.view.away3d {
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.view.away3d.SpriteFace;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.display.Sprite;
import flash.geom.Matrix;
import flash.text.TextField;
import flash.text.TextFormat;

public class Bottle extends SpriteFace {
    [Embed(source="../../../../../../../data/density-and-buoyancy/images/bottle.png")]
    private var imageClass:Class;

    public function Bottle() {
        var sprite:Sprite = new Sprite();
        sprite.addChild(new Bitmap(createBitmapData()));
        sprite.scaleX = 0.3 * DensityModel.DISPLAY_SCALE / sprite.height;
        sprite.scaleY = 0.3 * DensityModel.DISPLAY_SCALE / sprite.height;
        super(sprite);
    }

    private function createBitmapData():BitmapData {
        var data:BitmapData = (new imageClass()).bitmapData;

        var textField:TextField = new TextField();
        textField.text = FlexSimStrings.get("bottle.2L", "2L");

        var format:TextFormat = new TextFormat();
        format.size = 64;
        format.bold = true;
        textField.setTextFormat(format);
        textField.multiline = false;
        textField.textColor = 0xffffff;

        const matrix:Matrix = new Matrix();
        matrix.tx = data.width / 2 - textField.textWidth / 2;
        matrix.ty = data.height / 2 - textField.textHeight / 2;
        data.draw(textField, matrix, null, null, null, true);
        return data;
    }
}
}