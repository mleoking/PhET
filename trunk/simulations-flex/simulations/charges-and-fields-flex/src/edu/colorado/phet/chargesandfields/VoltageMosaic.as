package edu.colorado.phet.chargesandfields {

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.display.Stage
import flash.display.StageAlign;
import flash.display.StageScaleMode;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.ColorTransform;
import flash.text.TextField;
import flash.ui.Keyboard;
import mx.events.SliderEvent;
import mx.controls.sliderClasses.Slider;


public class VoltageMosaic extends Sprite {
    private var myWidth : Number;
    private var myHeight : Number;

    private var model : Model;

    [Bindable]
    public var fps : String = "FPS";

    [Bindable]
    public var step : Number = 10;

    public function VoltageMosaic(model : Model, w : Number, h : Number) {
        this.model = model;
        myWidth = w;
        myHeight = h;

        draw();
    }

    public function changeSize(w : Number, h : Number) : void {
        myWidth = w;
        myHeight = h;
        draw();
    }

    public function draw() : void {
        this.graphics.clear();

        //var step : Number = 10;

        var halfstep : Number = step * 0.5;

        var time : Number = (new Date()).valueOf();
        for(var ox : Number = 0; ox < myWidth; ox += step) {
            for(var oy : Number = 0; oy < myHeight; oy += step) {
                // 30ms for getV total
                //var color : uint = model.getV(ox + halfstep, oy + halfstep)[1];
                var color : int = model.getVColor(ox + halfstep, oy + halfstep);

                // 10 ms for graphics total
                this.graphics.beginFill(color);
                this.graphics.drawRect(ox, oy, ox + step, oy + step);
                this.graphics.endFill();
            }
        }

        fps = String( int( 1000 / ((new Date()).valueOf() - time) ) );
    }

    public function changeStepSize( event : SliderEvent ) : void {
        step = event.value;
        draw();
    }
}
}