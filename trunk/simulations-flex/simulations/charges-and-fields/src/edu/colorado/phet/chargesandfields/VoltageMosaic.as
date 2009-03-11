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

public class VoltageMosaic extends Sprite {
    private var myWidth : Number;
    private var myHeight : Number;

    private var model : Model;

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

        var step : Number = 10;

        for(var ox : Number = 0; ox < myWidth; ox += step) {
            for(var oy : Number = 0; oy < myHeight; oy += step) {
                var color : uint = model.getV(ox + step / 2, oy + step / 2)[1];
                this.graphics.beginFill(color);
                this.graphics.drawRect(ox, oy, ox + step, oy + step);
                this.graphics.endFill();
            }
        }
    }
}
}