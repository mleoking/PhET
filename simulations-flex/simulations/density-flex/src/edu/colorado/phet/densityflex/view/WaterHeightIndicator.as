package edu.colorado.phet.densityflex.view {
import edu.colorado.phet.densityflex.model.DensityModel;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class WaterHeightIndicator extends Sprite {
    private var textField:TextField;
    private var waterHeight:Number;
    private var model:DensityModel;

    public function WaterHeightIndicator(model:DensityModel) {
        super();
        this.model=model;
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.RIGHT;
        textField.text = "hello";
        addChild(textField);

        textField.selectable = false;
        update();
    }

    function update():void {
        graphics.clear();
        var indicatedVolume:Number = waterHeight * model.getPoolWidth() * model.getPoolDepth();
        //Convert SI to cm^3
        var cm3:Number = indicatedVolume * 1E6;
        textField.text= String(cm3.toFixed(3))+" cm\u00b3"; 
        var textFormat:TextFormat = new TextFormat();
        textFormat.size=16;
        textFormat.bold=true;
        textField.setTextFormat(textFormat);
        graphics.beginFill(0xFF0000);
        graphics.moveTo(0, 0);
        graphics.lineTo(-10, -10);
        graphics.lineTo(-10, 10);
        graphics.lineTo(0, 0);
        graphics.endFill();

        textField.x = -textField.width - 10;
        textField.y = -textField.height / 2;

        graphics.lineStyle(1, 0x000000);
        graphics.beginFill(0xFFFFFF);
        graphics.drawRoundRect(textField.x, textField.y, textField.width, textField.height, 6, 6);
        graphics.endFill();
    }

    function setWaterHeight(waterHeight:Number):void {
        this.waterHeight = waterHeight;
        update();
    }
}
}