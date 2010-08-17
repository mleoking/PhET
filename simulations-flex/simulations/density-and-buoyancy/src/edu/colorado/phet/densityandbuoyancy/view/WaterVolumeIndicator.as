package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;

import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class WaterVolumeIndicator extends Sprite {
    private var textField:TextField;
    private var waterHeight:Number;
    private var model:DensityModel;

    public function WaterVolumeIndicator(model:DensityModel) {
        super();
        this.model = model;
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.RIGHT;
        textField.text = "";
        addChild(textField);

        textField.selectable = false;
        update();
    }

    protected function update():void {
        graphics.clear();
        var indicatedVolume:Number = waterHeight * model.getPoolWidth() * model.getPoolDepth();

        //Convert SI to cm^3
        //        var readout:Number = indicatedVolume * 1E6;

        var readout:Number = DensityConstants.metersToLitersCubed(indicatedVolume);

        textField.text = FlexSimStrings.get("properties.volumeValue","{0} L",[String(readout.toFixed(2))]);
        var textFormat:TextFormat = new TextFormat();
        textFormat.size = 24;
        textFormat.bold = true;
        textField.setTextFormat(textFormat);
        graphics.beginFill(0xFF0000);
        graphics.moveTo(0, 0);
        graphics.lineTo(+10, -10);
        graphics.lineTo(+10, 10);
        graphics.lineTo(0, 0);
        graphics.endFill();

        textField.x = + 10;
        textField.y = -textField.height / 2;

        graphics.lineStyle(1, 0x000000);
        graphics.beginFill(0xFFFFFF);
        graphics.drawRoundRect(textField.x, textField.y, textField.width, textField.height, 6, 6);
        graphics.endFill();
    }

    public function setWaterHeight(waterHeight:Number):void {
        this.waterHeight = waterHeight;
        update();
    }
}
}