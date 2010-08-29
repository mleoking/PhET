package edu.colorado.phet.densityandbuoyancy.view {
import away3d.sprites.Sprite2D;

import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.BitmapData;
import flash.geom.Matrix;
import flash.text.TextField;
import flash.text.TextFormat;

public class Bottle extends Sprite2D {
    public const height:Number = 0.3;//height should be about 30cm
    [Embed(source="../../../../../../data/density-and-buoyancy/images/bottle.png")]
    private var imageClass:Class;

    public function Bottle() {
        const bitmapData:BitmapData = createBitmapData();
        super(bitmapData);
        smooth = true;
        scaling = 1.0 / bitmapData.height * DensityModel.DISPLAY_SCALE * height;//Make sure the height is correct.
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