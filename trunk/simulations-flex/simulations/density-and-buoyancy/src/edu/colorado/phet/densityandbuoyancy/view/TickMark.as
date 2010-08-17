package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.HoverCamera3D;
import away3d.containers.View3D;
import away3d.core.base.Vertex;
import away3d.core.draw.ScreenVertex;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class TickMark extends Sprite {
    private var textField:TextField;
    private var waterHeight:Number;
    private var model:DensityModel;
    private var value:Number;

    public function TickMark(model:DensityModel, value:Number) {
        super();
        this.value = value;
        this.model = model;
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.RIGHT;
        textField.text = "hello";
        addChild(textField);

        textField.selectable = false;
        this.visible = false;//show after locaiton is correct
        update();
    }

    protected function update():void {
        graphics.clear();
        var indicatedVolume:Number = value;

        //Convert SI to cm^3
        //        var readout:Number = indicatedVolume * 1E6;

        var readout:Number = DensityConstants.metersToLitersCubed(indicatedVolume);

        textField.text = String(readout.toFixed(2));// + " L";
        var textFormat:TextFormat = new TextFormat();
        textFormat.size = 14;
        textFormat.bold = true;
        textField.setTextFormat(textFormat);
        graphics.beginFill(0x000000);
        var x:Number = 0;
        graphics.moveTo(0 + x, 0);
        graphics.lineTo(+10 + x, -2);
        graphics.lineTo(+10 + x, 2);
        graphics.lineTo(0 + x, 0);
        graphics.endFill();

        textField.x = + 10;
        textField.y = -textField.height / 2;

        //        graphics.lineStyle(1, 0x000000);
        //        graphics.beginFill(0xFFFFFF);
        //        graphics.drawRoundRect(textField.x, textField.y, textField.width, textField.height, 2, 2);
        //        graphics.endFill();
    }


    public function updateCoordinates(camera:HoverCamera3D, groundNode:GroundNode, view:View3D):void {
        var height = value / model.getPoolDepth() / model.getPoolWidth();
        var screenVertex:ScreenVertex = camera.screen(groundNode, new Vertex(model.getPoolWidth() * DensityModel.DISPLAY_SCALE / 2, (-model.getPoolHeight() + height) * DensityModel.DISPLAY_SCALE, -20));
        this.x = screenVertex.x + view.x;
        this.y = screenVertex.y + view.y;
        this.visible = true;//Now can show the water volume indicator after it is at the right location
    }
}
}