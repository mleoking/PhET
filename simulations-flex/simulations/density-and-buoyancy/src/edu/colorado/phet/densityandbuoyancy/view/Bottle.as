package edu.colorado.phet.densityandbuoyancy.view {
import away3d.materials.ColorMaterial;
import away3d.materials.MovieMaterial;
import away3d.sprites.MovieClipSprite;
import away3d.sprites.Sprite2D;

import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.display.Sprite;
import flash.geom.Matrix;
import flash.text.TextField;
import flash.text.TextFormat;

public class Bottle extends MyMesh{
    [Embed(source="../../../../../../data/density-and-buoyancy/images/bottle.png")]
    private var imageClass:Class;
    const bitmapData:BitmapData = createBitmapData();
    
    public const bottleHeight:Number = 0.3*DensityModel.DISPLAY_SCALE;//height should be about 30cm
    public const bottleWidth:Number = (bottleHeight*bitmapData.width)/bitmapData.height; 

    public function Bottle() {
        
        // front earth vertices
        var BOTTOM_LEFT:Number = v(0,0, 0);
        var BOTTOM_RIGHT:Number = v(bottleWidth,0, 0);
        var TOP_LEFT:Number = v(0,bottleHeight, 0);
        var TOP_RIGHT:Number = v(bottleWidth,bottleHeight, 0);
        var sprite = new Sprite();
        sprite.addChild(new Bitmap(bitmapData));
        uv(0,0);
        uv(1,0);
        uv(0,1);
        uv(1,1);
        const movieMaterial:MovieMaterial = new MovieMaterial(sprite);
        movieMaterial.smooth=true;
        plane(BOTTOM_LEFT,BOTTOM_RIGHT,TOP_RIGHT,TOP_LEFT, movieMaterial);
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