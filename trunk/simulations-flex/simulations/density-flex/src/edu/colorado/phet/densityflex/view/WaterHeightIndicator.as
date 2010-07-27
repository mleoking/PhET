package edu.colorado.phet.densityflex.view {
import away3d.containers.ObjectContainer3D;
import away3d.sprites.MovieClipSprite;

import edu.colorado.phet.densityflex.model.DensityModel;

import flash.display.Sprite;
import flash.text.TextField;

public class WaterHeightIndicator extends ObjectContainer3D {
    public function WaterHeightIndicator(model:DensityModel) {
        super();
        var sprite:Sprite = new Sprite();
        var textField:TextField = new TextField();
//        textField.text = "hello";
        sprite.addChild( textField );
        addChild( new MovieClipSprite( sprite ) );

        sprite.graphics.beginFill(0xFF0000);
        sprite.graphics.moveTo(0,0);
        sprite.graphics.lineTo(-10,-10);
        sprite.graphics.lineTo(-10,10);
        sprite.graphics.lineTo(0,0);
        sprite.graphics.endFill();

//        model.addWaterHeightListener();
    }

    function setIndicatorPoint(x:Number, y:Number, z:Number):void {
//        this.x=x;
        this.y=y;
        this.z=z;
    }
}
}