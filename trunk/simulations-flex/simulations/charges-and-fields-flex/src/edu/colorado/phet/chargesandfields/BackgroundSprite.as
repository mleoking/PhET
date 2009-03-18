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

public class BackgroundSprite extends Sprite {
    private var myWidth : Number;
    private var myHeight : Number;

    public function BackgroundSprite(w : Number, h : Number) {
        myWidth = w;
        myHeight = h;

        drawBackground();
    }

    public function changeSize(w : Number, h : Number) : void {
        myWidth = w;
        myHeight = h;
        drawBackground();
    }

    public function drawBackground() : void {
        this.graphics.beginFill(0xFFFFFF);
        this.graphics.drawRect(0, 0, myWidth, myHeight);
        this.graphics.endFill();
    }

}
}
